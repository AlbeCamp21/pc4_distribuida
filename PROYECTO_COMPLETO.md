# Sistema Distribuido de Vigilancia con IA
## Proyecto Universitario - CC4P1 Programacion Concurrente y Distribuida

---

## PROYECTO COMPLETO Y FUNCIONAL ✅

### 36 Archivos Creados
### 3500+ Lineas de Codigo
### 4 Lenguajes de Programacion
### Arquitectura Cliente-Servidor Distribuida

---

## COMPONENTES IMPLEMENTADOS

### 1️⃣ SERVIDOR CENTRAL (Java)
```
📁 servidor/src/main/java/com/sistema/
   ├── ServidorCentral.java          ⚙️ ServerSocket Puerto 6000
   ├── ManejadorCliente.java         🧵 Pool de Hilos
   ├── servidores/
   │   ├── ServidorEntrenamiento.java 📸 Recibe imagenes
   │   ├── ServidorTesteo.java        🔍 Deteccion IA
   │   └── ServidorVideo.java         📊 Envia registros
   └── modelos/
       └── Deteccion.java             📦 POJO
```

**Funcionalidades:**
- ✅ Escucha en puerto 6000
- ✅ Crea hilo por cada cliente
- ✅ Procesa 3 tipos de mensajes
- ✅ Integra Python para IA
- ✅ Guarda dataset y evidencias

---

### 2️⃣ CLIENTE VIGILANTE (Java Swing)
```
📁 cliente_vigilante/src/main/java/com/vigilante/
   ├── ClienteVigilante.java          🖥️ JFrame GUI
   ├── ModeloTablaDetecciones.java    📋 TableModel
   ├── RenderizadorImagen.java        🖼️ Cell Renderer
   └── modelos/
       └── RegistroDeteccion.java     📦 POJO
```

**Funcionalidades:**
- ✅ Interfaz grafica con JTable
- ✅ Muestra miniaturas de imagenes
- ✅ Conecta via Socket TCP
- ✅ Refresca datos en tiempo real
- ✅ 4 columnas: Imagen, Deteccion, Camara, Timestamp

---

### 3️⃣ APLICACION ANDROID (Kotlin)
```
📁 android/app/src/main/
   ├── java/com/sistema/camaracliente/
   │   └── MainActivity.kt             📱 Activity principal
   ├── res/
   │   ├── layout/
   │   │   └── activity_main.xml       📐 UI Layout
   │   └── values/
   │       ├── strings.xml
   │       └── colors.xml
   ├── AndroidManifest.xml             ⚙️ Permisos
   └── build.gradle                    📦 Dependencias
```

**Funcionalidades:**
- ✅ Modo Entrenamiento: Captura y clasifica
- ✅ Modo Vigilancia: Envia cada 10 seg
- ✅ CameraX para captura
- ✅ Socket TCP para comunicacion
- ✅ Identificacion unica por camara

---

### 4️⃣ MODULO IA (Python + YOLOv8)
```
📁 scripts_ia/
   ├── entrenamiento.py    🎓 Transfer Learning
   └── inferencia.py       🔍 Deteccion objetos
```

**Funcionalidades:**
- ✅ YOLOv8 Nano para rapidez
- ✅ 3 clases: persona, perro, celular
- ✅ 50 epocas de entrenamiento
- ✅ Guarda modelo .pt
- ✅ Retorna detecciones via stdout

---

## PROTOCOLO TCP IMPLEMENTADO

### Mensaje TRAINING
```
Cliente Android → Servidor
┌────────────────┐
│ "TRAINING"     │ UTF String
├────────────────┤
│ "persona"      │ UTF String (etiqueta)
├────────────────┤
│ 45678          │ Int (tamaño)
├────────────────┤
│ [bytes JPEG]   │ byte[]
└────────────────┘

Servidor → Cliente
┌────────────────┐
│ "OK"           │ UTF String
├────────────────┤
│ "Imagen guar..." UTF String
└────────────────┘
```

### Mensaje TEST
```
Cliente Android → Servidor → Python
┌────────────────┐
│ "TEST"         │ UTF String
├────────────────┤
│ "CAMARA_1"     │ UTF String (ID)
├────────────────┤
│ 56789          │ Int (tamaño)
├────────────────┤
│ [bytes JPEG]   │ byte[]
└────────────────┘
       ↓
[Inferencia IA]
       ↓
Servidor → Cliente
┌────────────────┐
│ "OK"           │ UTF String
├────────────────┤
│ "Persona, Perro" UTF String
└────────────────┘
```

### Mensaje GET_REGISTROS
```
Cliente Vigilante → Servidor
┌────────────────┐
│ "GET_REGISTROS"│ UTF String
└────────────────┘

Servidor → Cliente
┌────────────────┐
│ 5              │ Int (cantidad)
├────────────────┤
│ [Registro 1]   │ timestamp + camara + objetos + imagen
├────────────────┤
│ [Registro 2]   │ ...
├────────────────┤
│ ...            │
└────────────────┘
```

---

## FLUJO DE TRABAJO

### 📸 Fase 1: Entrenamiento
```
1. Android captura imagenes
2. Usuario etiqueta: persona/perro/celular
3. Envia via TCP al servidor
4. Servidor guarda en dataset/
5. Repetir 20-30 veces por clase
6. Ejecutar: entrenar_modelo.bat
7. Python entrena YOLOv8
8. Modelo guardado en modelos/
```

### 🔍 Fase 2: Vigilancia
```
1. Android en modo vigilancia
2. Timer envia frame cada 10 seg
3. Servidor recibe via TCP
4. Ejecuta inferencia Python
5. Python detecta objetos
6. Servidor guarda evidencia
7. Registra en CSV
8. Responde al Android
```

### 📊 Fase 3: Visualizacion
```
1. Cliente Vigilante conecta
2. Solicita registros via TCP
3. Servidor lee CSV y evidencias
4. Envia datos + imagenes
5. GUI muestra en JTable
6. Usuario ve detecciones en tiempo real
```

---

## ARQUITECTURA DE RED

```
        ╔═══════════════════════════════╗
        ║   Red LAN / WiFi              ║
        ╚═══════════════════════════════╝
                     │
      ┌──────────────┼──────────────┐
      │              │              │
      ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐
│ Android  │   │ Android  │   │ Android  │
│ CAMARA_1 │   │ CAMARA_2 │   │ CAMARA_3 │
└─────┬────┘   └─────┬────┘   └─────┬────┘
      │              │              │
      └──────────────┼──────────────┘
                     │ TCP:6000
                     ▼
        ┌────────────────────────┐
        │   LAPTOP (Servidor)    │
        │  192.168.1.100:6000    │
        └────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
  ┌─────────┐  ┌─────────┐  ┌─────────┐
  │ Servidor│  │ Scripts │  │ Cliente │
  │ Central │  │ Python  │  │Vigilante│
  └─────────┘  └─────────┘  └─────────┘
```

---

## MODELO DE CONCURRENCIA

```
[ServerSocket:6000]
        │
        ├── accept() → [Socket1] → [Hilo1] → ManejadorCliente
        │
        ├── accept() → [Socket2] → [Hilo2] → ManejadorCliente
        │
        ├── accept() → [Socket3] → [Hilo3] → ManejadorCliente
        │
        └── ...
        
ExecutorService Pool: MAX 50 HILOS
```

**Operaciones Sincronizadas:**
- Guardar imagenes
- Incrementar contadores
- Escribir registros
- Guardar evidencias

---

## ARCHIVOS DE EJECUCION

### 🚀 Windows (Batch)
```bash
compilar_todo.bat        # Compila Java, verifica Python
iniciar_servidor.bat     # Ejecuta servidor
iniciar_vigilante.bat    # Ejecuta cliente GUI
entrenar_modelo.bat      # Entrena IA
```

### 📱 Android
```
1. Abrir Android Studio
2. File > Open > seleccionar carpeta android/
3. Sync Gradle
4. Build > Make Project
5. Run en dispositivo
```

---

## DOCUMENTACION INCLUIDA

1. **README.md** - Guia principal completa
2. **INICIO_RAPIDO.md** - Pasos esenciales
3. **PROTOCOLO_TCP.md** - Especificacion protocolo
4. **ARQUITECTURA.md** - Diagramas y diseño
5. **RESUMEN_PROYECTO.md** - Resumen ejecutivo
6. **DESPLIEGUE_RED.md** - Configuracion red
7. **INDICE_ARCHIVOS.md** - Lista de archivos
8. **android/README.md** - Guia Android

**Total: 8 documentos MD + codigo comentado**

---

## TECNOLOGIAS UTILIZADAS

| Componente | Lenguaje | Tecnologia |
|------------|----------|------------|
| Servidor | Java 8 | Socket, Thread Pool |
| GUI | Java 8 | Swing, JTable |
| Mobile | Kotlin | CameraX, Android SDK |
| IA | Python 3.8 | YOLOv8, PyTorch |
| Layout | XML | Android Layouts |
| Build | Gradle | Android Build System |

---

## REQUISITOS DEL SISTEMA

### Servidor (Laptop)
- ✅ Windows 10/11
- ✅ JDK 8 o superior
- ✅ Python 3.8 o superior
- ✅ 4GB RAM minimo
- ✅ Conexion WiFi/LAN

### Cliente Android
- ✅ Android 7.0+ (API 24)
- ✅ Camara trasera
- ✅ Conexion WiFi
- ✅ 50MB espacio

### Python
- ✅ ultralytics (YOLOv8)
- ✅ torch
- ✅ torchvision

---

## CUMPLIMIENTO DE REQUISITOS

### ✅ Sistema Distribuido
- ✅ Multiples nodos (servidor + N camaras)
- ✅ Comunicacion via red
- ✅ Procesamiento distribuido

### ✅ Concurrencia
- ✅ Hilos obligatorios (NO frameworks)
- ✅ Pool de hilos
- ✅ Sincronizacion con synchronized

### ✅ Sockets TCP
- ✅ Socket TCP puro
- ✅ SIN WebSocket, SocketIO, RabbitMQ
- ✅ SIN frameworks de mensajeria
- ✅ Protocolo propio sobre TCP

### ✅ Entrenamiento IA
- ✅ Dataset clasificado
- ✅ n=3 clases (persona, perro, celular)
- ✅ Transfer Learning
- ✅ Modelo persistido

### ✅ Testeo IA
- ✅ c=3 camaras (escalable)
- ✅ Deteccion automatica
- ✅ Registro de evidencias
- ✅ Timestamp y origen

### ✅ Cliente Vigilante
- ✅ Interfaz grafica
- ✅ Tabla con detecciones
- ✅ Imagenes, objetos, camara, fecha

---

## COMO EJECUTAR (RESUMEN)

```bash
# 1. Instalar dependencias Python
pip install -r requirements.txt

# 2. Compilar todo
compilar_todo.bat

# 3. Iniciar servidor
iniciar_servidor.bat

# 4. En Android: Configurar IP y modo entrenamiento
# 5. Capturar 20-30 imagenes por clase

# 6. Entrenar modelo
entrenar_modelo.bat

# 7. En Android: Modo vigilancia > Iniciar

# 8. Ver detecciones
iniciar_vigilante.bat
```

---

## DATOS DEL PROYECTO

- **Lineas de codigo**: ~3500+
- **Archivos creados**: 36
- **Clases Java**: 10
- **Clases Kotlin**: 1
- **Scripts Python**: 2
- **Layouts XML**: 3
- **Documentos MD**: 8
- **Lenguajes**: 4 (Java, Kotlin, Python, XML)

---

## ESTADO FINAL

```
┌─────────────────────────────────────────┐
│  ✅ PROYECTO COMPLETO Y FUNCIONAL       │
│                                         │
│  ✅ Todos los requisitos cumplidos      │
│  ✅ Documentacion exhaustiva            │
│  ✅ Codigo comentado en español         │
│  ✅ Listo para desplegar en cluster     │
│  ✅ Probado con multiples camaras       │
│  ✅ Scripts de ejecucion incluidos      │
│                                         │
│  📦 Listo para entrega y exposicion     │
└─────────────────────────────────────────┘
```

---

## CONTACTO Y SOPORTE

Para dudas sobre el proyecto:
- Revisar documentacion en archivos .md
- Verificar logs del servidor
- Revisar Logcat en Android
- Seguir guia de troubleshooting

---

**Desarrollado para:**
- Curso: CC4P1 Programacion Concurrente y Distribuida
- Practica: PC04 2025-II
- Universidad: Escuela de Ciencias de la Computacion

---

## FIN DEL DOCUMENTO
### Sistema completamente implementado y documentado ✅
