from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from detector import detect_objects
import shutil
import os
import csv
from datetime import datetime
from detector_tf import detect_objects_tf
from detector_frcnn import detect_objects_frcnn

app = FastAPI()
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])

def log_detections(image_filename: str, model_name: str, detections: list):
    log_file = "detections.csv"
    file_exists = os.path.isfile(log_file)

    with open(log_file, mode="a", newline="", encoding="utf-8") as file:
        writer = csv.writer(file)
        if not file_exists:
            writer.writerow(["timestamp", "image", "model", "label", "confidence", "x1", "y1", "x2", "y2"])
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
                round(det["bbox"][3])
            ])

@app.post("/detect")
async def detect(file: UploadFile = File(...)):
    temp_path = f"temp_{file.filename}"
    with open(temp_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        result = detect_objects(temp_path)
        print("→ Yolov8n wird verwendet.")
        print("→ Erkennungen:", result)
        # CSV-Logging aktivieren
        log_detections(file.filename, "yolov8n", result)
        return JSONResponse(content={"detections": result})
    finally:
        os.remove(temp_path)

@app.post("/detect_tf")
async def detect_tf(file: UploadFile = File(...)):
    temp_path = f"temp_tf_{file.filename}"
    with open(temp_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        result, duration = detect_objects_tf(temp_path)
        print("→ MobileNetV2 wird verwendet.")
        print("→ Erkennungen:", result)
        print("→ Dauer:", duration, "ms")
        return JSONResponse(content={"detections": result})
    finally:
        os.remove(temp_path)

@app.post("/detect_frcnn")
async def detect_frcnn(file: UploadFile = File(...)):
    temp_path = f"temp_frcnn_{file.filename}"
    with open(temp_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        result, duration = detect_objects_frcnn(temp_path)
        print("→ FASTER_R_CNN wird verwendet.")
        print("→ Erkennungen:", result)
        print("→ Dauer:", duration, "ms")
        return JSONResponse(content={"detections": result})
    finally:
        os.remove(temp_path)