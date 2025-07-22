import time
from ultralytics import YOLO

model = YOLO("yolov8n.pt")  # "n" steht für "nano" (klein und schnell)

def detect_objects(image_path: str) -> list:
    print("▶ YOLOv8n wird verwendet")
    start_time = time.time()
    results = model(image_path)
    duration = (time.time() - start_time)* 1000 #ms
    detections = []
    for r in results:
        for box in r.boxes:
            label = model.names[int(box.cls)]
            conf = float(box.conf)
            bbox = box.xyxy[0].tolist()
            detections.append({
                "label": label,
                "confidence": round(conf, 2),
                "bbox": [round(coord, 2) for coord in bbox]
            })
    print(f"✅ YOLOv8n fertig in {round(duration,2)}ms, {len(detections)} Objekte gefunden")
    return detections, duration
