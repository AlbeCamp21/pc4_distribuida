package com.sistema.servidores;

import com.sistema.modelos.Deteccion;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServidorTesteo {
    
    private static final String RUTA_EVIDENCIAS = "evidencias/";
    private static final String RUTA_TEMP = "temp/";
    private static final String RUTA_REGISTROS = "registros/detecciones.txt";
    private static int contadorEvidencias = 0;
    
    public void procesarTesteo(DataInputStream entrada, DataOutputStream salida) {
        try {
            // Leer ID de camara
            String idCamara = entrada.readUTF();
            System.out.println("[TESTEO] Camara: " + idCamara);
            
            // Leer tamaño de la imagen
            int tamanioImagen = entrada.readInt();
            System.out.println("[TESTEO] Tamaño de frame: " + tamanioImagen + " bytes");
            
            // Leer bytes de la imagen
            byte[] bytesImagen = new byte[tamanioImagen];
            entrada.readFully(bytesImagen);
            
            // Guardar imagen temporal
            String rutaTemp = guardarImagenTemporal(bytesImagen);
            
            // Ejecutar inferencia con IA
            String resultadoDeteccion = ejecutarInferencia(rutaTemp);
            
            // Si se detectaron objetos, guardar evidencia y registro
            if (!resultadoDeteccion.equals("NADA")) {
                String rutaEvidencia = guardarEvidencia(bytesImagen);
                registrarDeteccion(idCamara, resultadoDeteccion, rutaEvidencia);
                
                System.out.println("[TESTEO] Deteccion: " + resultadoDeteccion);
                
                // Enviar confirmacion al cliente
                salida.writeUTF("OK");
                salida.writeUTF("Detectado: " + resultadoDeteccion);
            } else {
                salida.writeUTF("OK");
                salida.writeUTF("No se detectaron objetos");
            }
            
            salida.flush();
            
            // Eliminar archivo temporal
            new File(rutaTemp).delete();
            
        } catch (IOException e) {
            System.err.println("[ERROR TESTEO] " + e.getMessage());
            try {
                salida.writeUTF("ERROR: " + e.getMessage());
                salida.flush();
            } catch (IOException ex) {
                System.err.println("[ERROR] Enviando respuesta: " + ex.getMessage());
            }
        }
    }
    
    private String guardarImagenTemporal(byte[] bytesImagen) throws IOException {
        File dirTemp = new File(RUTA_TEMP);
        if (!dirTemp.exists()) {
            dirTemp.mkdirs();
        }
        
        String nombreTemp = "frame_" + System.currentTimeMillis() + ".jpg";
        String rutaCompleta = RUTA_TEMP + nombreTemp;
        
        FileOutputStream fos = new FileOutputStream(rutaCompleta);
        fos.write(bytesImagen);
        fos.close();
        
        return rutaCompleta;
    }
    
    private String ejecutarInferencia(String rutaImagen) {
        try {
            // Construir comando para ejecutar script de inferencia
            String comandoPython = "python scripts_ia/inferencia.py " + rutaImagen;
            
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", comandoPython);
            pb.redirectErrorStream(true);
            
            Process proceso = pb.start();
            
            // Leer salida del proceso
            BufferedReader lector = new BufferedReader(
                new InputStreamReader(proceso.getInputStream())
            );
            
            StringBuilder resultado = new StringBuilder();
            String linea;
            String ultimaLinea = "";
            
            while ((linea = lector.readLine()) != null) {
                System.out.println("[PYTHON INFERENCIA] " + linea);
                ultimaLinea = linea;
            }
            
            int codigoSalida = proceso.waitFor();
            
            if (codigoSalida == 0 && !ultimaLinea.isEmpty()) {
                // La ultima linea contiene el resultado de la deteccion
                return ultimaLinea.trim();
            } else {
                return "NADA";
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Ejecutando inferencia: " + e.getMessage());
            return "NADA";
        }
    }
    
    private synchronized String guardarEvidencia(byte[] bytesImagen) throws IOException {
        File dirEvidencias = new File(RUTA_EVIDENCIAS);
        if (!dirEvidencias.exists()) {
            dirEvidencias.mkdirs();
        }
        
        contadorEvidencias++;
        String nombreArchivo = String.format("evidencia_%04d.jpg", contadorEvidencias);
        String rutaCompleta = RUTA_EVIDENCIAS + nombreArchivo;
        
        FileOutputStream fos = new FileOutputStream(rutaCompleta);
        fos.write(bytesImagen);
        fos.close();
        
        return rutaCompleta;
    }
    
    private synchronized void registrarDeteccion(String idCamara, String objetosDetectados, 
                                                  String rutaImagen) {
        try {
            // Crear directorio de registros si no existe
            File dirRegistros = new File("registros");
            if (!dirRegistros.exists()) {
                dirRegistros.mkdirs();
            }
            
            // Obtener timestamp actual
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String timestamp = sdf.format(new Date());
            
            // Crear registro en formato CSV
            String registro = String.format("%s|%s|%s|%s\n", 
                                          timestamp, idCamara, objetosDetectados, rutaImagen);
            
            // Agregar al archivo de registros
            FileWriter fw = new FileWriter(RUTA_REGISTROS, true);
            fw.write(registro);
            fw.close();
            
            System.out.println("[REGISTRO] Guardado: " + registro.trim());
            
        } catch (IOException e) {
            System.err.println("[ERROR] Registrando deteccion: " + e.getMessage());
        }
    }
    
    public static List<Deteccion> leerRegistros() {
        List<Deteccion> detecciones = new ArrayList<Deteccion>();
        
        try {
            File archivo = new File(RUTA_REGISTROS);
            if (!archivo.exists()) {
                return detecciones;
            }
            
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 4) {
                    Deteccion deteccion = new Deteccion(
                        partes[0],  // timestamp
                        partes[1],  // idCamara
                        partes[2],  // objetosDetectados
                        partes[3]   // rutaImagen
                    );
                    detecciones.add(deteccion);
                }
            }
            
            br.close();
            
        } catch (IOException e) {
            System.err.println("[ERROR] Leyendo registros: " + e.getMessage());
        }
        
        return detecciones;
    }
}
