package com.sistema.modelos;

public class Deteccion {
    private String timestamp;
    private String idCamara;
    private String objetosDetectados;
    private String rutaImagen;
    
    public Deteccion(String timestamp, String idCamara, String objetosDetectados, String rutaImagen) {
        this.timestamp = timestamp;
        this.idCamara = idCamara;
        this.objetosDetectados = objetosDetectados;
        this.rutaImagen = rutaImagen;
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
    
    public String getRutaImagen() {
        return rutaImagen;
    }
    
    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
    
    @Override
    public String toString() {
        return "Deteccion{" +
               "timestamp='" + timestamp + '\'' +
               ", idCamara='" + idCamara + '\'' +
               ", objetosDetectados='" + objetosDetectados + '\'' +
               ", rutaImagen='" + rutaImagen + '\'' +
               '}';
    }
}
