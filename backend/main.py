from typing import List
from fastapi import FastAPI, Form, UploadFile, File, Query
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

import shutil
import os
import csv
import uuid

from datetime import datetime
from detector import detect_objects as detect_yolo
from detector_tf import detect_objects_tf
from detector_frcnn import detect_objects_frcnn
from detector_ssd import detect_objects_ssd
from database import insert_detection, create_table

from mongo_utils import insert_detection_mongo
from mongo_queries import find_detections

# Datenbanktabelle erstellen
create_table()

app = FastAPI()
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])

def log_detections(image_filename: str, model_name: str, detections: list, duration: float):
    log_file = "detections.csv"
    file_exists = os.path.isfile(log_file)

    with open(log_file, mode="a", newline="", encoding="utf-8") as file:
        writer = csv.writer(file)
        if not file_exists:
            writer.writerow(["timestamp", "image", "model", "label", "confidence", "x1", "y1", "x2", "y2", "duration_ms"])
        for det in detections:
            writer.writerow([
                datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
                image_filename,
                model_name,
                det["label"],
                round(det["confidence"], 2),
                round(det["bbox"][0]),
                round(det["bbox"][1]),
                round(det["bbox"][2]),
                round(det["bbox"][3]),
                round(duration, 2)
            ])

@app.post("/detect")
async def detect(file: UploadFile = File(...)):
    temp_path = f"temp_{file.filename}"
    with open(temp_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        result = detect_yolo(temp_path)
        print("→ Yolov8n wird verwendet.")
        print("→ Erkennungen:", result)
        log_detections(file.filename, "yolov8n", result)
        return JSONResponse(content={"detections": result})
    finally:
        os.remove(temp_path)

@app.post("/detect_tf")
async def detect_tf_api(file: UploadFile = File(...)):
    temp_path = f"temp_tf_{file.filename}"
    with open(temp_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        result, duration = detect_objects_tf(temp_path)
        print("→ MobileNetV2 wird verwendet.")
        print("→ Erkennungen:", result)
        print("→ Dauer:", duration, "ms")
        log_detections(file.filename, "MobileNetV2", result)
        return JSONResponse(content={"detections": result})
    finally:
        os.remove(temp_path)

@app.post("/detect_frcnn")
async def detect_frcnn_api(file: UploadFile = File(...)):
    temp_path = f"temp_frcnn_{file.filename}"
    with open(temp_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        result, duration = detect_objects_frcnn(temp_path)
        print("→ FASTER_R_CNN wird verwendet.")
        print("→ Erkennungen:", result)
        print("→ Dauer:", duration, "ms")
        log_detections(file.filename, "FASTER_R_CNN", result)
        return JSONResponse(content={"detections": result})
    finally:
        os.remove(temp_path)

@app.post("/detect_ssd")
async def detect_ssd_api(file: UploadFile = File(...)):
    contents = await file.read()
    temp_path = f"temp_{file.filename}"
    with open(temp_path, "wb") as f:
        f.write(contents)

    try:
        result, duration = detect_objects_ssd(temp_path)
        print("→ SSD wird verwendet.")
        print("→ Erkennungen:", result)
        print("→ Dauer:", duration, "ms")

        # SSD-Ergebnisstruktur konvertieren
        log_detections(file.filename, "SSD", result)
        return JSONResponse(content={"detections": result})
    finally:
        os.remove(temp_path)

@app.post("/detect_batch")
async def detect_batch(
    files: List[UploadFile] = File(...),
    model: str = Form("yolo")
):
    results = []
    timestamp = datetime.now().isoformat()

    for file in files:
        temp_filename = f"temp_{uuid.uuid4().hex}_{file.filename}"
        with open(temp_filename, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # Modell auswählen
        if model == "mobilenet":
            detections, _ = detect_objects_tf(temp_filename)
        elif model == "frcnn":
            detections, _ = detect_objects_frcnn(temp_filename)
        elif model == "ssd":
            raw_detections, _ = detect_objects_ssd(temp_filename)
            detections = [{"label": d["label"], "confidence": d["score"], "bbox": d["box"]} for d in raw_detections]
        else:
            detections = detect_yolo(temp_filename)

        for det in detections:
            insert_detection(
                filename=file.filename,
                label=det["label"],
                confidence=det["confidence"],
                bbox=det["bbox"],
                model=model,
                timestamp=timestamp
            )
        # MongoDB-Logging
        insert_detection_mongo(
            filename=file.filename,
            model=model,
            detections=detections,
            timestamp=timestamp
        )
        # CSV-Logging
        log_detections(file.filename, model, detections)

        results.append({
            "filename": file.filename,
            "detections": detections,
            "timestamp": timestamp
        })

        os.remove(temp_filename)

    return JSONResponse(content={"batch_results": results})

@app.get("/search")
async def search_detections(
    label: str = Query(None),
    model: str = Query(None)
):
    results = find_detections(label, model)
    return JSONResponse(content={"results": results})

# Batch-Detection für alle Modelle
@app.post("/detect_batch_all")
async def detect_batch_all(files: List[UploadFile] = File(...)):
    results = []
    timestamp = datetime.now().isoformat()

    for file in files:
        clean_filename = os.path.basename(file.filename)
        temp_filename = f"temp_{uuid.uuid4().hex}_{clean_filename}"
        with open(temp_filename, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        file_results = {
            "filename": clean_filename,
            "timestamp": timestamp,
            "detections": {}
        }

        # YOLO
        yolo_detections, yolo_duration = detect_yolo(temp_filename)
        log_detections(clean_filename, "yolov8n", yolo_detections, yolo_duration)
        insert_detection_mongo(clean_filename, "yolov8n", yolo_detections, timestamp)
        file_results["detections"]["yolo"] = yolo_detections

        # MobileNetV2
        mobilenet_detections, mobilenet_duration = detect_objects_tf(temp_filename)
        log_detections(clean_filename, "mobilenetv2", mobilenet_detections, mobilenet_duration)
        insert_detection_mongo(clean_filename, "mobilenetv2", mobilenet_detections, timestamp)
        file_results["detections"]["mobilenet"] = mobilenet_detections

        # Faster R-CNN
        frcnn_detections, frcnn_duration = detect_objects_frcnn(temp_filename)
        log_detections(clean_filename, "faster_r_cnn", frcnn_detections, frcnn_duration)
        insert_detection_mongo(clean_filename, "faster_r_cnn", frcnn_detections, timestamp)
        file_results["detections"]["frcnn"] = frcnn_detections

        # SSD
        ssd_detections, ssd_duration = detect_objects_ssd(temp_filename)
        log_detections(clean_filename, "ssd", ssd_detections, ssd_duration)
        insert_detection_mongo(clean_filename, "ssd", ssd_detections, timestamp)
        file_results["detections"]["ssd"] = ssd_detections

        results.append(file_results)
        os.remove(temp_filename)

    return JSONResponse(content={"batch_results": results})


