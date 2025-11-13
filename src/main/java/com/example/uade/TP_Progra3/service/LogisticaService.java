package com.example.uade.TP_Progra3.service;

import com.example.uade.TP_Progra3.graph.Arista;
import com.example.uade.TP_Progra3.graph.Graph;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LogisticaService {

    // Construir grafo desde lista de aristas de la BD
    public Graph construirGrafoDesdeAristas(List<Map<String,Object>> aristas) {
        Graph grafo = new Graph();
        for (Map<String,Object> arista : aristas) {
            Object origen = arista.get("from");
            Object destino = arista.get("to");
            Object costo = arista.get("cost");
            
            if (origen == null || destino == null) continue;
            
            String nodoA = origen.toString();
            String nodoB = destino.toString();
            double peso = 1.0;
            
            if (costo != null) {
                try { 
                    peso = Double.parseDouble(costo.toString()); 
                } catch (NumberFormatException ex) { 
                    peso = 1.0; 
                }
            }
            
            grafo.agregarArista(nodoA, nodoB, peso);
        }
        return grafo;
    }

    public Map<String,Object> dijkstraFromEdges(List<Map<String,Object>> aristas, String desde, String hasta) {
        Graph grafo = construirGrafoDesdeAristas(aristas);
        List<String> camino = grafo.dijkstra(desde, hasta);
        
        Map<String,Object> respuesta = new HashMap<>();
        if (camino == null) {
            respuesta.put("path", new ArrayList<>());
            respuesta.put("totalCost", Double.POSITIVE_INFINITY);
            return respuesta;
        }
        
        double costoTotal = grafo.calcularCosto(camino);
        respuesta.put("path", camino);
        respuesta.put("totalCost", costoTotal);
        return respuesta;
    }

    public Map<String,Object> kruskalFromEdges(List<Map<String,Object>> aristas) {
        Graph grafo = construirGrafoDesdeAristas(aristas);
        List<Arista> mst = grafo.kruskalMST();
        
        List<Map<String,Object>> aristasResultado = new ArrayList<>();
        Set<String> nodosConectados = new HashSet<>();
        double costoTotal = 0.0;
        
        for (Arista arista : mst) {
            Map<String,Object> aristaMap = new HashMap<>();
            aristaMap.put("from", arista.origen);
            aristaMap.put("to", arista.destino);
            aristaMap.put("cost", arista.peso);
            aristasResultado.add(aristaMap);
            
            nodosConectados.add(arista.origen);
            nodosConectados.add(arista.destino);
            costoTotal += arista.peso;
        }
        
        Map<String,Object> respuesta = new HashMap<>();
        respuesta.put("edges", aristasResultado);
        respuesta.put("totalCost", costoTotal);
        respuesta.put("nodesCovered", nodosConectados);
        respuesta.put("edgesCount", aristasResultado.size());
        return respuesta;
    }

    public Map<String,Object> primFromEdges(List<Map<String,Object>> aristas, String inicio) {
        Graph grafo = construirGrafoDesdeAristas(aristas);
        List<Arista> mst = grafo.primMST(inicio);
        
        List<Map<String,Object>> aristasResultado = new ArrayList<>();
        Set<String> nodosConectados = new HashSet<>();
        double costoTotal = 0.0;
        
        for (Arista arista : mst) {
            Map<String,Object> aristaMap = new HashMap<>();
            aristaMap.put("from", arista.origen);
            aristaMap.put("to", arista.destino);
            aristaMap.put("cost", arista.peso);
            aristasResultado.add(aristaMap);
            
            nodosConectados.add(arista.origen);
            nodosConectados.add(arista.destino);
            costoTotal += arista.peso;
        }
        
        Map<String,Object> respuesta = new HashMap<>();
        respuesta.put("edges", aristasResultado);
        respuesta.put("totalCost", costoTotal);
        respuesta.put("nodesCovered", nodosConectados);
        respuesta.put("edgesCount", aristasResultado.size());
        return respuesta;
    }

    public Map<String,Object> greedyFromEdges(List<Map<String,Object>> aristas, String inicio) {
        Graph grafo = construirGrafoDesdeAristas(aristas);
        List<String> camino = grafo.tspVecinoCercano(inicio);
        
        Map<String,Object> respuesta = new HashMap<>();
        double costoTotal = grafo.calcularCosto(camino);
        boolean todosVisitados = (camino.size() == grafo.getNodos().size());
        
        respuesta.put("path", camino);
        respuesta.put("totalCost", costoTotal);
        respuesta.put("allNodesVisited", todosVisitados);
        return respuesta;
    }

    public Map<String,Object> floydFromEdges(List<Map<String,Object>> aristas) {
        Graph grafo = construirGrafoDesdeAristas(aristas);
        return grafo.floydWarshall();
    }

    public Map<String,Object> bfsFromEdges(List<Map<String,Object>> aristas, String desde, String hasta) {
        Graph grafo = construirGrafoDesdeAristas(aristas);
        List<String> camino = grafo.bfs(desde, hasta);
        
        Map<String,Object> respuesta = new HashMap<>();
        if (camino == null) {
            respuesta.put("from", desde);
            respuesta.put("to", hasta);
            respuesta.put("path", new ArrayList<>());
            respuesta.put("totalCost", Double.POSITIVE_INFINITY);
            respuesta.put("found", false);
            return respuesta;
        }
        
        double costoTotal = grafo.calcularCosto(camino);
        respuesta.put("from", desde);
        respuesta.put("to", hasta);
        respuesta.put("path", camino);
        respuesta.put("totalCost", costoTotal);
        respuesta.put("found", true);
        return respuesta;
    }

    public Map<String,Object> dfsFromEdges(List<Map<String,Object>> aristas, String desde, String hasta) {
        Graph grafo = construirGrafoDesdeAristas(aristas);
        List<String> camino = grafo.dfs(desde, hasta);
        
        Map<String,Object> respuesta = new HashMap<>();
        if (camino == null) {
            respuesta.put("from", desde);
            respuesta.put("to", hasta);
            respuesta.put("path", new ArrayList<>());
            respuesta.put("totalCost", Double.POSITIVE_INFINITY);
            respuesta.put("found", false);
            return respuesta;
        }
        
        double costoTotal = grafo.calcularCosto(camino);
        respuesta.put("from", desde);
        respuesta.put("to", hasta);
        respuesta.put("path", camino);
        respuesta.put("totalCost", costoTotal);
        respuesta.put("found", true);
        return respuesta;
    }

    public List<Map<String, Object>> sortNodesByDegreeFromEdges(List<Map<String, Object>> aristas) {
        Graph grafo = construirGrafoDesdeAristas(aristas);
        return grafo.ordenarPorGrado();
    }
}
