$ErrorActionPreference = "Continue"

$root = Split-Path -Parent $PSScriptRoot
$env:DB_URL = "jdbc:postgresql://localhost:5432/active_leisure"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "1029"
$env:JWT_SECRET = "diplom-active-leisure-secret-key-change-in-production"
$env:JWT_EXPIRATION_MS = "86400000"
$env:SERVER_PORT = "8080"

$java = "C:\Users\yuutu\.jdks\ms-21.0.11\bin\java.exe"
$jar = Join-Path $root "target\active-leisure-0.0.1-SNAPSHOT.jar"
$log = Join-Path $root "backend.log"

Set-Location $root
& $java -jar $jar *>> $log
