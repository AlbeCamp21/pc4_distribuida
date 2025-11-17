package com.sistema.servidores;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServidorEntrenamiento {
    
    private static final String RUTA_DATASET = "dataset/";
    private static int contadorPersona = 0;
    private static int contadorPerro = 0;
    private static int contadorCelular = 0;
    
    public void procesarEntrenamiento(DataInputStream entrada, DataOutputStream salida) {
        try {
            // Leer etiqueta de la clase
            String etiqueta = entrada.readUTF().toLowerCase();
            System.out.println("[ENTRENAMIENTO] Etiqueta recibida: " + etiqueta);
            
            // Validar etiqueta
            if (!etiquetaValida(etiqueta)) {
                salida.writeUTF("ERROR: Etiqueta no valida. Use: persona, perro o celular");
                salida.flush();
                return;
            }
            
            // Leer tamaño de la imagen
            int tamanioImagen = entrada.readInt();
            System.out.println("[ENTRENAMIENTO] Tamaño de imagen: " + tamanioImagen + " bytes");
            
            // Leer bytes de la imagen
            byte[] bytesImagen = new byte[tamanioImagen];
            entrada.readFully(bytesImagen);
            
            // Guardar imagen en el dataset
            String rutaArchivo = guardarImagen(etiqueta, bytesImagen);
            
            // Enviar confirmacion al cliente
            salida.writeUTF("OK");
            salida.writeUTF("Imagen guardada: " + rutaArchivo);
            salida.flush();
            
            System.out.println("[ENTRENAMIENTO] Imagen guardada exitosamente: " + rutaArchivo);
            
        } catch (IOException e) {
            System.err.println("[ERROR ENTRENAMIENTO] " + e.getMessage());
            try {
                salida.writeUTF("ERROR: " + e.getMessage());
                salida.flush();
            } catch (IOException ex) {
                System.err.println("[ERROR] Enviando respuesta de error: " + ex.getMessage());
            }
        }
    }
    
    private boolean etiquetaValida(String etiqueta) {
        return etiqueta.equals("persona") || 
               etiqueta.equals("perro") || 
               etiqueta.equals("celular");
    }
    
    private synchronized String guardarImagen(String etiqueta, byte[] bytesImagen) throws IOException {
        // Incrementar contador segun la etiqueta
        int numero = 0;
        if (etiqueta.equals("persona")) {
            numero = ++contadorPersona;
        } else if (etiqueta.equals("perro")) {
            numero = ++contadorPerro;
        } else if (etiqueta.equals("celular")) {
            numero = ++contadorCelular;
        }
        
        // Crear ruta de archivo
        String nombreArchivo = String.format("img_%04d.jpg", numero);
        String rutaCompleta = RUTA_DATASET + etiqueta + "/" + nombreArchivo;
        
        // Verificar que el directorio existe
        File directorio = new File(RUTA_DATASET + etiqueta);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        
        // Guardar archivo
        FileOutputStream fos = new FileOutputStream(rutaCompleta);
        fos.write(bytesImagen);
        fos.close();
        
        return rutaCompleta;
    }
    
    public static void entrenarModelo() {
        try {
            System.out.println("\n[ENTRENAMIENTO IA] Iniciando entrenamiento del modelo...");
            
            // Construir comando para ejecutar script de Python
            String comandoPython = "python scripts_ia/entrenamiento.py";
            
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", comandoPython);
            pb.redirectErrorStream(true);
            
            Process proceso = pb.start();
            
            // Leer salida del proceso
            BufferedReader lector = new BufferedReader(
                new InputStreamReader(proceso.getInputStream())
            );
            
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println("[PYTHON] " + linea);
            }
            
            int codigoSalida = proceso.waitFor();
            
            if (codigoSalida == 0) {
                System.out.println("[ENTRENAMIENTO IA] Completado exitosamente");
            } else {
                System.err.println("[ENTRENAMIENTO IA] Error con codigo: " + codigoSalida);
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Entrenando modelo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
