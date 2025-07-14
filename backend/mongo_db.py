from pymongo import MongoClient

def get_db():
    client = MongoClient("mongodb://localhost:27017/")  # oder dein Atlas-URL
    db = client["img_analyzer"]  # Name der Datenbank
    return db
