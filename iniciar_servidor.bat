@echo off
echo ==========================================
echo   INICIANDO SERVIDOR CENTRAL
echo ==========================================
echo.

if not exist "servidor\bin" mkdir servidor\bin

echo Compilando archivos Java...
cd servidor\src\main\java
javac -d ..\..\..\bin com\sistema\*.java com\sistema\servidores\*.java com\sistema\modelos\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Error en compilacion
    cd ..\..\..\..
    pause
    exit /b 1
)

echo Compilacion exitosa
echo.
echo Iniciando servidor en puerto 6000...
cd ..\..\..\..

java -cp servidor\bin com.sistema.ServidorCentral

pause
