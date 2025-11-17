@echo off
echo ==========================================
echo   COMPILACION COMPLETA DEL PROYECTO
echo ==========================================
echo.

echo [1/3] Compilando Servidor Central...
cd servidor
if not exist "bin" mkdir bin
cd src\main\java
javac -d ..\..\..\bin com\sistema\*.java com\sistema\servidores\*.java com\sistema\modelos\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo compilacion del servidor
    cd ..\..\..\..\
    pause
    exit /b 1
)
cd ..\..\..\..\
echo [OK] Servidor compilado

echo.
echo [2/3] Compilando Cliente Vigilante...
cd cliente_vigilante
if not exist "bin" mkdir bin
cd src\main\java
javac -d ..\..\..\bin com\vigilante\*.java com\vigilante\modelos\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo compilacion del cliente vigilante
    cd ..\..\..\..\
    pause
    exit /b 1
)
cd ..\..\..\..\
echo [OK] Cliente Vigilante compilado

echo.
echo [3/3] Verificando dependencias Python...
python --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ADVERTENCIA: Python no encontrado
) else (
    pip show ultralytics >nul 2>&1
    if %ERRORLEVEL% NEQ 0 (
        echo Instalando dependencias Python...
        pip install -r requirements.txt
    ) else (
        echo [OK] Dependencias Python instaladas
    )
)

echo.
echo ==========================================
echo   COMPILACION COMPLETADA
echo ==========================================
echo.
echo Componentes compilados:
echo - Servidor Central (Java)
echo - Cliente Vigilante (Java)
echo.
echo Para Android:
echo - Importar carpeta android/ en Android Studio
echo.
echo Siguiente pasos:
echo 1. Ejecutar: iniciar_servidor.bat
echo 2. Ejecutar: iniciar_vigilante.bat
echo 3. Abrir Android Studio e importar carpeta android/
echo.
pause
