package com.example.uade.TP_Progra3.graph;

import java.util.*;

public class Graph {
    private final Map<String, List<Edge>> adj = new HashMap<>();
    private final Set<String> nodes = new HashSet<>();

    public Set<String> getNodes() { return nodes; }
    public Map<String, List<Edge>> getAdj() { return adj; }

    public void addEdge(String a, String b, double w) {
        nodes.add(a); nodes.add(b);
        adj.computeIfAbsent(a, k -> new ArrayList<>()).add(new Edge(a,b,w));
        adj.computeIfAbsent(b, k -> new ArrayList<>()).add(new Edge(b,a,w));
    }

    public List<String> dijkstra(String src, String dst) {
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

    public List<Edge> kruskalMST() {
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

    public List<Edge> primMST(String start) {
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

    public double getWeightBetween(String a, String b) {
        List<Edge> edges = adj.getOrDefault(a, Collections.emptyList());
        double minWeight = Double.POSITIVE_INFINITY;
        for (Edge e : edges) {
            if (e.v.equals(b)) {
                minWeight = Math.min(minWeight, e.weight);
            }
        }
        return minWeight;
    }

    public double pathCost(List<String> path) {
        if (path == null || path.size() < 2) return 0.0;
        double total = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            double w = getWeightBetween(path.get(i), path.get(i+1));
            if (Double.isInfinite(w)) return Double.POSITIVE_INFINITY;
            total += w;
        }
        return total;
    }

    public List<String> tspNearestNeighbor(String start) {
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

    public Map<String, Object> floydWarshall() {
        List<String> nodeList = new ArrayList<>(nodes);
        int n = nodeList.size();
        double[][] dist = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) dist[i][j] = 0.0;
                else dist[i][j] = Double.POSITIVE_INFINITY;
            }
        }
        
        for (int i = 0; i < n; i++) {
            String u = nodeList.get(i);
            for (int j = 0; j < n; j++) {
                String v = nodeList.get(j);
                double w = getWeightBetween(u, v);
                if (!Double.isInfinite(w)) dist[i][j] = w;
            }
        }
        
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        
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

    public List<String> bfsPath(String start, String target) {
        if (!nodes.contains(start) || !nodes.contains(target)) return null;
        if (start.equals(target)) return new ArrayList<>(List.of(start));
        
        Queue<List<String>> queue = new LinkedList<>();
        queue.add(new ArrayList<>(List.of(start)));
        Set<String> visited = new HashSet<>();
        visited.add(start);
        
        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String last = path.get(path.size() - 1);
            
            for (Edge e : adj.getOrDefault(last, Collections.emptyList())) {
                String nxt = e.v;
                if (!visited.contains(nxt)) {
                    visited.add(nxt);
                    List<String> nextPath = new ArrayList<>(path);
                    nextPath.add(nxt);
                    
                    if (nxt.equals(target)) {
                        return nextPath;
                    }
                    queue.add(nextPath);
                }
            }
        }
        return null; // No path found
    }

    public List<String> dfsPath(String start, String target) {
        if (!nodes.contains(start) || !nodes.contains(target)) return null;
        if (start.equals(target)) return new ArrayList<>(List.of(start));
        
        Set<String> visited = new HashSet<>();
        List<String> path = new ArrayList<>();
        if (dfsHelper(start, target, visited, path)) {
            return path;
        }
        return null; // No path found
    }

    private boolean dfsHelper(String current, String target, Set<String> visited, List<String> path) {
        visited.add(current);
        path.add(current);
        
        if (current.equals(target)) {
            return true;
        }
        
        for (Edge e : adj.getOrDefault(current, Collections.emptyList())) {
            String nxt = e.v;
            if (!visited.contains(nxt)) {
                if (dfsHelper(nxt, target, visited, path)) {
                    return true;
                }
            }
        }
        
        path.remove(path.size() - 1);
        return false;
    }

    public static void mergeSort(List<String> list, int left, int right) {
        if (list == null || list.isEmpty()) return;
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, left, mid);
            mergeSort(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }

    public List<Map<String, Object>> sortNodesByDegree() {
        // Build list of [nodeName, degree]
        List<Map<String, Object>> nodesList = new ArrayList<>();
        for (String node : nodes) {
            int degree = adj.getOrDefault(node, Collections.emptyList()).size();
            Map<String, Object> entry = new HashMap<>();
            entry.put("nombre", node);
            entry.put("rutas", degree);
            nodesList.add(entry);
        }
        
        // Sort by degree (descending) using mergesort
        mergesortByDegree(nodesList, 0, nodesList.size() - 1);
        return nodesList;
    }

    private static void mergesortByDegree(List<Map<String, Object>> list, int left, int right) {
        if (list == null || list.isEmpty()) return;
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergesortByDegree(list, left, mid);
            mergesortByDegree(list, mid + 1, right);
            mergeByDegree(list, left, mid, right);
        }
    }

    private static void mergeByDegree(List<Map<String, Object>> list, int left, int mid, int right) {
        List<Map<String, Object>> temp = new ArrayList<>();
        int i = left, j = mid + 1;
        while (i <= mid && j <= right) {
            int degreeI = (int) list.get(i).get("rutas");
            int degreeJ = (int) list.get(j).get("rutas");
            if (degreeI >= degreeJ) {  // Descending order
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

    private static void merge(List<String> list, int left, int mid, int right) {
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
