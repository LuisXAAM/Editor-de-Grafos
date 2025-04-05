/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.unicda.grafo;

/**
 *
 * @author Luisv
 */




import java.util.*;

public class Grafo {
    private Map<Integer, List<Arista>> listaAdyacencia;

    public Grafo() {
        this.listaAdyacencia = new HashMap<>();
    }

    public void agregarVertice(int vertice) {
        listaAdyacencia.putIfAbsent(vertice, new ArrayList<>());
    }

    public void agregarArista(int origen, int destino, int arist) {
        agregarVertice(origen);
        agregarVertice(destino);
        // Permite múltiples aristas entre los mismos nodos
        listaAdyacencia.get(origen).add(new Arista(destino, arist)); 
    }

    public Set<Integer> getVertices() {
        return listaAdyacencia.keySet();
    }

    public List<Arista> getVecinos(int vertice) {
        return listaAdyacencia.getOrDefault(vertice, new ArrayList<>());
    }

    public static class Arista {
        int destino;
        int arist;
        int id; // Identificador único para aristas paralelas

        public Arista(int destino, int arist) {
            this.destino = destino;
            this.arist = arist;
            this.id = (int) (Math.random() * 1000); // ID único
        }
    }
}