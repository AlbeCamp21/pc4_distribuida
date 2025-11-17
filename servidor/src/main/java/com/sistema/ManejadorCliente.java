package com.sistema;

import com.sistema.servidores.*;
import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    
    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;
    
    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            // Inicializar streams
            entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            salida = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            
            // Leer cabecera del protocolo
            String tipoMensaje = entrada.readUTF();
            
            System.out.println("[HILO-" + Thread.currentThread().getId() + 
                             "] Tipo de mensaje recibido: " + tipoMensaje);
            
            // Rutear segun el tipo de mensaje
            if (tipoMensaje.equals("TRAINING")) {
                manejarEntrenamiento();
            } else if (tipoMensaje.equals("TEST")) {
                manejarTesteo();
            } else if (tipoMensaje.equals("GET_REGISTROS")) {
                manejarConsultaRegistros();
            } else {
                System.err.println("[ERROR] Tipo de mensaje desconocido: " + tipoMensaje);
                salida.writeUTF("ERROR: Tipo de mensaje no reconocido");
                salida.flush();
            }
            
        } catch (IOException e) {
            System.err.println("[ERROR] En manejo de cliente: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }
    
    private void manejarEntrenamiento() throws IOException {
        ServidorEntrenamiento servidorEntrenamiento = new ServidorEntrenamiento();
        servidorEntrenamiento.procesarEntrenamiento(entrada, salida);
    }
    
    private void manejarTesteo() throws IOException {
        ServidorTesteo servidorTesteo = new ServidorTesteo();
        servidorTesteo.procesarTesteo(entrada, salida);
    }
    
    private void manejarConsultaRegistros() throws IOException {
        ServidorVideo servidorVideo = new ServidorVideo();
        servidorVideo.enviarRegistros(salida);
    }
    
    private void cerrarConexion() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null && !socket.isClosed()) socket.close();
            
            System.out.println("[HILO-" + Thread.currentThread().getId() + 
                             "] Conexion cerrada");
        } catch (IOException e) {
            System.err.println("[ERROR] Cerrando conexion: " + e.getMessage());
        }
    }
}
