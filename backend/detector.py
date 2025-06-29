from ultralytics import YOLO

# YOLOv8-Modell laden (vorgefertigt)
model = YOLO("yolov8n.pt")  # "n" steht für "nano" (klein und schnell)

def detect_objects(image_path: str) -> list:
    results = model(image_path)
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
    return detections
