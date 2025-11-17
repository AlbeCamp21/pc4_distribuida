# Arquitectura del Sistema Distribuido

## Diagrama de Componentes

```
+------------------+     +------------------+     +------------------+
|  Cliente Android |     |  Cliente Android |     |  Cliente Android |
|    (CAMARA_1)    |     |    (CAMARA_2)    |     |    (CAMARA_3)    |
+------------------+     +------------------+     +------------------+
| - MainActivity   |     | - MainActivity   |     | - MainActivity   |
| - CameraX        |     | - CameraX        |     | - CameraX        |
| - Socket TCP     |     | - Socket TCP     |     | - Socket TCP     |
+--------+---------+     +--------+---------+     +--------+---------+
         |                        |                        |
         |      TCP/IP            |                        |
         |      Puerto 6000       |                        |
         +------------------------+------------------------+
                                  |
                                  v
         +------------------------------------------------+
         |          SERVIDOR CENTRAL (Java)               |
         |              Puerto 6000                       |
         +------------------------------------------------+
         | - ServidorCentral (ServerSocket)              |
         | - ManejadorCliente (Thread Pool)              |
         |                                               |
         | +-----------+  +-----------+  +-----------+  |
         | | Servidor  |  | Servidor  |  | Servidor  |  |
         | | Video     |  | Entrena.  |  | Testeo    |  |
         | +-----------+  +-----------+  +-----------+  |
         |      |              |              |         |
         +------|--------------|--------------|----------+
                |              |              |
                |              v              v
                |         +--------+    +-----------+
                |         | Dataset|    | Scripts   |
                |         | Folder |    | Python IA |
                |         +--------+    +-----------+
                |         /   |   \     | - entrena.|
                |        /    |    \    | - inferen.|
                |  persona perro celular+-----------+
                |                             |
                v                             v
         +-----------+                  +-----------+
         | Registros |                  | Modelos   |
         | CSV File  |                  | .pt Files |
         +-----------+                  +-----------+
                |
                |
                v
         +------------------------+
         | Cliente Vigilante      |
         | (Java Swing GUI)       |
         +------------------------+
         | - JTable               |
         | - ImageIcon            |
         | - Socket TCP           |
         +------------------------+
```

## Flujo de Datos

### 1. Modo Entrenamiento

```
[Cliente Android]
       |
       | 1. Captura imagen con CameraX
       v
[Bitmap -> ByteArray]
       |
       | 2. Envia via Socket TCP
       |    Protocolo: TRAINING + etiqueta + bytes
       v
[Servidor Central]
       |
       | 3. Hilo dedica procesa
       v
[ServidorEntrenamiento]
       |
       | 4. Guarda en filesystem
       v
[dataset/{etiqueta}/img_XXXX.jpg]
       |
       | 5. Responde confirmacion
       v
[Cliente Android]
       |
       v
   [Toast: OK]
```

### 2. Modo Vigilancia

```
[Cliente Android]
       |
       | 1. Timer cada 10 segundos
       v
[Captura frame automatico]
       |
       | 2. Envia via Socket TCP
       |    Protocolo: TEST + id_camara + bytes
       v
[Servidor Central]
       |
       | 3. Hilo procesa
       v
[ServidorTesteo]
       |
       | 4. Guarda temporal
       v
[temp/frame_TIMESTAMP.jpg]
       |
       | 5. Ejecuta Python
       v
[ProcessBuilder]
       |
       v
[Script inferencia.py]
       |
       | 6. Carga modelo YOLOv8
       v
[modelo_entrenado.pt]
       |
       | 7. Detecta objetos
       v
[Resultado: "Persona, Perro"]
       |
       | 8. Retorna via stdout
       v
[ServidorTesteo]
       |
       | 9. Guarda evidencia
       v
[evidencias/evidencia_XXXX.jpg]
       |
       | 10. Registra en CSV
       v
[registros/detecciones.txt]
       |
       | 11. Responde al cliente
       v
[Cliente Android]
       |
       v
   [TextView: Detectado: Persona, Perro]
```

### 3. Visualizacion de Detecciones

```
[Cliente Vigilante GUI]
       |
       | 1. Click en "Refrescar"
       v
[Socket TCP al Servidor]
       |
       | 2. Envia GET_REGISTROS
       v
[Servidor Central]
       |
       | 3. Hilo procesa
       v
[ServidorVideo]
       |
       | 4. Lee archivo CSV
       v
[registros/detecciones.txt]
       |
       | 5. Lee imagenes
       v
[evidencias/*.jpg]
       |
       | 6. Serializa y envia
       |    cantidad + [timestamp, camara, objetos, imagen]
       v
[Cliente Vigilante]
       |
       | 7. Deserializa datos
       v
[List<RegistroDeteccion>]
       |
       | 8. Actualiza modelo tabla
       v
[ModeloTablaDetecciones]
       |
       | 9. Renderiza en JTable
       v
[GUI: Tabla con miniaturas]
```

## Modelo de Concurrencia

```
[ServerSocket puerto 6000]
       |
       | accept()
       v
[Socket cliente1] ---> [Hilo 1] ---> [ManejadorCliente]
       |
       | accept()
       v
[Socket cliente2] ---> [Hilo 2] ---> [ManejadorCliente]
       |
       | accept()
       v
[Socket cliente3] ---> [Hilo 3] ---> [ManejadorCliente]
       |
       v
[ExecutorService Pool: max 50 hilos]
```

### Sincronizacion

```
Operacion                          Metodo
------------------------------------------------------------
Guardar imagen entrenamiento       synchronized
Incrementar contador               synchronized
Guardar evidencia                  synchronized
Escribir registro CSV              synchronized
```

## Arquitectura de Red

```
                    Red LAN/WiFi
                         |
    +--------------------+--------------------+
    |                    |                    |
    v                    v                    v
[Laptop]           [Android 1]          [Android 2]
IP: 192.168.1.100  IP: 192.168.1.101   IP: 192.168.1.102
Servidor:6000      Cliente             Cliente
    |
    |--- Java Servidor
    |--- Python Scripts
    |--- Cliente Vigilante
```

## Diagrama de Clases Principal

### Servidor

```
ServidorCentral
  - ServerSocket serverSocket
  - ExecutorService poolHilos
  + void iniciar()
  + void detener()
  
ManejadorCliente implements Runnable
  - Socket socket
  - DataInputStream entrada
  - DataOutputStream salida
  + void run()
  
ServidorEntrenamiento
  + void procesarEntrenamiento()
  + String guardarImagen()
  + void entrenarModelo()
  
ServidorTesteo
  + void procesarTesteo()
  + String ejecutarInferencia()
  + void registrarDeteccion()
  
ServidorVideo
  + void enviarRegistros()
  + List<Deteccion> leerRegistros()
  
Deteccion
  - String timestamp
  - String idCamara
  - String objetosDetectados
  - String rutaImagen
```

### Cliente Vigilante

```
ClienteVigilante extends JFrame
  - JTable tablaDetecciones
  - ModeloTablaDetecciones modeloTabla
  + void cargarDatos()
  
ModeloTablaDetecciones extends AbstractTableModel
  - List<RegistroDeteccion> registros
  + Object getValueAt(int row, int col)
  
RegistroDeteccion
  - String timestamp
  - String idCamara
  - String objetosDetectados
  - ImageIcon iconoImagen
```

### Cliente Android

```
MainActivity : AppCompatActivity
  - PreviewView vistaPrevia
  - ImageCapture imageCapture
  + void capturarYEnviar()
  + void enviarModoEntrenamiento()
  + void enviarModoVigilancia()
  + void iniciarVigilancia()
```

## Flujo de Entrenamiento del Modelo

```
[Dataset con imagenes]
       |
       | dataset/persona/*.jpg
       | dataset/perro/*.jpg
       | dataset/celular/*.jpg
       v
[entrenamiento.py]
       |
       | 1. Crea dataset.yaml
       v
[YOLO("yolov8n.pt")]
       |
       | 2. Transfer Learning
       | epochs=50, batch=8
       v
[Proceso de entrenamiento]
       |
       | 3. Guarda mejor modelo
       v
[runs/detect/modelo_objetos/weights/best.pt]
       |
       | 4. Copia a carpeta modelos
       v
[modelos/modelo_entrenado.pt]
```

## Estructura de Archivos Generados

```
pc4_distribuida/
│
├── dataset/                     (Generado por servidor)
│   ├── persona/
│   │   ├── img_0001.jpg
│   │   ├── img_0002.jpg
│   │   └── ...
│   ├── perro/
│   │   └── ...
│   └── celular/
│       └── ...
│
├── evidencias/                  (Generado por servidor)
│   ├── evidencia_0001.jpg
│   ├── evidencia_0002.jpg
│   └── ...
│
├── modelos/                     (Generado por Python)
│   └── modelo_entrenado.pt
│
├── registros/                   (Generado por servidor)
│   └── detecciones.txt
│       (formato: timestamp|camara|objetos|ruta)
│
├── temp/                        (Generado por servidor)
│   └── frame_TIMESTAMP.jpg
│
└── runs/                        (Generado por Python)
    └── detect/
        └── modelo_objetos/
            ├── weights/
            └── ...
```

## Tecnologias Utilizadas

```
Componente             Lenguaje    Framework/Libreria
-------------------------------------------------------
Servidor Central       Java 8      java.net.Socket
                                  java.util.concurrent
                                  
Cliente Vigilante      Java 8      javax.swing
                                  java.net.Socket
                                  
Cliente Movil          Kotlin      CameraX
                                  Android SDK
                                  java.net.Socket
                                  
Scripts IA             Python 3.8  ultralytics (YOLOv8)
                                  torch
                                  
Dataset                           JPEG images
Modelo                            PyTorch (.pt)
Registros                         CSV (pipe-delimited)
```

## Puntos Clave del Diseño

1. **Sin frameworks de mensajeria**: Solo Sockets TCP puros
2. **Concurrencia con Hilos**: ExecutorService con pool
3. **Comunicacion binaria**: DataInputStream/DataOutputStream
4. **Modularizacion**: Servidores logicos separados
5. **Interoperabilidad**: Java invoca Python via ProcessBuilder
6. **Persistencia**: Filesystem para imagenes, CSV para registros
7. **GUI reactiva**: SwingUtilities para thread-safety
8. **Vigilancia automatica**: Timer con Handler en Android
