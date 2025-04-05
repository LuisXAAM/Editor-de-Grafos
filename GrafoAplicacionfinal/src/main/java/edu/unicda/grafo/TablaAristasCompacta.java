package edu.unicda.grafo;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class TablaAristasCompacta extends JScrollPane {
    private final JTable tabla;
    private final DefaultTableModel modelo;

    public TablaAristasCompacta() {
        modelo = new DefaultTableModel(new Object[]{"Vértice", "Aristas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);   // Vértice
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);  // Aristas
        
        this.setViewportView(tabla);
        this.setPreferredSize(new Dimension(250, 300));
    }

    public void actualizar(Grafo grafo) {
        modelo.setRowCount(0);
        
        // 1. Mapa para almacenar TODAS las aristas (origen -> lista de pesos)
        Map<Integer, Set<String>> aristasPorVertice = new HashMap<>();
        
        // Inicializar el mapa con todos los vértices
        for (int vertice : grafo.getVertices()) {
            aristasPorVertice.put(vertice, new TreeSet<>());
        }
        
        // 2. Procesar TODAS las aristas del grafo
        for (int origen : grafo.getVertices()) {
            for (Grafo.Arista arista : grafo.getVecinos(origen)) {
                // Arista saliente (origen -> destino)
                aristasPorVertice.get(origen).add(
                    String.format("e"+ arista.arist)
                );
                
                // Arista entrante (destino -> origen) - para grafos no dirigidos
                aristasPorVertice.get(arista.destino).add(
                    String.format( "e"+arista.arist)
                );
            }
        }
        
        // 3. Ordenar vértices
        List<Integer> verticesOrdenados = new ArrayList<>(aristasPorVertice.keySet());
        Collections.sort(verticesOrdenados);
        
        // 4. Llenar la tabla
        for (int vertice : verticesOrdenados) {
            Set<String> aristas = aristasPorVertice.get(vertice);
            
            modelo.addRow(new Object[]{
                vertice,
                aristas.isEmpty() ? "—" : String.join(", ", aristas)
            });
        }
    }
}