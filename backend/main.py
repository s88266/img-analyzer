from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from detector import detect_objects
import shutil
import os

app = FastAPI()
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])

@app.post("/detect")
async def detect(file: UploadFile = File(...)):
    #temporäres Bild speichern
    temp_path = f"temp_{file.filename}"
    with open(temp_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        #Bild analysieren
        result = detect_objects(temp_path)
        return JSONResponse(content={"detections": result})
    finally:
        os.remove(temp_path)
    # return {"tags": ["Person", "Hund"]}
