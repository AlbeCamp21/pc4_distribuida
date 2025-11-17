# Indice Completo de Archivos del Proyecto

## Servidor Central (Java) - 8 archivos

### Paquete principal
1. `servidor/src/main/java/com/sistema/ServidorCentral.java`
   - Clase principal del servidor
   - ServerSocket puerto 6000
   - ExecutorService para pool de hilos

2. `servidor/src/main/java/com/sistema/ManejadorCliente.java`
   - Implementa Runnable
   - Maneja cada cliente en hilo separado
   - Rutea mensajes TRAINING, TEST, GET_REGISTROS

### Paquete servidores
3. `servidor/src/main/java/com/sistema/servidores/ServidorEntrenamiento.java`
   - Procesa mensajes TRAINING
   - Guarda imagenes en dataset/
   - Metodo estatico para entrenar modelo

4. `servidor/src/main/java/com/sistema/servidores/ServidorTesteo.java`
   - Procesa mensajes TEST
   - Ejecuta inferencia Python
   - Guarda evidencias y registros

5. `servidor/src/main/java/com/sistema/servidores/ServidorVideo.java`
   - Procesa mensajes GET_REGISTROS
   - Lee registros CSV
   - Envia imagenes y datos al cliente

### Paquete modelos
6. `servidor/src/main/java/com/sistema/modelos/Deteccion.java`
   - POJO para detecciones
   - timestamp, idCamara, objetosDetectados, rutaImagen

## Cliente Vigilante (Java Swing) - 4 archivos

### Paquete principal
7. `cliente_vigilante/src/main/java/com/vigilante/ClienteVigilante.java`
   - JFrame principal
   - JTable con detecciones
   - Boton refrescar, conexion Socket TCP

8. `cliente_vigilante/src/main/java/com/vigilante/ModeloTablaDetecciones.java`
   - AbstractTableModel personalizado
   - Maneja datos de la tabla

9. `cliente_vigilante/src/main/java/com/vigilante/RenderizadorImagen.java`
   - TableCellRenderer para imagenes
   - Muestra miniaturas en JTable

### Paquete modelos
10. `cliente_vigilante/src/main/java/com/vigilante/modelos/RegistroDeteccion.java`
    - POJO para registros
    - timestamp, idCamara, objetos, ImageIcon

## Aplicacion Android (Kotlin) - 9 archivos

### Codigo Kotlin
11. `android/app/src/main/java/com/sistema/camaracliente/MainActivity.kt`
    - Activity principal
    - CameraX para captura
    - Socket TCP para comunicacion
    - Modos entrenamiento y vigilancia

### Layouts XML
12. `android/app/src/main/res/layout/activity_main.xml`
    - Layout principal
    - PreviewView, botones, spinners, etc.

### Recursos
13. `android/app/src/main/res/values/strings.xml`
    - Strings de la app

14. `android/app/src/main/res/values/colors.xml`
    - Paleta de colores

### Configuracion
15. `android/app/src/main/AndroidManifest.xml`
    - Permisos: CAMERA, INTERNET
    - Configuracion de Activity

16. `android/app/build.gradle`
    - Dependencias: CameraX, Material, etc.

17. `android/build.gradle`
    - Configuracion proyecto Android

18. `android/settings.gradle`
    - Modulos del proyecto

19. `android/gradle/wrapper/gradle-wrapper.properties`
    - Version de Gradle 7.5

20. `android/app/proguard-rules.pro`
    - Reglas ProGuard

## Scripts Python IA - 2 archivos

21. `scripts_ia/entrenamiento.py`
    - Entrenamiento con YOLOv8
    - Transfer Learning
    - Guarda modelo .pt

22. `scripts_ia/inferencia.py`
    - Deteccion de objetos
    - Carga modelo entrenado
    - Retorna objetos por stdout

## Scripts de Ejecucion (Batch) - 4 archivos

23. `iniciar_servidor.bat`
    - Compila y ejecuta servidor central

24. `iniciar_vigilante.bat`
    - Compila y ejecuta cliente vigilante

25. `entrenar_modelo.bat`
    - Ejecuta script Python de entrenamiento

26. `compilar_todo.bat`
    - Compila todos los componentes Java
    - Verifica dependencias Python

## Documentacion (Markdown) - 6 archivos

27. `README.md`
    - Documentacion principal completa
    - Instalacion, ejecucion, arquitectura

28. `INICIO_RAPIDO.md`
    - Guia de inicio rapido
    - Pasos esenciales

29. `PROTOCOLO_TCP.md`
    - Especificacion detallada del protocolo
    - Ejemplos de codigo
    - Diagramas de secuencia

30. `ARQUITECTURA.md`
    - Diagramas de componentes
    - Flujos de datos
    - Diagramas de clases

31. `RESUMEN_PROYECTO.md`
    - Resumen ejecutivo
    - Cumplimiento de requisitos
    - Estado del proyecto

32. `android/README.md`
    - Guia especifica para Android
    - Importar en Android Studio

## Configuracion - 3 archivos

33. `requirements.txt`
    - Dependencias Python
    - ultralytics, torch, etc.

34. `.gitignore`
    - Archivos a ignorar en git
    - Compilados, generados, etc.

35. `practica.txt`
    - Archivo original con especificaciones

## Total: 35 archivos creados/configurados

### Distribucion por tipo:
- **Java**: 10 archivos (.java)
- **Kotlin**: 1 archivo (.kt)
- **Python**: 2 archivos (.py)
- **XML**: 3 archivos (.xml)
- **Gradle**: 4 archivos (.gradle, .properties)
- **Batch**: 4 archivos (.bat)
- **Markdown**: 6 archivos (.md)
- **Configuracion**: 5 archivos (txt, pro, gitignore, etc.)

### Distribucion por componente:
- **Servidor Java**: 6 clases
- **Cliente Vigilante**: 4 clases
- **Android**: 10 archivos
- **Python IA**: 2 scripts
- **Scripts ejecucion**: 4 batch
- **Documentacion**: 6 markdown

## Archivos que se generan en ejecucion:

### Al compilar:
- `servidor/bin/` - Clases compiladas del servidor
- `cliente_vigilante/bin/` - Clases compiladas del cliente

### Al ejecutar:
- `dataset/persona/` - Imagenes de entrenamiento
- `dataset/perro/` - Imagenes de entrenamiento
- `dataset/celular/` - Imagenes de entrenamiento
- `evidencias/` - Capturas con detecciones
- `modelos/modelo_entrenado.pt` - Modelo entrenado
- `registros/detecciones.txt` - Log CSV de detecciones
- `temp/` - Archivos temporales
- `runs/detect/` - Outputs del entrenamiento YOLO

## Estructura final del proyecto:

```
pc4_distribuida/                           (Directorio raiz)
├── servidor/                              (Componente 1)
│   ├── src/main/java/com/sistema/        (6 archivos .java)
│   └── bin/                              (generado)
├── cliente_vigilante/                     (Componente 2)
│   ├── src/main/java/com/vigilante/      (4 archivos .java)
│   └── bin/                              (generado)
├── android/                               (Componente 3)
│   ├── app/                              (10 archivos)
│   └── gradle/                           (4 archivos)
├── scripts_ia/                            (Componente 4)
│   ├── entrenamiento.py
│   └── inferencia.py
├── dataset/                               (generado)
├── evidencias/                            (generado)
├── modelos/                               (generado)
├── registros/                             (generado)
├── temp/                                  (generado)
├── *.bat                                  (4 scripts)
├── *.md                                   (6 documentos)
├── requirements.txt
├── .gitignore
└── practica.txt
```

## Resumen de lineas de codigo:

- **Java Servidor**: ~500 lineas
- **Java Cliente**: ~300 lineas
- **Kotlin Android**: ~400 lineas
- **Python IA**: ~200 lineas
- **XML Layouts**: ~150 lineas
- **Documentacion**: ~2000 lineas
- **TOTAL**: ~3500+ lineas

## Caracteristicas implementadas:

✅ Sistema distribuido completo
✅ Arquitectura Cliente-Servidor
✅ Sockets TCP puros (sin frameworks)
✅ Concurrencia con Hilos
✅ 3 servidores logicos
✅ 2 modos de operacion
✅ Integracion Java-Python
✅ GUI con Swing
✅ App Android completa
✅ IA con YOLOv8
✅ Protocolo TCP propio
✅ Documentacion exhaustiva

## Estado: PROYECTO COMPLETO ✅
