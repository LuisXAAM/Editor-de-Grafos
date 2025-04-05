package edu.unicda.grafo;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class TablaVecinos extends JScrollPane {
    private final JTable tabla;
    private final DefaultTableModel modelo;

    public TablaVecinos() {
        modelo = new DefaultTableModel(new Object[]{"Vértice", "Vecinos Directos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);  // Vértice
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150); // Vecinos
        
        this.setViewportView(tabla);
        this.setPreferredSize(new Dimension(250, 300));
    }

    public void actualizar(Grafo grafo) {
        modelo.setRowCount(0);
        
        // 1. Obtener todos los vértices ordenados
        List<Integer> vertices = new ArrayList<>(grafo.getVertices());
        Collections.sort(vertices);
        
        // 2. Mapa para almacenar todas las conexiones (incluyendo entrantes)
        Map<Integer, Set<Integer>> conexionesCompletas = new HashMap<>();
        
        // Inicializar el mapa
        for (int v : vertices) {
            conexionesCompletas.put(v, new TreeSet<>());
        }
        
        // 3. Procesar conexiones salientes (las originales)
        for (int origen : vertices) {
            for (Grafo.Arista arista : grafo.getVecinos(origen)) {
                conexionesCompletas.get(origen).add(arista.destino);
                // Si el grafo es no dirigido, agregar la conexión inversa
                conexionesCompletas.get(arista.destino).add(origen);
            }
        }
        
        // 4. Llenar la tabla
        for (int vertice : vertices) {
            Set<Integer> vecinos = conexionesCompletas.get(vertice);
            modelo.addRow(new Object[]{
                vertice,
                vecinos.isEmpty() ? "Aislado" : String.join(", ", vecinos.toString()
                    .replace("[", "").replace("]", ""))
            });
        }
    }
}