# KI-gestützte Bildanalyse – Bachelorarbeit Nguyen Duc Dai

Dieses Projekt implementiert einen Prototyp zur **KI-gestützten Objekterkennung** mit einem **Python-FastAPI-Backend** und einer **JavaFX-Frontend-Anwendung**.  
Unterstützte Modelle: **YOLOv8n**, **Faster R-CNN**, **SSD** und **MobileNetV2**.

---

## 📦 Voraussetzungen

### Backend (Python)
- Python **3.10+**
- [FastAPI](https://fastapi.tiangolo.com/)
- [Uvicorn](https://www.uvicorn.org/)
- PyTorch
- TensorFlow/Keras
- Ultralytics YOLOv8
- MongoDB (lokal oder remote, falls benötigt)

Backend-Abhängigkeiten installieren:
bash
pip install fastapi uvicorn torch torchvision torchaudio tensorflow keras ultralytics pillow pymongo
Frontend (Java)
Java 17+

Apache Maven
🚀 Startanleitung
1. Backend starten
In das Backend-Verzeichnis wechseln:
cd backend
Uvicorn-Server starten:
uvicorn main:app --reload
Das Backend läuft nun standardmäßig unter http://127.0.0.1:8000.

⚙️ Funktionsweise
Bild hochladen → wird an das Backend gesendet.

Backend führt Erkennung mit YOLOv8n, Faster R-CNN, SSD oder MobileNetV2 gleichzeitig durch.

Ergebnisse → Bounding Boxes + Labels werden im Frontend angezeigt und in CSV/SQLite/MongoDB gespeichert.

📂 Projektstruktur
.vscode/                     # VS Code Einstellungen
backend/                     # Python-FastAPI Backend
frontend/img-analyzer-client # JavaFX-Frontend
run-all.bat                  # Optionaler Batch-Start
.gitignore                   # Git-Ignore Datei

🛠 Hinweise
YOLOv8n und andere Modelle werden beim ersten Start automatisch heruntergeladen (Internetverbindung erforderlich).

Für MongoDB-Funktionen muss eine lokale oder Remote-Instanz verfügbar sein.

Große Testbilder/Datasets sind aus Platzgründen nicht enthalten.
