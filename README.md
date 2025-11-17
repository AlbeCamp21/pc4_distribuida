# Sistema Distribuido para Entrenamiento y Vigilancia con IA

Sistema distribuido desarrollado en Java, Python y Kotlin que permite el entrenamiento y consumo de modelos de Inteligencia Artificial para deteccion de objetos mediante camaras IP moviles.

## Arquitectura del Sistema

El sistema consta de tres componentes principales:

1. **Servidor Central (Java)**: Nodo de procesamiento que orquesta tres servidores logicos:
   - Servidor de Video: Gestiona los registros de detecciones
   - Servidor de Entrenamiento: Recibe y almacena imagenes para entrenar el modelo
   - Servidor de Testeo: Procesa frames de vigilancia en tiempo real

2. **Cliente Movil (Android/Kotlin)**: Aplicacion de camara con dos modos:
   - Modo Entrenamiento: Captura imagenes clasificadas para dataset
   - Modo Vigilancia: Envia frames cada 10 segundos para analisis

3. **Cliente Vigilante (Java Swing)**: Interfaz grafica para visualizar detecciones en tiempo real

4. **Modulo de IA (Python/PyTorch)**: Scripts de entrenamiento e inferencia con YOLOv8

## Requisitos

### Servidor (Java)
- JDK 8 o superior
- Python 3.8 o superior

### Python
- ultralytics (YOLOv8)
- torch
- torchvision

### Android
- Android Studio Arctic Fox o superior
- Dispositivo Android con API 24 o superior
- Camara trasera

## Instalacion

### 1. Configurar Python

```bash
cd pc4_distribuida
pip install -r requirements.txt
```

### 2. Compilar Servidor Java

```bash
cd servidor/src/main/java
javac -d ../../../bin com/sistema/*.java com/sistema/servidores/*.java com/sistema/modelos/*.java
```

### 3. Compilar Cliente Vigilante

```bash
cd cliente_vigilante/src/main/java
javac -d ../../../bin com/vigilante/*.java com/vigilante/modelos/*.java
```

### 4. Importar Proyecto Android

1. Abrir Android Studio
2. File > Open > Seleccionar carpeta `android/`
3. Sync Project with Gradle Files
4. Build > Make Project

## Ejecucion

### 1. Iniciar Servidor Central

```bash
cd servidor
java -cp bin com.sistema.ServidorCentral
```

El servidor inicia en puerto 6000 y crea automaticamente las carpetas necesarias.

### 2. Modo Entrenamiento

#### Desde Android:
1. Abrir aplicacion en dispositivo
2. Configurar IP del servidor
3. Seleccionar "Modo Entrenamiento"
4. Elegir etiqueta (persona, perro, celular)
5. Capturar y enviar imagenes

#### Entrenar el modelo:
```bash
python scripts_ia/entrenamiento.py
```

El modelo entrenado se guarda en `modelos/modelo_entrenado.pt`

### 3. Modo Vigilancia

#### Desde Android:
1. Configurar IP del servidor y ID de camara
2. Seleccionar "Modo Vigilancia"
3. Click en "Iniciar Vigilancia Automatica"
4. La camara envia frames cada 10 segundos

### 4. Cliente Vigilante

```bash
cd cliente_vigilante
java -cp bin com.vigilante.ClienteVigilante
```

La interfaz grafica muestra:
- Miniatura de imagen capturada
- Objetos detectados
- Camara de origen
- Timestamp de deteccion

## Protocolo de Comunicacion TCP

### Mensaje de Entrenamiento
```
Cliente -> Servidor:
  - "TRAINING" (UTF String)
  - Etiqueta (UTF String): "persona", "perro" o "celular"
  - Tamaño imagen (Int)
  - Bytes de imagen

Servidor -> Cliente:
  - Estado (UTF String): "OK" o "ERROR"
  - Mensaje (UTF String)
```

### Mensaje de Testeo/Vigilancia
```
Cliente -> Servidor:
  - "TEST" (UTF String)
  - ID Camara (UTF String): "CAMARA_1", "CAMARA_2", etc.
  - Tamaño imagen (Int)
  - Bytes de imagen

Servidor -> Cliente:
  - Estado (UTF String): "OK" o "ERROR"
  - Resultado (UTF String): Objetos detectados o mensaje
```

### Consulta de Registros
```
Cliente -> Servidor:
  - "GET_REGISTROS" (UTF String)

Servidor -> Cliente:
  - Cantidad registros (Int)
  - Para cada registro:
    - Timestamp (UTF String)
    - ID Camara (UTF String)
    - Objetos detectados (UTF String)
    - Ruta imagen (UTF String)
    - Tamaño imagen (Int)
    - Bytes imagen
```

## Estructura de Directorios

```
pc4_distribuida/
├── servidor/                    # Servidor Central Java
│   └── src/main/java/com/sistema/
│       ├── ServidorCentral.java
│       ├── ManejadorCliente.java
│       ├── servidores/
│       │   ├── ServidorEntrenamiento.java
│       │   ├── ServidorTesteo.java
│       │   └── ServidorVideo.java
│       └── modelos/
│           └── Deteccion.java
│
├── cliente_vigilante/           # Cliente GUI Java
│   └── src/main/java/com/vigilante/
│       ├── ClienteVigilante.java
│       ├── ModeloTablaDetecciones.java
│       ├── RenderizadorImagen.java
│       └── modelos/
│           └── RegistroDeteccion.java
│
├── android/                     # Aplicacion Android
│   └── app/src/main/
│       ├── java/com/sistema/camaracliente/
│       │   └── MainActivity.kt
│       ├── res/
│       │   └── layout/
│       │       └── activity_main.xml
│       └── AndroidManifest.xml
│
├── scripts_ia/                  # Scripts Python IA
│   ├── entrenamiento.py
│   └── inferencia.py
│
├── dataset/                     # Imagenes de entrenamiento
│   ├── persona/
│   ├── perro/
│   └── celular/
│
├── evidencias/                  # Imagenes con detecciones
├── modelos/                     # Modelos entrenados (.pt)
└── registros/                   # Logs de detecciones
    └── detecciones.txt
```

## Configuracion de Red

### Red LAN
1. Obtener IP del servidor: `ipconfig` (Windows) o `ifconfig` (Linux)
2. Configurar firewall para permitir puerto 6000
3. En app Android, ingresar IP del servidor

### Red WiFi
1. Conectar servidor y dispositivos Android a la misma red WiFi
2. Obtener IP local del servidor
3. Configurar IP en aplicacion Android

## Notas Importantes

- El sistema usa **Sockets TCP puros** (no HTTP, REST ni frameworks)
- La concurrencia se maneja con **Hilos (Threads)** en Java
- El servidor soporta **multiples clientes simultaneos**
- Cada cliente Android puede funcionar como camara independiente
- El entrenamiento requiere **minimo 20-30 imagenes por clase**
- La inferencia necesita el modelo entrenado en `modelos/modelo_entrenado.pt`

## Troubleshooting

### Error de conexion desde Android
- Verificar que servidor este ejecutandose
- Validar IP correcta del servidor
- Comprobar que esten en la misma red
- Verificar firewall no bloquee puerto 6000

### Modelo no detecta objetos
- Entrenar modelo con mas imagenes
- Verificar que exista `modelos/modelo_entrenado.pt`
- Revisar que imagenes de entrenamiento sean claras

### Errores en scripts Python
- Instalar dependencias: `pip install -r requirements.txt`
- Verificar version Python 3.8 o superior

## Autores

Proyecto universitario - CC4P1 Programacion Concurrente y Distribuida

## Licencia

Uso academico - Universidad 2025-II