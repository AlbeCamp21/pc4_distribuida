package com.vigilante;

import com.vigilante.modelos.RegistroDeteccion;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ModeloTablaDetecciones extends AbstractTableModel {
    
    private String[] columnas = {"Imagen", "Deteccion", "Camara", "Fecha y Hora"};
    private List<RegistroDeteccion> registros;
    
    public ModeloTablaDetecciones() {
        this.registros = new ArrayList<RegistroDeteccion>();
    }
    
    @Override
    public int getRowCount() {
        return registros.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnas.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return javax.swing.ImageIcon.class;
        }
        return String.class;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RegistroDeteccion registro = registros.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return registro.getIconoImagen();
            case 1: return registro.getObjetosDetectados();
            case 2: return registro.getIdCamara();
            case 3: return registro.getTimestamp();
            default: return null;
        }
    }
    
    public void setRegistros(List<RegistroDeteccion> registros) {
        this.registros = registros;
        fireTableDataChanged();
    }
    
    public void agregarRegistro(RegistroDeteccion registro) {
        this.registros.add(registro);
        fireTableRowsInserted(registros.size() - 1, registros.size() - 1);
    }
    
    public void limpiar() {
        this.registros.clear();
        fireTableDataChanged();
    }
}
