@echo off
echo   COMPILACION COMPLETA DEL PROYECTO
echo.

echo [1/3] Compilando Servidor Central...
cd servidor
if not exist "bin" mkdir bin
cd src\main\java
javac -d ..\..\..\bin com\sistema\*.java com\sistema\servidores\*.java com\sistema\modelos\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo compilacion del servidor
    cd ..\..\..\..\
    exit /b 1
)
cd ..\..\..\..\
echo Servidor compilado

echo.
echo Compilando Cliente Vigilante...
cd cliente_vigilante
if not exist "bin" mkdir bin
cd src\main\java
javac -d ..\..\..\bin com\vigilante\*.java com\vigilante\modelos\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo compilacion del cliente vigilante
    cd ..\..\..\..\
    exit /b 1
)
cd ..\..\..\..\
echo Cliente Vigilante compilado

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
        echo Dependencias Python instaladas
    )
)

echo.
echo   COMPILACION COMPLETADA
echo.
echo Componentes compilados:
echo - Servidor Central
echo - Cliente Vigilante
echo.
