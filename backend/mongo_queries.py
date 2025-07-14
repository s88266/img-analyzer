from mongo_db import get_db

def find_detections(label: str = None, model: str = None):
    db = get_db()
    collection = db["detections"]

    query = {}
    if label:
        query["detections.label"] = label
    if model:
        query["model"] = model

    results = collection.find(query, {"_id": 0})  # kein MongoDB _id-Feld
    return list(results)
