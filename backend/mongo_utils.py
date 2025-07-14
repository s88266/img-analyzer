
from datetime import datetime
from pymongo import MongoClient

def get_db():
    client = MongoClient("mongodb://localhost:27017/")
    return client["img_analyzer"]

def insert_detection_mongo(filename, model, detections, timestamp=None):
    db = get_db()
    collection = db["detections"]
    doc = {
        "filename": filename,
        "model": model,
        "detections": detections,
        "timestamp": timestamp or datetime.utcnow().isoformat()
    }
    collection.insert_one(doc)
