@echo off
set DB_URL=jdbc:postgresql://localhost:5432/active_leisure
set DB_USERNAME=postgres
set DB_PASSWORD=1029
set JWT_SECRET=diplom-active-leisure-secret-key-change-in-production
set JWT_EXPIRATION_MS=86400000
set SERVER_PORT=8080
cd /d C:\Users\yuutu\IdeaProjects\Diplom
"C:\Users\yuutu\.jdks\ms-21.0.11\bin\java.exe" -jar "C:\Users\yuutu\IdeaProjects\Diplom\target\active-leisure-0.0.1-SNAPSHOT.jar" >> "C:\Users\yuutu\IdeaProjects\Diplom\backend.log" 2>&1
