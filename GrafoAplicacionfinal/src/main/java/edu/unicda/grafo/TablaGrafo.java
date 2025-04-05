// TablaGrafo.java
package edu.unicda.grafo;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class TablaGrafo extends JScrollPane {
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    public TablaGrafo() {
        // Configuración del modelo con columnas personalizadas
        modeloTabla = new DefaultTableModel(new Object[]{"Arista", "Puntos extremos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla = new JTable(modeloTabla);
        tabla.setAutoCreateRowSorter(true);
        
        // Diseño ajustado
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);  // Columna "Arista"
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150); // Columna "Puntos extremos"
        
        this.setViewportView(tabla);
        this.setPreferredSize(new Dimension(200, 100));
    }

    public void actualizar(Grafo grafo) {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        // Contador para nombres de aristas (e1, e2, ...)
        int contadorAristas = 1;
        
        // Agregar filas para cada arista
        for (int origen : grafo.getVertices()) {
            for (Grafo.Arista arista : grafo.getVecinos(origen)) {
                String puntosExtremos;
                if (origen == arista.destino) {
                    puntosExtremos = String.format("{%d}", origen); // Lazos
                } else {
                    puntosExtremos = String.format("{%d, %d}", origen, arista.destino); // Aristas normales
                }
                
                modeloTabla.addRow(new Object[]{
                    "e" + contadorAristas++, // Nombre secuencial (e1, e2,...)
                    puntosExtremos
                });
            }
        }
    }
}