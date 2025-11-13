package com.example.uade.TP_Progra3.graph;

// Representa una arista (conexión) entre dos nodos del grafo
public final class Arista {
    public final String origen;
    public final String destino;
    public final double peso;

    public Arista(String origen, String destino, double peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }
}