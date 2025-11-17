# Guia de Inicio Rapido

## Pasos para ejecutar el sistema completo

### 1. Instalar dependencias de Python
```bash
pip install -r requirements.txt
```

### 2. Iniciar el Servidor Central
Doble click en `iniciar_servidor.bat` o ejecutar:
```bash
cd servidor/src/main/java
javac -d ../../../bin com/sistema/*.java com/sistema/servidores/*.java com/sistema/modelos/*.java
cd ../../../
java -cp bin com.sistema.ServidorCentral
```

### 3. Recolectar imagenes para entrenamiento
- Abrir la app Android en el dispositivo
- Configurar la IP del servidor (ejecutar `ipconfig` para obtenerla)
- Seleccionar "Modo Entrenamiento"
- Capturar al menos 20-30 imagenes de cada clase (persona, perro, celular)

### 4. Entrenar el modelo
Doble click en `entrenar_modelo.bat` o ejecutar:
```bash
python scripts_ia/entrenamiento.py
```

### 5. Activar modo vigilancia
- En la app Android, cambiar a "Modo Vigilancia"
- Configurar ID de camara (CAMARA_1, CAMARA_2, etc.)
- Click en "Iniciar Vigilancia Automatica"

### 6. Visualizar detecciones
Doble click en `iniciar_vigilante.bat` o ejecutar:
```bash
cd cliente_vigilante/src/main/java
javac -d ../../../bin com/vigilante/*.java com/vigilante/modelos/*.java
cd ../../../
java -cp bin com.vigilante.ClienteVigilante
```

## Arquitectura de Comunicacion

```
[Cliente Android 1] ---\
[Cliente Android 2] -----> [SERVIDOR CENTRAL:6000] <---> [Scripts IA Python]
[Cliente Android 3] ---/            |
                                    v
                         [Cliente Vigilante GUI]
```

## Protocolo de Mensajes

### TRAINING (Entrenamiento)
```
Cliente -> Servidor: "TRAINING" + etiqueta + tamaño + bytes_imagen
Servidor -> Cliente: "OK" + mensaje
```

### TEST (Vigilancia)
```
Cliente -> Servidor: "TEST" + id_camara + tamaño + bytes_imagen
Servidor -> Python: ejecutar inferencia
Python -> Servidor: objetos detectados
Servidor -> Cliente: "OK" + resultado
```

### GET_REGISTROS (Consulta)
```
Cliente Vigilante -> Servidor: "GET_REGISTROS"
Servidor -> Cliente Vigilante: cantidad + [registros con imagenes]
```

## Estructura de Carpetas Generadas

```
dataset/
  persona/      # Imagenes de personas
  perro/        # Imagenes de perros
  celular/      # Imagenes de celulares

evidencias/     # Capturas con detecciones
modelos/        # Modelos entrenados (.pt)
registros/      # Logs de detecciones (CSV)
temp/           # Archivos temporales
```

## Notas Importantes

1. **Puerto 6000**: Asegurar que no este bloqueado por firewall
2. **Red WiFi**: Todos los dispositivos en la misma red
3. **Dataset**: Minimo 20 imagenes por clase para buen entrenamiento
4. **Concurrencia**: El servidor maneja multiples camaras simultaneamente
5. **Hilos**: Cada conexion se procesa en un hilo independiente

## Troubleshooting

- **No se conecta**: Verificar IP y que el servidor este ejecutandose
- **No detecta**: Entrenar el modelo con mas imagenes
- **Error Python**: Instalar dependencias con `pip install -r requirements.txt`
