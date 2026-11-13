Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "Iniciando Servidor Web Libreria El Saber en http://localhost:8084" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Cyan
java -cp "build/classes;bin;lib/*" patrones.servidor.ServidorLibreriaWeb
