package com.example.uade.TP_Progra3.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Estructura Union-Find para detectar ciclos en grafos
// Se usa principalmente en el algoritmo de Kruskal
public class UnionFind {
    // Mapa que guarda el padre de cada nodo
    private final Map<String, String> padre = new HashMap<>();

    // Constructor: cada nodo empieza siendo su propio padre
    public UnionFind(Set<String> nodos) {
        for (String nodo : nodos) {
            padre.put(nodo, nodo);
        }
    }

    // Busca la raíz del conjunto al que pertenece el nodo
    // Usa compresión de caminos para optimizar búsquedas futuras
    public String buscarRaiz(String nodo) {
        String padreActual = padre.get(nodo);
        
        // Si el nodo no existe, retornamos null
        if (padreActual == null) {
            return null;
        }
        
        // Si el nodo no es su propio padre, buscar recursivamente
        if (!padreActual.equals(nodo)) {
            String raiz = buscarRaiz(padreActual);
            padre.put(nodo, raiz);  // Comprimir el camino
            return raiz;
        }
        
        // El nodo es su propio padre (es la raíz)
        return padreActual;
    }

    // Une dos conjuntos. Retorna true si se unieron, false si ya estaban unidos
    public boolean unir(String nodoA, String nodoB) {
        String raizA = buscarRaiz(nodoA);
        String raizB = buscarRaiz(nodoB);
        
        // Si alguna raíz no existe, no se puede unir
        if (raizA == null || raizB == null) {
            return false;
        }
        
        // Si ya tienen la misma raíz, ya están en el mismo conjunto
        if (raizA.equals(raizB)) {
            return false;
        }
        
        // Unir los conjuntos haciendo que una raíz apunte a la otra
        padre.put(raizA, raizB);
        return true;
    }
}