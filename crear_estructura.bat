@echo off
echo ==========================================
echo   CREANDO ESTRUCTURA DE CARPETAS
echo ==========================================
echo.

echo Creando carpetas para dataset...
if not exist "dataset" mkdir dataset
if not exist "dataset\persona" mkdir dataset\persona
if not exist "dataset\perro" mkdir dataset\perro
if not exist "dataset\celular" mkdir dataset\celular
echo [OK] Carpetas de dataset creadas

echo.
echo Creando carpetas para registros...
if not exist "registros" mkdir registros
echo [OK] Carpeta de registros creada

echo.
echo Creando carpetas para evidencias...
if not exist "evidencias" mkdir evidencias
echo [OK] Carpeta de evidencias creada

echo.
echo Creando carpetas para scripts IA...
if not exist "scripts_ia\modelo_entrenado" mkdir scripts_ia\modelo_entrenado
echo [OK] Carpeta para modelo entrenado creada

echo.
echo ==========================================
echo   ESTRUCTURA CREADA EXITOSAMENTE
echo ==========================================
echo.
echo Carpetas creadas:
echo - dataset/persona/
echo - dataset/perro/
echo - dataset/celular/
echo - registros/
echo - evidencias/
echo - scripts_ia/modelo_entrenado/
echo.
pause
