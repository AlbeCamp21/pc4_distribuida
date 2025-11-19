# Sistema Distribuido para Vigilancia con IA

Sistema distribuido con Java, Python y Android para detección de objetos en tiempo real usando YOLOv8.

## Pasos de Ejecución

### 1. Instalar dependencias Python

```powershell
$ python -m venv venv
$ venv\Scripts\activate
$ pip install -r requirements.txt
```

### 2. Obtener IP del servidor

```powershell
ipconfig
```

### 3. Iniciar servidor

```powershell
.\iniciar_servidor.bat
```

### 4. Configurar Android

**Modificar IP en el código:**
- Abrir el proyecto de Android
- Cambiar línea: `private val SERVER_IP = "<IP>"`
- Poner tu IP del paso 2

**Compilar en Android Studio:**
1. File → Open → Seleccionar carpeta `android/`
2. Gradle Sync
3. Conectar dispositivo Android por USB
4. Run → Run 'app'

### 5. Ejecutar vigilancia en Android

1. Abrir app
2. Ingresar IP del servidor
3. Ingresar ID cámara, por ejemplo: `CAMARA_1`
4. Seleccionar "Modo Vigilancia"
5. Presionar "Iniciar Vigilancia Automática"

### 6. Ver detecciones (Opcional)

```powershell
.\iniciar_vigilante.bat
```

---

## Pasos para Entrenar Modelo (Opcional)

### 1. Iniciar servidor

```powershell
.\iniciar_servidor.bat
```

### 2. Capturar imágenes en Android

1. Abrir app
2. Ingresar IP del servidor
3. Seleccionar "Modo Entrenamiento"
4. Seleccionar etiqueta: `persona`, `perro` o `celular`
5. Presionar "Capturar y Enviar"
6. Repetir 30-50 veces por cada etiqueta

### 3. Entrenar el modelo

```powershell
.\entrenar_modelo.bat
```

### 4. Usar el modelo entrenado

El modelo se guarda automáticamente en `modelos/modelo_entrenado.pt` y está listo para usar en modo vigilancia.