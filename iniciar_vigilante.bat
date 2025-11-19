@echo off
echo   INICIANDO CLIENTE VIGILANTE
echo.

if not exist "cliente_vigilante\bin" mkdir cliente_vigilante\bin

echo Compilando archivos Java...
cd cliente_vigilante\src\main\java
javac -d ..\..\..\bin com\vigilante\*.java com\vigilante\modelos\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Error en compilacion
    cd ..\..\..\..
    exit /b 1
)

echo Compilacion exitosa
echo.
echo Iniciando interfaz grafica...
cd ..\..\..\..

java -cp cliente_vigilante\bin com.vigilante.ClienteVigilante
