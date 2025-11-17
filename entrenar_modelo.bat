@echo off
echo ==========================================
echo   ENTRENAMIENTO DEL MODELO DE IA
echo ==========================================
echo.

echo Verificando instalacion de Python...
python --version
if %ERRORLEVEL% NEQ 0 (
    echo Error: Python no esta instalado
    pause
    exit /b 1
)

echo.
echo Verificando dependencias...
pip show ultralytics >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Instalando dependencias...
    pip install -r requirements.txt
)

echo.
echo Iniciando entrenamiento del modelo...
echo Esto puede tomar varios minutos...
echo.

python scripts_ia\entrenamiento.py

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==========================================
    echo   ENTRENAMIENTO COMPLETADO
    echo ==========================================
) else (
    echo.
    echo Error durante el entrenamiento
)

pause
