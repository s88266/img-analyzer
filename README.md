# KI-gestützte Bildanalyse – Bachelorarbeit Nguyen Duc Dai

Dieses Projekt implementiert einen Prototyp zur **KI-gestützten Objekterkennung** mit einem **Python-FastAPI-Backend** und einer **JavaFX-Frontend-Anwendung**.  
Unterstützte Modelle:
- **YOLOv8n**
- **Faster R-CNN**
- **SSD300-VGG16**
- **MobileNetV2**

---

## 📦 Voraussetzungen

### Backend (Python)
- Python **3.10+**
- [FastAPI](https://fastapi.tiangolo.com/)
- [Uvicorn](https://www.uvicorn.org/)
- PyTorch & TorchVision
- TensorFlow / Keras
- Ultralytics YOLOv8
- Pillow
- PyMongo (optional, für MongoDB-Funktionen)
- MongoDB (lokal oder remote, falls benötigt)

**Installation der Abhängigkeiten:**
```bash
pip install -r requirements.txt
```

---

### Frontend (Java)
- Java **17+**
- Apache Maven oder Gradle
- JavaFX-Unterstützung (z. B. über Maven-Plugin)

---

## 🚀 Startanleitung
Entweder über die run-all.bat starten oder:

### 1. Backend starten
```bash
cd backend
uvicorn main:app --reload
```
Das Backend läuft standardmäßig unter [http://127.0.0.1:8000](http://127.0.0.1:8000).  
API-Dokumentation: [http://127.0.0.1:8000/docs](http://127.0.0.1:8000/docs)

---

### 2. Frontend starten
```bash
cd frontend
cd img-analyzer-client
mvn --% exec:java -Dexec.mainClass=com.ducki.App
```
- Projekt in IntelliJ IDEA oder Eclipse importieren.
- `App.java` als JavaFX-Anwendung starten.
- Verbindung zum Backend muss aktiv sein (`http://localhost:8000`).

---

## ⚙️ Funktionsweise
1. **Upload**: Bilder werden im Frontend ausgewählt und ans Backend gesendet.
2. **Analyse**: Das Backend führt die Erkennung mit allen vier Modellen durch.
3. **Speicherung**: Ergebnisse (Labels, Bounding Boxes, Confidence, Laufzeit) werden in  
   - `detections.csv`  
   - `detections.db` (SQLite)  
   - optional MongoDB gespeichert.
4. **Anzeige**: Frontend zeigt Ergebnisse als Tabelle und mit Bounding-Box-Overlay im Bild.

---

## 📂 Projektstruktur
```
backend/                 # Python FastAPI Backend
frontend/                # JavaFX Frontend
requirements.txt         # Python-Abhängigkeiten
README.md                # Projektdokumentation
.gitignore               # Ausschlussregeln für Git
detections.csv           # Ergebnis-Log (CSV)
detections.db            # Ergebnis-Log (SQLite)
```

---

## 🛠 Hinweise
- Modelle (YOLOv8n, Faster R-CNN, SSD, MobileNetV2) werden beim ersten Start automatisch heruntergeladen.
- Für MongoDB-Funktionen muss eine lokale oder Remote-Instanz verfügbar sein.
- Große Testbilder und Datasets sind **nicht** enthalten.

---

## 📄 Lizenz
Projekt im Rahmen der Bachelorarbeit im Studiengang Medieninformatik  
Hochschule für Technik und Wirtschaft Berlin  
**Autor:** Nguyen Duc Dai – Matrikelnummer: S932816
