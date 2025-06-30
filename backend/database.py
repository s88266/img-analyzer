import sqlite3

def create_connection():
    conn = sqlite3.connect("detections.db")
    return conn

def create_table():
    conn = create_connection()
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS detections (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            filename TEXT,
            label TEXT,
            confidence REAL,
            x1 REAL,
            y1 REAL,
            x2 REAL,
            y2 REAL,
            model TEXT,
            timestamp TEXT
        )
    ''')
    conn.commit()
    conn.close()

def insert_detection(filename, label, confidence, bbox, model, timestamp):
    x1, y1, x2, y2 = bbox
    conn = create_connection()
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO detections (filename, label, confidence, x1, y1, x2, y2, model, timestamp)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    ''', (filename, label, confidence, x1, y1, x2, y2, model, timestamp))
    conn.commit()
    conn.close()
