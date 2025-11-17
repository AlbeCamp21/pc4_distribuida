package com.sistema.servidores;

import com.sistema.modelos.Deteccion;
import java.io.*;
import java.util.List;

public class ServidorVideo {
    
    public void enviarRegistros(DataOutputStream salida) {
        try {
            // Obtener todos los registros
            List<Deteccion> detecciones = ServidorTesteo.leerRegistros();
            
            // Enviar cantidad de registros
            salida.writeInt(detecciones.size());
            
            // Enviar cada registro
            for (Deteccion deteccion : detecciones) {
                salida.writeUTF(deteccion.getTimestamp());
                salida.writeUTF(deteccion.getIdCamara());
                salida.writeUTF(deteccion.getObjetosDetectados());
                salida.writeUTF(deteccion.getRutaImagen());
                
                // Leer y enviar bytes de la imagen
                File archivoImagen = new File(deteccion.getRutaImagen());
                if (archivoImagen.exists()) {
                    byte[] bytesImagen = leerArchivoImagen(archivoImagen);
                    salida.writeInt(bytesImagen.length);
                    salida.write(bytesImagen);
                } else {
                    salida.writeInt(0);  // No hay imagen
                }
            }
            
            salida.flush();
            
            System.out.println("[SERVIDOR VIDEO] Enviados " + detecciones.size() + " registros");
            
        } catch (IOException e) {
            System.err.println("[ERROR SERVIDOR VIDEO] " + e.getMessage());
        }
    }
    
    private byte[] leerArchivoImagen(File archivo) throws IOException {
        FileInputStream fis = new FileInputStream(archivo);
        byte[] bytes = new byte[(int) archivo.length()];
        fis.read(bytes);
        fis.close();
        return bytes;
    }
}
