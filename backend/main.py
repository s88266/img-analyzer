from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])

@app.post("/detect")
async def detect(file: UploadFile = File(...)):
    # TODO: hier später YOLO etc. aufrufen
    return {"tags": ["Person", "Hund"]}
