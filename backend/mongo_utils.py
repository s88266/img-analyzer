from datetime import datetime
from pymongo import MongoClient
from typing import List

def get_db():
    client = MongoClient("mongodb://localhost:27017/")
    return client["img_analyzer"]

def insert_detection_mongo(filename: str, model: str, detections: List[dict], timestamp: str):
    try:
        db = get_db()
        collection = db["detections"] 
        doc = {
            "filename": filename,
            "model": model,
            "detections": detections,
            "timestamp": timestamp
        }
        collection.insert_one(doc)
        print(f"[MongoDB] Inserted detection for {filename} using {model}")
    except Exception as e:
        print(f"[MongoDB] Error inserting detection for {filename}: {e}")
