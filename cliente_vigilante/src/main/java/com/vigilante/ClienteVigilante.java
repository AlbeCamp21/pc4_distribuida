package com.vigilante;

import com.vigilante.modelos.RegistroDeteccion;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class ClienteVigilante extends JFrame {
    
    private static final String HOST_SERVIDOR = "localhost";
    private static final int PUERTO_SERVIDOR = 6000;
    
    private JTable tablaDetecciones;
    private ModeloTablaDetecciones modeloTabla;
    private JButton botonRefrescar;
    private JButton botonConectar;
    private JLabel labelEstado;
    
    public ClienteVigilante() {
        configurarVentana();
        crearComponentes();
        layoutComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Sistema de Vigilancia - Cliente Vigilante");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void crearComponentes() {
        // Crear modelo de tabla personalizado
        modeloTabla = new ModeloTablaDetecciones();
        
        // Crear tabla
        tablaDetecciones = new JTable(modeloTabla);
        tablaDetecciones.setRowHeight(100);  // Altura para mostrar miniaturas
        tablaDetecciones.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Configurar renderizador para imagenes
        tablaDetecciones.getColumnModel().getColumn(0).setCellRenderer(new RenderizadorImagen());
        
        // Ajustar anchos de columnas
        tablaDetecciones.getColumnModel().getColumn(0).setPreferredWidth(120);  // Imagen
        tablaDetecciones.getColumnModel().getColumn(1).setPreferredWidth(200);  // Deteccion
        tablaDetecciones.getColumnModel().getColumn(2).setPreferredWidth(150);  // Camara
        tablaDetecciones.getColumnModel().getColumn(3).setPreferredWidth(180);  // Timestamp
        
        // Crear botones
        botonRefrescar = new JButton("Refrescar Datos");
        botonRefrescar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarDatos();
            }
        });
        
        botonConectar = new JButton("Conectar al Servidor");
        botonConectar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarDatos();
            }
        });
        
        // Label de estado
        labelEstado = new JLabel("Desconectado");
        labelEstado.setForeground(Color.RED);
    }
    
    private void layoutComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con botones y estado
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(botonConectar);
        panelSuperior.add(botonRefrescar);
        panelSuperior.add(new JLabel("Estado: "));
        panelSuperior.add(labelEstado);
        
        // Agregar componentes al frame
        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tablaDetecciones), BorderLayout.CENTER);
        
        // Panel inferior con informacion
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel labelInfo = new JLabel("Total de detecciones: 0");
        panelInferior.add(labelInfo);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        // Ejecutar en hilo separado para no bloquear la GUI
        new Thread(new Runnable() {
            public void run() {
                Socket socket = null;
                try {
                    labelEstado.setText("Conectando...");
                    labelEstado.setForeground(Color.ORANGE);
                    
                    // Conectar al servidor
                    socket = new Socket(HOST_SERVIDOR, PUERTO_SERVIDOR);
                    
                    DataOutputStream salida = new DataOutputStream(
                        new BufferedOutputStream(socket.getOutputStream())
                    );
                    DataInputStream entrada = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream())
                    );
                    
                    // Enviar solicitud de registros
                    salida.writeUTF("GET_REGISTROS");
                    salida.flush();
                    
                    // Recibir cantidad de registros
                    int cantidadRegistros = entrada.readInt();
                    
                    List<RegistroDeteccion> registros = new ArrayList<RegistroDeteccion>();
                    
                    // Recibir cada registro
                    for (int i = 0; i < cantidadRegistros; i++) {
                        String timestamp = entrada.readUTF();
                        String idCamara = entrada.readUTF();
                        String objetosDetectados = entrada.readUTF();
                        String rutaImagen = entrada.readUTF();
                        
                        // Recibir bytes de la imagen
                        int tamanioImagen = entrada.readInt();
                        ImageIcon icono = null;
                        
                        if (tamanioImagen > 0) {
                            byte[] bytesImagen = new byte[tamanioImagen];
                            entrada.readFully(bytesImagen);
                            
                            // Crear ImageIcon desde bytes
                            ImageIcon iconoOriginal = new ImageIcon(bytesImagen);
                            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(
                                100, 80, Image.SCALE_SMOOTH
                            );
                            icono = new ImageIcon(imagenEscalada);
                        }
                        
                        RegistroDeteccion registro = new RegistroDeteccion(
                            timestamp, idCamara, objetosDetectados, icono
                        );
                        registros.add(registro);
                    }
                    
                    // Actualizar tabla en el hilo de la GUI
                    final List<RegistroDeteccion> registrosFinales = registros;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            modeloTabla.setRegistros(registrosFinales);
                            labelEstado.setText("Conectado - " + registrosFinales.size() + " registros");
                            labelEstado.setForeground(Color.GREEN);
                        }
                    });
                    
                } catch (IOException e) {
                    final String mensaje = e.getMessage();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            labelEstado.setText("Error: " + mensaje);
                            labelEstado.setForeground(Color.RED);
                            JOptionPane.showMessageDialog(
                                ClienteVigilante.this,
                                "Error conectando al servidor:\n" + mensaje,
                                "Error de Conexion",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    });
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    
    public static void main(String[] args) {
        // Usar look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClienteVigilante cliente = new ClienteVigilante();
                cliente.setVisible(true);
            }
        });
    }
}
