package com.example.uade.TP_Progra3.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UnionFind {
    private final Map<String, String> parent = new HashMap<>();

    public UnionFind(Set<String> nodes) {
        for (String n : nodes) parent.put(n, n);
    }

    public String find(String x) {
        String p = parent.get(x);
        if (p == null) return null;
        if (!p.equals(x)) {
            String r = find(p);
            parent.put(x, r);
            return r;
        }
        return p;
    }

    public boolean union(String a, String b) {
        String ra = find(a); String rb = find(b);
        if (ra == null || rb == null) return false;
        if (ra.equals(rb)) return false;
        parent.put(ra, rb);
        return true;
    }
}
