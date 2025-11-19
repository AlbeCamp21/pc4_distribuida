package com.vigilante.modelos;

import javax.swing.ImageIcon;

public class RegistroDeteccion {
    private String timestamp;
    private String idCamara;
    private String objetosDetectados;
    private ImageIcon iconoImagen;
    
    public RegistroDeteccion(String timestamp, String idCamara, 
                            String objetosDetectados, ImageIcon iconoImagen) {
        this.timestamp = timestamp;
        this.idCamara = idCamara;
        this.objetosDetectados = objetosDetectados;
        this.iconoImagen = iconoImagen;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getIdCamara() {
        return idCamara;
    }
    
    public void setIdCamara(String idCamara) {
        this.idCamara = idCamara;
    }
    
    public String getObjetosDetectados() {
        return objetosDetectados;
    }
    
    public void setObjetosDetectados(String objetosDetectados) {
        this.objetosDetectados = objetosDetectados;
    }
    
    public ImageIcon getIconoImagen() {
        return iconoImagen;
    }
    
    public void setIconoImagen(ImageIcon iconoImagen) {
        this.iconoImagen = iconoImagen;
    }
}
