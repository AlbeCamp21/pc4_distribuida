# Protocolo de Comunicacion TCP

Sistema de comunicacion basado en Sockets TCP sin frameworks de mensajeria.

## Caracteristicas del Protocolo

- **Transporte**: TCP (Transmission Control Protocol)
- **Puerto**: 6000
- **Encoding**: UTF-8 para strings
- **Formato**: Binario para imagenes
- **Concurrencia**: Un hilo por cliente

## Tipos de Mensajes

### 1. TRAINING - Envio de Imagenes para Entrenamiento

**Direccion**: Cliente Android -> Servidor

**Formato del Mensaje**:
```
Campo                 Tipo           Descripcion
----------------------------------------------------------
Tipo de mensaje       UTF String     "TRAINING"
Etiqueta             UTF String     "persona", "perro" o "celular"
Tamaño imagen        Int (4 bytes)  Cantidad de bytes de la imagen
Bytes imagen         byte[]         Datos binarios de la imagen JPEG
```

**Respuesta del Servidor**:
```
Campo                 Tipo           Descripcion
----------------------------------------------------------
Estado               UTF String     "OK" o "ERROR"
Mensaje              UTF String     Descripcion del resultado
```

**Ejemplo en Java (Cliente)**:
```java
DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
salida.writeUTF("TRAINING");
salida.writeUTF("persona");
salida.writeInt(bytesImagen.length);
salida.write(bytesImagen);
salida.flush();
```

**Ejemplo en Java (Servidor)**:
```java
DataInputStream entrada = new DataInputStream(socket.getInputStream());
String tipo = entrada.readUTF();        // "TRAINING"
String etiqueta = entrada.readUTF();    // "persona"
int tamanio = entrada.readInt();
byte[] bytes = new byte[tamanio];
entrada.readFully(bytes);
```

---

### 2. TEST - Envio de Frames para Vigilancia

**Direccion**: Cliente Android -> Servidor -> Scripts Python

**Formato del Mensaje**:
```
Campo                 Tipo           Descripcion
----------------------------------------------------------
Tipo de mensaje       UTF String     "TEST"
ID Camara            UTF String     "CAMARA_1", "CAMARA_2", etc.
Tamaño imagen        Int (4 bytes)  Cantidad de bytes de la imagen
Bytes imagen         byte[]         Datos binarios de la imagen JPEG
```

**Respuesta del Servidor**:
```
Campo                 Tipo           Descripcion
----------------------------------------------------------
Estado               UTF String     "OK" o "ERROR"
Resultado            UTF String     Objetos detectados ej: "Persona, Perro"
```

**Flujo Interno del Servidor**:
1. Recibe imagen del cliente
2. Guarda temporalmente en disco
3. Ejecuta script Python de inferencia
4. Lee resultado de stdout
5. Si hay deteccion, guarda evidencia y registro
6. Responde al cliente

**Ejemplo en Kotlin (Cliente)**:
```kotlin
val salida = DataOutputStream(socket.getOutputStream())
salida.writeUTF("TEST")
salida.writeUTF("CAMARA_1")
salida.writeInt(bytesImagen.size)
salida.write(bytesImagen)
salida.flush()
```

---

### 3. GET_REGISTROS - Consulta de Detecciones

**Direccion**: Cliente Vigilante -> Servidor

**Formato del Mensaje**:
```
Campo                 Tipo           Descripcion
----------------------------------------------------------
Tipo de mensaje       UTF String     "GET_REGISTROS"
```

**Respuesta del Servidor**:
```
Campo                       Tipo           Descripcion
----------------------------------------------------------
Cantidad de registros       Int            Numero de detecciones
[Para cada registro:]
  Timestamp                 UTF String     "dd/MM/yyyy HH:mm:ss"
  ID Camara                 UTF String     Identificador de camara
  Objetos detectados        UTF String     Lista de objetos
  Ruta imagen               UTF String     Path del archivo
  Tamaño imagen             Int            Bytes de la imagen
  Bytes imagen              byte[]         Datos binarios de imagen
```

**Ejemplo en Java (Cliente Vigilante)**:
```java
DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
DataInputStream entrada = new DataInputStream(socket.getInputStream());

salida.writeUTF("GET_REGISTROS");
salida.flush();

int cantidad = entrada.readInt();
for (int i = 0; i < cantidad; i++) {
    String timestamp = entrada.readUTF();
    String idCamara = entrada.readUTF();
    String objetos = entrada.readUTF();
    String ruta = entrada.readUTF();
    int tamanio = entrada.readInt();
    byte[] bytes = new byte[tamanio];
    entrada.readFully(bytes);
}
```

---

## Manejo de Concurrencia

### Pool de Hilos en Servidor

```java
ExecutorService poolHilos = Executors.newFixedThreadPool(50);

while (true) {
    Socket cliente = serverSocket.accept();
    poolHilos.execute(new ManejadorCliente(cliente));
}
```

### Hilo por Cliente

Cada cliente conectado obtiene su propio hilo que:
1. Lee el tipo de mensaje
2. Rutea a la funcion correspondiente
3. Procesa la solicitud
4. Envia respuesta
5. Cierra la conexion

### Sincronizacion

Operaciones sincronizadas con `synchronized`:
- Guardar imagenes (incremento de contadores)
- Escribir registros de detecciones
- Guardar evidencias

---

## Formato de Datos

### Imagenes

- **Formato**: JPEG
- **Compresion**: 80% de calidad
- **Resolucion**: Variable (capturada por camara)
- **Transmision**: Array de bytes completo

### Strings

- **Encoding**: UTF-8
- **Metodo**: DataInputStream.readUTF() / DataOutputStream.writeUTF()
- **Longitud**: Variable con prefijo de tamaño

### Enteros

- **Tipo**: int (4 bytes)
- **Orden**: Big-endian (Java default)
- **Rango**: -2,147,483,648 a 2,147,483,647

---

## Manejo de Errores

### Servidor

```java
try {
    // Procesar solicitud
    salida.writeUTF("OK");
    salida.writeUTF("Mensaje de exito");
} catch (Exception e) {
    salida.writeUTF("ERROR");
    salida.writeUTF(e.getMessage());
}
```

### Cliente

```kotlin
try {
    // Enviar solicitud
    val estado = entrada.readUTF()
    if (estado == "OK") {
        // Procesar respuesta exitosa
    } else {
        // Manejar error
    }
} catch (e: Exception) {
    // Error de conexion
}
```

---

## Diagrama de Secuencia

### Modo Entrenamiento

```
Cliente Android          Servidor Central
      |                        |
      |------ "TRAINING" ----->|
      |------ "persona" ------->|
      |------ tamaño --------->|
      |------ bytes[] -------->|
      |                        | [Guarda en dataset/persona/]
      |<----- "OK" ------------|
      |<----- mensaje ---------|
      |                        |
```

### Modo Vigilancia

```
Cliente Android    Servidor Central    Script Python
      |                  |                  |
      |--- "TEST" ------->|                  |
      |--- "CAMARA_1" --->|                  |
      |--- tamaño ------->|                  |
      |--- bytes[] ------>|                  |
      |                  | [Guarda temp]    |
      |                  |--- ejecutar ---->|
      |                  |                  | [Inferencia]
      |                  |<-- resultado ----|
      |                  | [Guarda evidencia]
      |                  | [Registra deteccion]
      |<-- "OK" ---------|                  |
      |<-- objetos ------|                  |
      |                  |                  |
```

### Consulta de Registros

```
Cliente Vigilante       Servidor Central
      |                        |
      |-- "GET_REGISTROS" ---->|
      |                        | [Lee archivo registros]
      |<-- cantidad ----------|
      |<-- [timestamp] --------|
      |<-- [idCamara] ---------|
      |<-- [objetos] ----------|
      |<-- [ruta] -------------|
      |<-- [tamaño] -----------|
      |<-- [bytes[]] ----------|
      |     (repetir N veces)  |
      |                        |
```

---

## Seguridad y Validaciones

### Validaciones del Servidor

1. **Tipo de mensaje**: Verificar que sea TRAINING, TEST o GET_REGISTROS
2. **Etiqueta**: Solo permitir persona, perro, celular
3. **Tamaño imagen**: Validar que sea razonable (< 10MB)
4. **Formato archivo**: Verificar que sea imagen valida

### Limitaciones

- **Timeout**: Conexiones sin actividad por 30 segundos se cierran
- **Buffer**: 8KB para I/O de red
- **Max conexiones**: Pool limitado a 50 hilos concurrentes

---

## Notas de Implementacion

1. **No usar HTTP**: Solo TCP puro con Sockets
2. **No usar frameworks**: Sin Spring, Netty, etc.
3. **Hilos obligatorios**: Para manejo de concurrencia
4. **BufferedStream**: Para optimizar I/O de red
5. **Flush**: Siempre hacer flush() despues de escribir

---

## Ejemplo Completo

### Cliente (Enviar Imagen)

```kotlin
fun enviarImagen(imagen: ByteArray, etiqueta: String) {
    Thread {
        val socket = Socket("192.168.1.100", 6000)
        val salida = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
        val entrada = DataInputStream(BufferedInputStream(socket.getInputStream()))
        
        salida.writeUTF("TRAINING")
        salida.writeUTF(etiqueta)
        salida.writeInt(imagen.size)
        salida.write(imagen)
        salida.flush()
        
        val estado = entrada.readUTF()
        val mensaje = entrada.readUTF()
        
        socket.close()
        
        Log.d("Cliente", "$estado: $mensaje")
    }.start()
}
```

### Servidor (Procesar Mensaje)

```java
public void procesarCliente(Socket socket) {
    try {
        DataInputStream entrada = new DataInputStream(
            new BufferedInputStream(socket.getInputStream())
        );
        DataOutputStream salida = new DataOutputStream(
            new BufferedOutputStream(socket.getOutputStream())
        );
        
        String tipo = entrada.readUTF();
        
        if (tipo.equals("TRAINING")) {
            String etiqueta = entrada.readUTF();
            int tamanio = entrada.readInt();
            byte[] bytes = new byte[tamanio];
            entrada.readFully(bytes);
            
            guardarImagen(etiqueta, bytes);
            
            salida.writeUTF("OK");
            salida.writeUTF("Imagen guardada correctamente");
            salida.flush();
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        socket.close();
    }
}
```
