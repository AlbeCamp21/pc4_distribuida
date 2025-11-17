package com.sistema;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorCentral {
    
    private static final int PUERTO = 6000;
    private static final int MAX_HILOS = 50;
    
    private ServerSocket serverSocket;
    private ExecutorService poolHilos;
    private boolean ejecutando;
    
    public ServidorCentral() {
        this.poolHilos = Executors.newFixedThreadPool(MAX_HILOS);
        this.ejecutando = false;
    }
    
    public void iniciar() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            ejecutando = true;
            
            System.out.println("===========================================");
            System.out.println("   SERVIDOR CENTRAL INICIADO");
            System.out.println("   Puerto: " + PUERTO);
            System.out.println("   Esperando conexiones de clientes...");
            System.out.println("===========================================");
            
            // Crear directorios necesarios si no existen
            crearDirectorios();
            
            while (ejecutando) {
                try {
                    // Aceptar conexion de cliente
                    Socket clienteSocket = serverSocket.accept();
                    
                    System.out.println("\n[NUEVA CONEXION] Cliente conectado desde: " + 
                                     clienteSocket.getInetAddress().getHostAddress());
                    
                    // Asignar un hilo del pool para manejar este cliente
                    poolHilos.execute(new ManejadorCliente(clienteSocket));
                    
                } catch (IOException e) {
                    if (ejecutando) {
                        System.err.println("[ERROR] Error aceptando cliente: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("[ERROR CRITICO] No se pudo iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void crearDirectorios() {
        String[] directorios = {
            "dataset/persona",
            "dataset/perro", 
            "dataset/celular",
            "evidencias",
            "modelos",
            "registros",
            "temp"
        };
        
        for (String dir : directorios) {
            File directorio = new File(dir);
            if (!directorio.exists()) {
                if (directorio.mkdirs()) {
                    System.out.println("[DIRECTORIO] Creado: " + dir);
                }
            }
        }
    }
    
    public void detener() {
        ejecutando = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            poolHilos.shutdown();
            if (!poolHilos.awaitTermination(5, TimeUnit.SECONDS)) {
                poolHilos.shutdownNow();
            }
            System.out.println("\n[SERVIDOR] Detenido correctamente");
        } catch (Exception e) {
            System.err.println("[ERROR] Deteniendo servidor: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        ServidorCentral servidor = new ServidorCentral();
        
        // Agregar hook para detener el servidor al cerrar
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                servidor.detener();
            }
        });
        
        servidor.iniciar();
    }
}
