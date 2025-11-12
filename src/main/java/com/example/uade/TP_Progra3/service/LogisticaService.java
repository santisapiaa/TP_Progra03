package com.example.uade.TP_Progra3.service;

import com.example.uade.TP_Progra3.graph.Edge;
import com.example.uade.TP_Progra3.graph.Graph;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LogisticaService {

    public Graph buildGraphFromEdges(List<Map<String,Object>> edges) {
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

    public Map<String,Object> dijkstraFromEdges(List<Map<String,Object>> edges, String from, String to) {
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
    }

    public Map<String,Object> kruskalFromEdges(List<Map<String,Object>> edges) {
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
    }

    public Map<String,Object> primFromEdges(List<Map<String,Object>> edges, String start) {
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
    }

    public Map<String,Object> greedyFromEdges(List<Map<String,Object>> edges, String start) {
        Graph g = buildGraphFromEdges(edges);
        List<String> path = g.tspNearestNeighbor(start);
        Map<String,Object> resp = new HashMap<>();
        double total = g.pathCost(path);
        boolean allVisited = (path.size() == g.getNodes().size());
        resp.put("path", path);
        resp.put("totalCost", total);
        resp.put("allNodesVisited", allVisited);
        return resp;
    }

    public Map<String,Object> floydFromEdges(List<Map<String,Object>> edges) {
        Graph g = buildGraphFromEdges(edges);
        return g.floydWarshall();
    }

    public Map<String,Object> bfsFromEdges(List<Map<String,Object>> edges, String from, String to) {
        Graph g = buildGraphFromEdges(edges);
        List<String> path = g.bfsPath(from, to);
        Map<String,Object> resp = new HashMap<>();
        if (path == null) {
            resp.put("from", from);
            resp.put("to", to);
            resp.put("path", new ArrayList<>());
            resp.put("totalCost", Double.POSITIVE_INFINITY);
            resp.put("found", false);
            return resp;
        }
        double total = g.pathCost(path);
        resp.put("from", from);
        resp.put("to", to);
        resp.put("path", path);
        resp.put("totalCost", total);
        resp.put("found", true);
        return resp;
    }

    public Map<String,Object> dfsFromEdges(List<Map<String,Object>> edges, String from, String to) {
        Graph g = buildGraphFromEdges(edges);
        List<String> path = g.dfsPath(from, to);
        Map<String,Object> resp = new HashMap<>();
        if (path == null) {
            resp.put("from", from);
            resp.put("to", to);
            resp.put("path", new ArrayList<>());
            resp.put("totalCost", Double.POSITIVE_INFINITY);
            resp.put("found", false);
            return resp;
        }
        double total = g.pathCost(path);
        resp.put("from", from);
        resp.put("to", to);
        resp.put("path", path);
        resp.put("totalCost", total);
        resp.put("found", true);
        return resp;
    }

    public List<Map<String, Object>> sortNodesByDegreeFromEdges(List<Map<String, Object>> edges) {
        Graph g = buildGraphFromEdges(edges);
        return g.sortNodesByDegree();
    }
}
