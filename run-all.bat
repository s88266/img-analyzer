start cmd /k "cd backend && call venv\Scripts\activate && uvicorn main:app --reload"
start cmd /k "cd frontend\img-analyzer-client && mvn exec:java -Dexec.mainClass=com.ducki.ImageApp"
