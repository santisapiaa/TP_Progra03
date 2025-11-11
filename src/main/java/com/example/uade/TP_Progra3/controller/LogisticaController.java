package com.example.uade.TP_Progra3.controller;

import com.example.uade.TP_Progra3.entity.AlmacenEntity;
import com.example.uade.TP_Progra3.repository.AlmacenRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Collections;
// removed unused import

@RestController
@RequestMapping("/logistica")
public class LogisticaController {

    private final AlmacenRepository almacenRepository;

    public LogisticaController(AlmacenRepository almacenRepository) {
        this.almacenRepository = almacenRepository;
    }

    // Endpoint para listar todos los almacenes
    @GetMapping(value = "/almacenes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<AlmacenEntity> getAllAlmacenes() {
        return almacenRepository.findAll();
    }

    // Endpoint para Algoritmo BFS -> devuelve una lista de caminos (cada camino es una lista de nodos con sus propiedades)
    @GetMapping(value = "/bfs/{nombre}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<List<Map<String, Object>>>> bfs(@PathVariable String nombre, @RequestParam(defaultValue = "3") int depth) {
        return almacenRepository.findBfsPath(nombre, depth)
                .map(m -> (List<List<Map<String, Object>>>) m.get("paths"));
    }

    // Endpoint para Algoritmo DFS -> devuelve una lista de caminos (cada camino es una lista de nodos con sus propiedades)
    @GetMapping(value = "/dfs/{nombre}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<List<Map<String, Object>>>> dfs(@PathVariable String nombre, @RequestParam(defaultValue = "3") int depth) {
        return almacenRepository.findDfsPath(nombre, depth)
                .map(m -> (List<List<Map<String, Object>>>) m.get("paths"));
    }

    // Endpoint Dijkstra: camino de costo mínimo entre from y to
    @GetMapping(value = "/dijkstra/{from}/{to}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> dijkstra(@PathVariable String from, @PathVariable String to) {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> {
                    Graph g = buildGraphFromEdges(edges);
                    List<String> path = g.dijkstra(from, to);
                    Map<String,Object> resp = new HashMap<>();
                    if (path == null) {
                        resp.put("path", new ArrayList<>());
                        resp.put("totalCost", Double.POSITIVE_INFINITY);
                        return resp;
                    }
                    double total = g.pathCost(path);
                    resp.put("path", path);
                    resp.put("totalCost", total);
                    return resp;
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    // Endpoint Kruskal: árbol generador mínimo (puede ser bosque si el grafo está desconectado)
    @GetMapping(value = "/kruskal", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> kruskal() {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> {
                    Graph g = buildGraphFromEdges(edges);
                    List<Edge> mst = g.kruskalMST();
                    List<Map<String,Object>> out = new ArrayList<>();
                    Set<String> covered = new HashSet<>();
                    double total = 0.0;
                    for (Edge e : mst) {
                        Map<String,Object> m = new HashMap<>();
                        m.put("from", e.u);
                        m.put("to", e.v);
                        m.put("cost", e.weight);
                        out.add(m);
                        covered.add(e.u); covered.add(e.v);
                        total += e.weight;
                    }
                    Map<String,Object> resp = new HashMap<>();
                    resp.put("edges", out);
                    resp.put("totalCost", total);
                    resp.put("nodesCovered", covered);
                    resp.put("edgesCount", out.size());
                    return resp;
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    // Endpoint Prim: árbol generador mínimo usando Prim (opcional start)
    @GetMapping(value = "/prim", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> prim(@RequestParam(required = false) String start) {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> {
                    Graph g = buildGraphFromEdges(edges);
                    List<Edge> mst = g.primMST(start);
                    List<Map<String,Object>> out = new ArrayList<>();
                    Set<String> covered = new HashSet<>();
                    double total = 0.0;
                    for (Edge e : mst) {
                        Map<String,Object> m = new HashMap<>();
                        m.put("from", e.u);
                        m.put("to", e.v);
                        m.put("cost", e.weight);
                        out.add(m);
                        covered.add(e.u); covered.add(e.v);
                        total += e.weight;
                    }
                    Map<String,Object> resp = new HashMap<>();
                    resp.put("edges", out);
                    resp.put("totalCost", total);
                    resp.put("nodesCovered", covered);
                    resp.put("edgesCount", out.size());
                    return resp;
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    // Endpoint raw edges (crudo) para depuración: devuelve exactamente lo que llega de Neo4j
    @GetMapping(value = "/edges", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Map<String, Object>>> edges() {
        return almacenRepository.findAllEdges().collectList();
    }

    // Endpoint TSP Greedy (Nearest Neighbor): visita todos los nodos eligiendo siempre el más cercano no visitado
    @GetMapping(value = "/greedy", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> tsp(@RequestParam(required = false) String start) {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> {
                    Graph g = buildGraphFromEdges(edges);
                    List<String> path = g.tspNearestNeighbor(start);
                    Map<String,Object> resp = new HashMap<>();
                    double total = g.pathCost(path);
                    boolean allVisited = (path.size() == g.nodes.size());
                    resp.put("path", path);
                    resp.put("totalCost", total);
                    resp.put("allNodesVisited", allVisited);
                    return resp;
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    // Endpoint Divide y Vencerás (Mergesort): ordena almacenes alfabéticamente (ejemplo de D&C)
    @GetMapping(value = "/divide-conquer", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<String>> divideConquerSort() {
        return almacenRepository.findAll()
                .map(AlmacenEntity::getNombre)
                .collectList()
                .flatMap(names -> Mono.fromCallable(() -> {
                    List<String> sorted = new ArrayList<>(names);
                    mergeSort(sorted, 0, sorted.size() - 1);
                    return sorted;
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    // Endpoint Programación Dinámica: Floyd-Warshall (matriz de caminos más cortos entre todos los pares)
    @GetMapping(value = "/dynamic-programming", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> floydWarshall() {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> {
                    Graph g = buildGraphFromEdges(edges);
                    Map<String, Object> result = g.floydWarshall();
                    return result;
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    private static class Edge {
        final String u;
        final String v;
        final double weight;

        Edge(String u, String v, double weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }

    private static class Graph {
        private final Map<String, List<Edge>> adj = new HashMap<>();
        private final Set<String> nodes = new HashSet<>();

        void addEdge(String a, String b, double w) {
            nodes.add(a); nodes.add(b);
            adj.computeIfAbsent(a, k -> new ArrayList<>()).add(new Edge(a,b,w));
            adj.computeIfAbsent(b, k -> new ArrayList<>()).add(new Edge(b,a,w));
        }

        List<String> dijkstra(String src, String dst) {
            if (!nodes.contains(src) || !nodes.contains(dst)) return null;
            Map<String, Double> dist = new HashMap<>();
            Map<String, String> prev = new HashMap<>();
            for (String n : nodes) dist.put(n, Double.POSITIVE_INFINITY);
            dist.put(src, 0.0);
            PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
            pq.add(src);
            while (!pq.isEmpty()) {
                String u = pq.poll();
                double du = dist.get(u);
                if (u.equals(dst)) break;
                List<Edge> edges = adj.getOrDefault(u, Collections.emptyList());
                for (Edge e : edges) {
                    String v = e.v;
                    double alt = du + e.weight;
                    if (alt < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                        dist.put(v, alt);
                        prev.put(v, u);
                        pq.remove(v); // remove if present
                        pq.add(v);
                    }
                }
            }
            if (!prev.containsKey(dst) && !src.equals(dst)) return null;
            List<String> path = new ArrayList<>();
            String cur = dst;
            path.add(cur);
            while (prev.containsKey(cur)) {
                cur = prev.get(cur);
                path.add(cur);
            }
            Collections.reverse(path);
            return path;
        }

        List<Edge> kruskalMST() {
            List<Edge> all = new ArrayList<>();
            Set<String> seenPairs = new HashSet<>();
            for (Map.Entry<String, List<Edge>> en : adj.entrySet()) {
                for (Edge e : en.getValue()) {
                    String a = e.u; String b = e.v;
                    String key = a.compareTo(b) <= 0 ? a+"|"+b : b+"|"+a;
                    if (seenPairs.contains(key)) continue;
                    seenPairs.add(key);
                    all.add(new Edge(a,b,e.weight));
                }
            }
            Collections.sort(all, Comparator.comparingDouble(e -> e.weight));
            UnionFind uf = new UnionFind(nodes);
            List<Edge> mst = new ArrayList<>();
            for (Edge e : all) {
                if (uf.union(e.u, e.v)) {
                    mst.add(e);
                }
            }
            return mst;
        }

        List<Edge> primMST(String start) {
            if (nodes.isEmpty()) return new ArrayList<>();
            String s = start;
            if (s == null || !nodes.contains(s)) s = nodes.iterator().next();
            Set<String> visited = new HashSet<>();
            PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.weight));
            List<Edge> mst = new ArrayList<>();
            visited.add(s);
            pq.addAll(adj.getOrDefault(s, Collections.emptyList()));
            while (!pq.isEmpty() && visited.size() < nodes.size()) {
                Edge e = pq.poll();
                if (visited.contains(e.v)) continue;
                visited.add(e.v);
                mst.add(e);
                for (Edge next : adj.getOrDefault(e.v, Collections.emptyList())) {
                    if (!visited.contains(next.v)) pq.add(next);
                }
            }
            return mst;
        }

        double getWeightBetween(String a, String b) {
            // Encontrar el MENOR peso entre todos los caminos de a a b
            List<Edge> edges = adj.getOrDefault(a, Collections.emptyList());
            double minWeight = Double.POSITIVE_INFINITY;
            for (Edge e : edges) {
                if (e.v.equals(b)) {
                    minWeight = Math.min(minWeight, e.weight);
                }
            }
            return minWeight;
        }

        double pathCost(List<String> path) {
            if (path == null || path.size() < 2) return 0.0;
            double total = 0.0;
            for (int i = 0; i < path.size() - 1; i++) {
                double w = getWeightBetween(path.get(i), path.get(i+1));
                if (Double.isInfinite(w)) return Double.POSITIVE_INFINITY;
                total += w;
            }
            return total;
        }

        // TSP Greedy Nearest Neighbor: inicia en un nodo y siempre va al no visitado más cercano en todo el grafo
        List<String> tspNearestNeighbor(String start) {
            if (nodes.isEmpty()) return new ArrayList<>();
            String current = start;
            if (current == null || !nodes.contains(current)) {
                current = nodes.iterator().next();
            }
            Set<String> visited = new HashSet<>();
            List<String> path = new ArrayList<>();
            path.add(current);
            visited.add(current);
            
            while (visited.size() < nodes.size()) {
                // Buscar el nodo no visitado más cercano GLOBALMENTE en todo el grafo
                String nearest = null;
                double minCost = Double.POSITIVE_INFINITY;
                
                for (String candidate : nodes) {
                    if (!visited.contains(candidate)) {
                        double dist = getWeightBetween(current, candidate);
                        if (dist < minCost) {
                            minCost = dist;
                            nearest = candidate;
                        }
                    }
                }
                
                if (nearest == null) break;
                path.add(nearest);
                visited.add(nearest);
                current = nearest;
            }
            return path;
        }

        // Programación Dinámica: Floyd-Warshall (matriz de caminos más cortos entre todos los pares)
        Map<String, Object> floydWarshall() {
            List<String> nodeList = new ArrayList<>(nodes);
            int n = nodeList.size();
            double[][] dist = new double[n][n];
            
            // Inicializar: infinito para no vecinos, 0 para diagonal
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j) dist[i][j] = 0.0;
                    else dist[i][j] = Double.POSITIVE_INFINITY;
                }
            }
            
            // Llenar con aristas existentes
            for (int i = 0; i < n; i++) {
                String u = nodeList.get(i);
                for (int j = 0; j < n; j++) {
                    String v = nodeList.get(j);
                    double w = getWeightBetween(u, v);
                    if (!Double.isInfinite(w)) dist[i][j] = w;
                }
            }
            
            // Floyd-Warshall
            for (int k = 0; k < n; k++) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }
            
            // Convertir a mapa para respuesta
            Map<String, Object> resp = new HashMap<>();
            Map<String, Map<String, Double>> matrix = new HashMap<>();
            for (int i = 0; i < n; i++) {
                Map<String, Double> row = new HashMap<>();
                for (int j = 0; j < n; j++) {
                    row.put(nodeList.get(j), dist[i][j]);
                }
                matrix.put(nodeList.get(i), row);
            }
            resp.put("shortestPaths", matrix);
            return resp;
        }

        // Backtracking: encuentra todos los caminos posibles entre from y to (con límite de resultados)
        void findAllPathsBacktracking(String current, String target, List<String> path, Set<String> visited,
                                       List<List<String>> allPaths, int maxDepth, int maxPaths) {
            // Si ya alcanzamos el límite de caminos, detener
            if (allPaths.size() >= maxPaths) return;
            
            // Si la profundidad excede el máximo, detener
            if (path.size() > maxDepth) return;
            
            // Si alcanzamos el destino, guardar el camino
            if (current.equals(target)) {
                allPaths.add(new ArrayList<>(path));
                return;
            }
            
            // Explorar todos los vecinos
            List<Edge> neighbors = adj.getOrDefault(current, Collections.emptyList());
            for (Edge e : neighbors) {
                String next = e.v;
                // Solo continuar si no lo hemos visitado en este camino
                if (!visited.contains(next)) {
                    visited.add(next);
                    path.add(next);
                    
                    findAllPathsBacktracking(next, target, path, visited, allPaths, maxDepth, maxPaths);
                    
                    // Backtrack
                    path.remove(path.size() - 1);
                    visited.remove(next);
                }
            }
        }

        // Ramificación y Poda (Branch & Bound): TSP mejorado con cota inferior
        List<String> tspBranchAndBound() {
            if (nodes.isEmpty()) return new ArrayList<>();
            List<String> bestPath = new ArrayList<>();
            double[] bestCost = {Double.POSITIVE_INFINITY};
            String start = nodes.iterator().next();
            
            // Llamar al helper recursivo
            bbHelper(start, new ArrayList<>(List.of(start)), new HashSet<>(List.of(start)), 0.0, bestPath, bestCost, nodes.size());
            
            // Si no encontramos solución completa, intentar TSP greedy como fallback
            if (bestPath.isEmpty() || bestPath.size() == 1) {
                bestPath = tspNearestNeighbor(start);
            }
            return bestPath;
        }

        private void bbHelper(String current, List<String> path, Set<String> visited, double costSoFar,
                              List<String> bestPath, double[] bestCost, int totalNodes) {
            // Si visitamos todos los nodos, actualizar mejor solución
            if (visited.size() == totalNodes) {
                if (costSoFar < bestCost[0]) {
                    bestCost[0] = costSoFar;
                    bestPath.clear();
                    bestPath.addAll(path);
                }
                return;
            }
            
            // Poda: si el costo actual excede el mejor conocido, no continúes
            if (costSoFar >= bestCost[0]) return;
            
            // Primero intentar con vecinos directos
            List<Edge> neighbors = adj.getOrDefault(current, Collections.emptyList());
            boolean foundNeighbor = false;
            
            for (Edge e : neighbors) {
                if (!visited.contains(e.v)) {
                    foundNeighbor = true;
                    visited.add(e.v);
                    path.add(e.v);
                    bbHelper(e.v, path, visited, costSoFar + e.weight, bestPath, bestCost, totalNodes);
                    path.remove(path.size() - 1);
                    visited.remove(e.v);
                }
            }
            
            // Si no hay vecinos, hacer búsqueda global de nodos no visitados
            if (!foundNeighbor) {
                for (String candidate : nodes) {
                    if (!visited.contains(candidate)) {
                        double dist = getWeightBetween(current, candidate);
                        if (!Double.isInfinite(dist) && costSoFar + dist < bestCost[0]) {
                            visited.add(candidate);
                            path.add(candidate);
                            bbHelper(candidate, path, visited, costSoFar + dist, bestPath, bestCost, totalNodes);
                            path.remove(path.size() - 1);
                            visited.remove(candidate);
                        }
                    }
                }
            }
        }
    }

    private static class UnionFind {
        private final Map<String, String> parent = new HashMap<>();

        UnionFind(Set<String> nodes) {
            for (String n : nodes) parent.put(n, n);
        }

        String find(String x) {
            String p = parent.get(x);
            if (p == null) return null;
            if (!p.equals(x)) {
                String r = find(p);
                parent.put(x, r);
                return r;
            }
            return p;
        }

        boolean union(String a, String b) {
            String ra = find(a); String rb = find(b);
            if (ra == null || rb == null) return false;
            if (ra.equals(rb)) return false;
            parent.put(ra, rb);
            return true;
        }
    }

    private Graph buildGraphFromEdges(List<Map<String,Object>> edges) {
        Graph g = new Graph();
        for (Map<String,Object> e : edges) {
            Object of = e.get("from");
            Object ot = e.get("to");
            Object oc = e.get("cost");
            if (of == null || ot == null) continue;
            String a = of.toString();
            String b = ot.toString();
            double w = 1.0;
            if (oc != null) {
                try { w = Double.parseDouble(oc.toString()); } catch (NumberFormatException ex) { w = 1.0; }
            }
            g.addEdge(a,b,w);
        }
        return g;
    }

    // Divide y Vencerás: Mergesort para ordenar strings
    private void mergeSort(List<String> list, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, left, mid);
            mergeSort(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }

    private void merge(List<String> list, int left, int mid, int right) {
        List<String> temp = new ArrayList<>();
        int i = left, j = mid + 1;
        while (i <= mid && j <= right) {
            if (list.get(i).compareTo(list.get(j)) <= 0) {
                temp.add(list.get(i++));
            } else {
                temp.add(list.get(j++));
            }
        }
        while (i <= mid) temp.add(list.get(i++));
        while (j <= right) temp.add(list.get(j++));
        for (int k = 0; k < temp.size(); k++) {
            list.set(left + k, temp.get(k));
        }
    }
}