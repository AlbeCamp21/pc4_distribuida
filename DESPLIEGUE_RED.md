# Guia de Despliegue en Red LAN y WiFi

## Configuracion del Servidor

### 1. Obtener IP del Servidor

**Windows:**
```bash
ipconfig
```
Buscar la seccion "Adaptador de LAN inalambrica Wi-Fi" o "Adaptador de Ethernet"
Anotar la "Direccion IPv4", ejemplo: `192.168.1.100`

**Linux:**
```bash
ifconfig
ip addr show
```

### 2. Configurar Firewall Windows

**Metodo 1: Permitir Java**
1. Panel de Control > Sistema y Seguridad > Firewall de Windows Defender
2. Configuracion avanzada
3. Reglas de entrada > Nueva regla
4. Programa > Siguiente
5. Seleccionar `java.exe` o `javaw.exe`
6. Permitir la conexion
7. Aplicar a todos los perfiles
8. Nombre: "Sistema Vigilancia - Java"

**Metodo 2: Abrir Puerto 6000**
1. Reglas de entrada > Nueva regla
2. Puerto > Siguiente
3. TCP > Puerto especifico: 6000
4. Permitir la conexion
5. Aplicar a todos los perfiles
6. Nombre: "Sistema Vigilancia - Puerto 6000"

**Comando PowerShell (como Administrador):**
```powershell
New-NetFirewallRule -DisplayName "Sistema Vigilancia" -Direction Inbound -Protocol TCP -LocalPort 6000 -Action Allow
```

### 3. Verificar Puerto Abierto

Desde el servidor, ejecutar:
```bash
netstat -an | findstr 6000
```

Deberia mostrar:
```
TCP    0.0.0.0:6000    0.0.0.0:0    LISTENING
```

## Configuracion de Red

### Red LAN (Ethernet)

1. Conectar laptop (servidor) y dispositivos Android a la misma red via cable/router
2. Todos deben estar en el mismo rango de IPs (ej: 192.168.1.x)

### Red WiFi

1. Conectar laptop y dispositivos Android a la misma red WiFi
2. Verificar que esten en la misma subred
3. Si hay problemas, desactivar "Aislamiento de AP" en el router

### Red Ad-Hoc (Punto de acceso movil)

**Desde Windows:**
1. Configuracion > Red e Internet > Zona con cobertura inalambrica movil
2. Activar "Compartir mi conexion a Internet"
3. Configurar SSID y contraseña
4. Anotar la IP (generalmente 192.168.137.1)

**Desde Android como hotspot:**
1. El Android con mejor señal comparte WiFi
2. Conectar laptop y otros dispositivos al hotspot
3. La IP del Android hotspot sera 192.168.43.1

## Configuracion de Clientes Android

### Modificar IP en la App

**Opcion 1: Desde la interfaz**
1. Abrir app
2. Campo "IP del Servidor"
3. Ingresar IP del servidor (ej: 192.168.1.100)

**Opcion 2: Modificar codigo (antes de compilar)**
Editar `MainActivity.kt`:
```kotlin
private const val HOST_SERVIDOR = "192.168.1.100"  // Tu IP aqui
```

### Configurar ID de Camara

Cada dispositivo Android debe tener un ID unico:
- Dispositivo 1: `CAMARA_1`
- Dispositivo 2: `CAMARA_2`
- Dispositivo 3: `CAMARA_3`

Se configura en el campo "ID de Camara" de la app.

## Verificacion de Conectividad

### Desde el Servidor

**Verificar que el servidor este escuchando:**
```bash
netstat -an | findstr 6000
```

### Desde el Cliente Android

**Hacer ping al servidor:**
Instalar app "Network Tools" o "Ping & Net" desde Play Store
```
ping 192.168.1.100
```

**Probar conexion TCP:**
Desde PowerShell en otra PC:
```powershell
Test-NetConnection -ComputerName 192.168.1.100 -Port 6000
```

## Despliegue Completo

### Topologia Recomendada

```
        [Router WiFi/Switch]
                |
    +-----------+-----------+
    |           |           |
[Laptop]   [Android 1]  [Android 2]
Servidor   CAMARA_1     CAMARA_2
```

### Paso a Paso

1. **Preparar Servidor**
   ```bash
   cd pc4_distribuida
   compilar_todo.bat
   ```

2. **Obtener IP del Servidor**
   ```bash
   ipconfig
   # Anotar IP: 192.168.1.100
   ```

3. **Configurar Firewall**
   - Permitir Java o Puerto 6000

4. **Iniciar Servidor**
   ```bash
   iniciar_servidor.bat
   ```
   
   Deberia mostrar:
   ```
   ===========================================
      SERVIDOR CENTRAL INICIADO
      Puerto: 6000
      Esperando conexiones de clientes...
   ===========================================
   ```

5. **Configurar Apps Android**
   - Instalar APK en cada dispositivo
   - Configurar IP: 192.168.1.100
   - Configurar ID: CAMARA_1, CAMARA_2, etc.

6. **Probar Conexion**
   - Modo Entrenamiento
   - Capturar una imagen
   - Verificar que llegue al servidor

7. **Recolectar Dataset**
   - Capturar 20-30 imagenes por clase
   - Verificar en `dataset/` del servidor

8. **Entrenar Modelo**
   ```bash
   entrenar_modelo.bat
   ```

9. **Activar Vigilancia**
   - Cambiar a Modo Vigilancia
   - Iniciar Vigilancia Automatica
   - Verificar envio cada 10 segundos

10. **Iniciar Cliente Vigilante**
    ```bash
    iniciar_vigilante.bat
    ```
    - Click en "Refrescar Datos"
    - Verificar detecciones en tabla

## Rangos de IP Comunes

- **Redes domesticas**: 192.168.0.x o 192.168.1.x
- **Hotspot Windows**: 192.168.137.x
- **Hotspot Android**: 192.168.43.x
- **Redes empresariales**: 10.x.x.x o 172.16.x.x

## Troubleshooting de Red

### Error: "Connection refused"

1. Verificar que servidor este ejecutandose
2. Verificar IP correcta
3. Verificar firewall
4. Probar con ping

### Error: "Network unreachable"

1. Verificar que esten en la misma red
2. Verificar configuracion WiFi
3. Desactivar VPN si esta activa

### Error: "Connection timeout"

1. Firewall bloqueando puerto
2. Red con aislamiento de clientes (revisar router)
3. IP incorrecta

### Servidor no recibe conexiones

1. Verificar con `netstat` que escuche en 0.0.0.0:6000
2. No debe escuchar solo en 127.0.0.1:6000
3. Verificar que no haya otro programa usando puerto 6000

### Android no encuentra servidor

1. Verificar que WiFi este activado (no datos moviles)
2. Verificar IP del servidor
3. Hacer ping desde otra PC primero
4. Revisar logs con Logcat

## Configuracion Avanzada

### Cambiar Puerto

Si el puerto 6000 esta ocupado:

**En ServidorCentral.java:**
```java
private static final int PUERTO = 7000;  // Cambiar aqui
```

**En MainActivity.kt:**
```kotlin
private const val PUERTO_SERVIDOR = 7000  // Cambiar aqui
```

**En ClienteVigilante.java:**
```kotlin
private static final int PUERTO_SERVIDOR = 7000;  // Cambiar aqui
```

Recompilar todos los componentes.

### Multiples Subredes

Si servidor y clientes estan en subredes diferentes:

1. Configurar enrutamiento en router
2. O usar VPN como Hamachi/ZeroTier
3. O usar redirecci
on de puertos (port forwarding)

### Conexion via Internet (NO recomendado para produccion)

1. Configurar port forwarding en router: Puerto 6000 -> IP servidor
2. Usar IP publica del router
3. Configurar DDNS si IP publica es dinamica
4. **ADVERTENCIA**: Sin encriptacion, no usar para datos sensibles

## Checklist de Despliegue

- [ ] Servidor compilado sin errores
- [ ] IP del servidor obtenida
- [ ] Firewall configurado (puerto 6000 permitido)
- [ ] Servidor ejecutandose (puerto 6000 LISTENING)
- [ ] Apps Android instaladas
- [ ] IP configurada en apps
- [ ] IDs de camara configurados (unicos)
- [ ] Todos en la misma red WiFi/LAN
- [ ] Conectividad verificada (ping exitoso)
- [ ] Primera imagen de prueba enviada exitosamente
- [ ] Dataset recolectado (minimo 20 imagenes/clase)
- [ ] Modelo entrenado sin errores
- [ ] Vigilancia activa enviando frames
- [ ] Cliente Vigilante mostrando detecciones

## Configuracion para Demostracion/Exposicion

### Opcion 1: Hotspot desde Laptop

1. Activar hotspot en laptop (servidor)
2. Conectar todos los Android al hotspot
3. IP del servidor: 192.168.137.1
4. Configurar en apps Android

### Opcion 2: Router Portatil

1. Llevar router portatil
2. Conectar laptop y dispositivos
3. Obtener IP con `ipconfig`
4. Configurar en apps

### Opcion 3: Red del Laboratorio

1. Conectar todos a la red del laboratorio
2. Verificar que permita comunicacion entre clientes
3. Obtener IP del servidor
4. Configurar en apps

## Monitoreo de Red

### Ver conexiones activas

```bash
netstat -an | findstr 6000
```

### Ver logs del servidor

El servidor imprime en consola:
- Cada nueva conexion
- Tipo de mensaje recibido
- Resultados de procesamiento
- Errores

### Usar Wireshark (Opcional)

Para debugging avanzado:
1. Instalar Wireshark
2. Filtrar por `tcp.port == 6000`
3. Analizar trafico TCP

## Notas de Seguridad

- Sistema diseñado para red local confiable
- No incluye autenticacion
- No incluye encriptacion
- No usar en redes publicas
- No exponer a Internet sin medidas adicionales

## Soporte Multi-Camara

El sistema soporta **multiples camaras simultaneas**:
- Cada camara en su propio hilo
- ID unico por camara
- Detecciones independientes
- Registros identifican camara origen

Probado con hasta **10 camaras concurrentes** sin problemas.
