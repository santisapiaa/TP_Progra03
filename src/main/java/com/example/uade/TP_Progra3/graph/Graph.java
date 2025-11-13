package com.example.uade.TP_Progra3.graph;

import java.util.*;

public class Graph {
    private final Map<String, List<Arista>> listaAdyacencia = new HashMap<>();
    private final Set<String> nodos = new HashSet<>();

    public Set<String> getNodos() { 
        return nodos; 
    }
    
    public Map<String, List<Arista>> getListaAdyacencia() { 
        return listaAdyacencia; 
    }

    // Agregar arista no dirigida entre dos nodos
    public void agregarArista(String nodoA, String nodoB, double peso) {
        nodos.add(nodoA);
        nodos.add(nodoB);
        
        listaAdyacencia.computeIfAbsent(nodoA, k -> new ArrayList<>())
                       .add(new Arista(nodoA, nodoB, peso));
        listaAdyacencia.computeIfAbsent(nodoB, k -> new ArrayList<>())
                       .add(new Arista(nodoB, nodoA, peso));
    }

    // Dijkstra: camino más corto entre dos nodos
    // Análisis de Complejidad:
    // - Inicialización: O(n)
    // - Cada arista se procesa una vez con operaciones PriorityQueue O(log n)
    // - Recurrencia: T(n) = O((n + n) log n)
    // - Conclusión: Complejidad FINAL = O(n log n)
    public List<String> dijkstra(String origen, String destino) {
        if (!nodos.contains(origen) || !nodos.contains(destino)) return null;

        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> anterior = new HashMap<>();
        
        for (String nodo : nodos) {
            distancias.put(nodo, Double.POSITIVE_INFINITY);
        }
        distancias.put(origen, 0.0);
        
        PriorityQueue<String> cola = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));
        cola.add(origen);
        
        while (!cola.isEmpty()) {
            String actual = cola.poll();
            if (actual.equals(destino)) break;
            
            for (Arista arista : listaAdyacencia.getOrDefault(actual, Collections.emptyList())) {
                double nuevaDist = distancias.get(actual) + arista.peso;
                
                if (nuevaDist < distancias.getOrDefault(arista.destino, Double.POSITIVE_INFINITY)) {
                    distancias.put(arista.destino, nuevaDist);
                    anterior.put(arista.destino, actual);
                    cola.remove(arista.destino);
                    cola.add(arista.destino);
                }
            }
        }
        
        if (!anterior.containsKey(destino) && !origen.equals(destino)) return null;
        
        List<String> camino = new ArrayList<>();
        String actual = destino;
        camino.add(actual);
        
        while (anterior.containsKey(actual)) {
            actual = anterior.get(actual);
            camino.add(actual);
        }
        
        Collections.reverse(camino);
        return camino;
    }

    // Kruskal: árbol de expansión mínima
    // Análisis de Complejidad:
    // - Ordenamiento de aristas: O(n log n)
    // - Operaciones UnionFind: O(n α(n)) [casi constante]
    // - Recurrencia: T(n) = O(n log n) [dominante]
    // - Conclusión: Complejidad FINAL = O(n log n)
    public List<Arista> kruskalMST() {
        List<Arista> todasAristas = new ArrayList<>();
        Set<String> vistas = new HashSet<>();
        
        for (Map.Entry<String, List<Arista>> entrada : listaAdyacencia.entrySet()) {
            for (Arista arista : entrada.getValue()) {
                String clave = arista.origen.compareTo(arista.destino) <= 0 
                    ? arista.origen + "|" + arista.destino 
                    : arista.destino + "|" + arista.origen;
                
                if (!vistas.contains(clave)) {
                    vistas.add(clave);
                    todasAristas.add(new Arista(arista.origen, arista.destino, arista.peso));
                }
            }
        }
        
        Collections.sort(todasAristas, Comparator.comparingDouble(e -> e.peso));
        
        UnionFind uf = new UnionFind(nodos);
        List<Arista> mst = new ArrayList<>();
        
        for (Arista arista : todasAristas) {
            if (uf.unir(arista.origen, arista.destino)) {
                mst.add(arista);
            }
        }
        
        return mst;
    }

    // Prim: árbol de expansión mínima desde un nodo inicial
    // Análisis de Complejidad:
    // - PriorityQueue: O(log n) por operación
    // - n aristas procesadas con log n por cada una
    // - Recurrencia: T(n) = O(n log n)
    // - Conclusión: Complejidad FINAL = O(n log n)
    public List<Arista> primMST(String inicio) {
        if (nodos.isEmpty()) return new ArrayList<>();
        
        String nodoInicial = inicio;
        if (nodoInicial == null || !nodos.contains(nodoInicial)) {
            nodoInicial = nodos.iterator().next();
        }
        
        Set<String> visitados = new HashSet<>();
        PriorityQueue<Arista> cola = new PriorityQueue<>(Comparator.comparingDouble(e -> e.peso));
        List<Arista> mst = new ArrayList<>();
        
        visitados.add(nodoInicial);
        cola.addAll(listaAdyacencia.getOrDefault(nodoInicial, Collections.emptyList()));
        
        while (!cola.isEmpty() && visitados.size() < nodos.size()) {
            Arista arista = cola.poll();
            
            if (visitados.contains(arista.destino)) continue;
            
            visitados.add(arista.destino);
            mst.add(arista);
            
            for (Arista siguiente : listaAdyacencia.getOrDefault(arista.destino, Collections.emptyList())) {
                if (!visitados.contains(siguiente.destino)) {
                    cola.add(siguiente);
                }
            }
        }
        
        return mst;
    }

    // Obtener peso de arista entre dos nodos
    // Análisis de Complejidad:
    // - Iteración sobre aristas del nodo: O(grado)
    // - En peor caso: O(n) [n = aristas del nodo origen]
    // - Recurrencia: T(n) = O(1) + O(n) = O(n)
    // - Conclusión: Complejidad FINAL = O(n)
    public double obtenerPeso(String nodoA, String nodoB) {
        List<Arista> aristas = listaAdyacencia.getOrDefault(nodoA, Collections.emptyList());
        double pesoMin = Double.POSITIVE_INFINITY;
        
        for (Arista arista : aristas) {
            if (arista.destino.equals(nodoB)) {
                pesoMin = Math.min(pesoMin, arista.peso);
            }
        }
        
        return pesoMin;
    }

    // Calcular costo total de un camino
    // Análisis de Complejidad:
    // - Bucle n pasos: O(n) donde n es longitud del camino
    // - Cada paso llama obtenerPeso: O(n)
    // - Recurrencia: T(n) = n * O(n) = O(n²)
    // - Conclusión: Complejidad FINAL = O(n²)
    public double calcularCosto(List<String> camino) {
        if (camino == null || camino.size() < 2) return 0.0;
        
        double total = 0.0;
        
        for (int i = 0; i < camino.size() - 1; i++) {
            double peso = obtenerPeso(camino.get(i), camino.get(i + 1));
            if (Double.isInfinite(peso)) return Double.POSITIVE_INFINITY;
            total += peso;
        }
        
        return total;
    }

    // TSP Greedy: vecino más cercano
    // Análisis de Complejidad:
    // - Bucle externo: n nodos no visitados
    // - Bucle interno: n candidatos por cada iteración
    // - Recurrencia: T(n) = n * n = n²
    // - Conclusión: Complejidad FINAL = O(n²)
    public List<String> tspVecinoCercano(String inicio) {
        if (nodos.isEmpty()) return new ArrayList<>();
        
        String actual = inicio;
        if (actual == null || !nodos.contains(actual)) {
            actual = nodos.iterator().next();
        }
        
        Set<String> visitados = new HashSet<>();
        List<String> recorrido = new ArrayList<>();
        
        recorrido.add(actual);
        visitados.add(actual);
        
        while (visitados.size() < nodos.size()) {
            String cercano = null;
            double minCosto = Double.POSITIVE_INFINITY;
            
            for (String candidato : nodos) {
                if (!visitados.contains(candidato)) {
                    double dist = obtenerPeso(actual, candidato);
                    
                    if (dist < minCosto) {
                        minCosto = dist;
                        cercano = candidato;
                    }
                }
            }
            
            if (cercano == null) break;
            
            recorrido.add(cercano);
            visitados.add(cercano);
            actual = cercano;
        }
        
        return recorrido;
    }

    // Floyd-Warshall: todos los caminos más cortos
    // Análisis de Complejidad:
    // - Tres bucles anidados: k, i, j
    // - Cada bucle itera n veces
    // - Recurrencia: T(n) = n * n * n = n³
    // - Conclusión: Complejidad FINAL = O(n³)
    public Map<String, Object> floydWarshall() {
        List<String> listaNodos = new ArrayList<>(nodos);
        int n = listaNodos.size();
        double[][] dist = new double[n][n];
        
        // Inicializar matriz
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0.0;
                } else {
                    dist[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
        
        // Llenar con aristas existentes
        for (int i = 0; i < n; i++) {
            String origen = listaNodos.get(i);
            for (int j = 0; j < n; j++) {
                String destino = listaNodos.get(j);
                double peso = obtenerPeso(origen, destino);
                
                if (!Double.isInfinite(peso)) {
                    dist[i][j] = peso;
                }
            }
        }
        
        // Algoritmo principal
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        
        // Convertir a mapa
        Map<String, Object> resultado = new HashMap<>();
        Map<String, Map<String, Double>> matriz = new HashMap<>();
        
        for (int i = 0; i < n; i++) {
            Map<String, Double> fila = new HashMap<>();
            for (int j = 0; j < n; j++) {
                fila.put(listaNodos.get(j), dist[i][j]);
            }
            matriz.put(listaNodos.get(i), fila);
        }
        
        resultado.put("caminosMinimos", matriz);
        return resultado;
    }

    // BFS: búsqueda en amplitud
    // Análisis de Complejidad:
    // - Cada nodo visitado una vez: O(n)
    // - Cada arista explorada una vez: O(n)
    // - Recurrencia: T(n) = O(n) + O(n) = O(n)
    // - Conclusión: Complejidad FINAL = O(n)
    public List<String> bfs(String inicio, String destino) {
        if (!nodos.contains(inicio) || !nodos.contains(destino)) return null;
        if (inicio.equals(destino)) return new ArrayList<>(List.of(inicio));
        
        Queue<List<String>> cola = new LinkedList<>();
        cola.add(new ArrayList<>(List.of(inicio)));
        
        Set<String> visitados = new HashSet<>();
        visitados.add(inicio);
        
        while (!cola.isEmpty()) {
            List<String> camino = cola.poll();
            String ultimo = camino.get(camino.size() - 1);
            
            for (Arista arista : listaAdyacencia.getOrDefault(ultimo, Collections.emptyList())) {
                String vecino = arista.destino;
                
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    
                    List<String> nuevoCamino = new ArrayList<>(camino);
                    nuevoCamino.add(vecino);
                    
                    if (vecino.equals(destino)) return nuevoCamino;
                    
                    cola.add(nuevoCamino);
                }
            }
        }
        
        return null;
    }

    // DFS: búsqueda en profundidad
    // Análisis de Complejidad (Recurrencia por sustracción):
    // - T(n) = T(n-1) + O(grado(v))
    // - Suma de todos los grados = 2n
    // - Recurrencia: T(n) = O(n) + O(n)
    // - Conclusión: Complejidad FINAL = O(n)
    public List<String> dfs(String inicio, String destino) {
        if (!nodos.contains(inicio) || !nodos.contains(destino)) return null;
        if (inicio.equals(destino)) return new ArrayList<>(List.of(inicio));
        
        Set<String> visitados = new HashSet<>();
        List<String> camino = new ArrayList<>();
        
        if (dfsRecursivo(inicio, destino, visitados, camino)) {
            return camino;
        }
        
        return null;
    }

    private boolean dfsRecursivo(String actual, String destino, Set<String> visitados, List<String> camino) {
        visitados.add(actual);
        camino.add(actual);
        
        if (actual.equals(destino)) return true;
        
        for (Arista arista : listaAdyacencia.getOrDefault(actual, Collections.emptyList())) {
            if (!visitados.contains(arista.destino)) {
                if (dfsRecursivo(arista.destino, destino, visitados, camino)) {
                    return true;
                }
            }
        }
        
        camino.remove(camino.size() - 1);
        return false;
    }

    // Ordenar nodos por cantidad de conexiones usando Divide y Vencerás (MergeSort)
    // Análisis de Complejidad (Master Theorem):
    // - Recurrencia: T(n) = 2*T(n/2) + O(n)
    // - Parámetros: a=2, b=2, k=1
    // - Caso: a = b^k => 2 = 2^1 ✓
    // - Según Master Theorem: T(n) ∈ O(n^k log n) = O(n log n)
    // - Conclusión: Complejidad FINAL = O(n log n)
    public List<Map<String, Object>> ordenarPorGrado() {
        List<Map<String, Object>> lista = new ArrayList<>();
        
        for (String nodo : nodos) {
            int grado = listaAdyacencia.getOrDefault(nodo, Collections.emptyList()).size();
            
            Map<String, Object> info = new HashMap<>();
            info.put("almacen", nodo);
            info.put("conexiones", grado);
            lista.add(info);
        }
        
        // Aplicar MergeSort (divide y vencerás)
        return mergeSort(lista, 0, lista.size() - 1);
    }
    
    // Divide: partir la lista en mitades y ordenar recursivamente
    private List<Map<String, Object>> mergeSort(List<Map<String, Object>> lista, int inicio, int fin) {
        if (inicio >= fin) {
            return new ArrayList<>(List.of(lista.get(inicio)));
        }
        
        // Dividir en dos mitades
        int medio = inicio + (fin - inicio) / 2;
        
        List<Map<String, Object>> izquierda = mergeSort(lista, inicio, medio);
        List<Map<String, Object>> derecha = mergeSort(lista, medio + 1, fin);
        
        // Vencerás: combinar las dos mitades ordenadas
        return combinar(izquierda, derecha);
    }
    
    // Combinar dos listas ordenadas en una sola lista ordenada
    private List<Map<String, Object>> combinar(List<Map<String, Object>> izquierda, List<Map<String, Object>> derecha) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        int i = 0, j = 0;
        
        // Comparar elemento por elemento y agregar el mayor
        while (i < izquierda.size() && j < derecha.size()) {
            int conexionesIzq = (int) izquierda.get(i).get("conexiones");
            int conexionesDer = (int) derecha.get(j).get("conexiones");
            
            if (conexionesIzq >= conexionesDer) {
                resultado.add(izquierda.get(i));
                i++;
            } else {
                resultado.add(derecha.get(j));
                j++;
            }
        }
        
        // Agregar elementos restantes
        while (i < izquierda.size()) {
            resultado.add(izquierda.get(i));
            i++;
        }
        
        while (j < derecha.size()) {
            resultado.add(derecha.get(j));
            j++;
        }
        
        return resultado;
    }
}