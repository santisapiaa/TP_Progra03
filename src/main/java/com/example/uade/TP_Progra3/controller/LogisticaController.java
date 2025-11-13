package com.example.uade.TP_Progra3.controller;

import com.example.uade.TP_Progra3.entity.AlmacenEntity;
import com.example.uade.TP_Progra3.repository.AlmacenRepository;
import com.example.uade.TP_Progra3.service.LogisticaService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.*;

@RestController
@RequestMapping("/logistica")
public class LogisticaController {

    private final AlmacenRepository almacenRepository;
    private final LogisticaService logisticaService;

    public LogisticaController(AlmacenRepository almacenRepository, LogisticaService logisticaService) {
        this.almacenRepository = almacenRepository;
        this.logisticaService = logisticaService;
    }

    /**
     * GET /logistica/almacenes
     * Devuelve todos los almacenes registrados en la BD.
     */
    @GetMapping(value = "/almacenes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<AlmacenEntity> getAllAlmacenes() {
        return almacenRepository.findAll();
    }

    /**
     * GET /logistica/bfs/{from}/{to}
     * Búsqueda en Amplitud (BFS): encuentra el primer camino entre dos almacenes explorando por niveles.
     * NOTA: BFS ignora los pesos de las aristas (NO es óptimo en grafos ponderados).
     *       Para grafo ponderado y camino más barato, usa /dijkstra.
     */
    @GetMapping(value = "/bfs/{from}/{to}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> bfs(@PathVariable String from, @PathVariable String to) {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> logisticaService.bfsFromEdges(edges, from, to))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    /**
     * GET /logistica/dfs/{from}/{to}
     * Búsqueda en Profundidad (DFS): encuentra el primer camino entre dos almacenes explorando en profundidad.
     * NOTA: DFS ignora los pesos de las aristas (NO es óptimo en grafos ponderados).
     *       Para grafo ponderado y camino más barato, usa /dijkstra.
     */
    @GetMapping(value = "/dfs/{from}/{to}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> dfs(@PathVariable String from, @PathVariable String to) {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> logisticaService.dfsFromEdges(edges, from, to))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    /**
     * GET /logistica/dijkstra/{from}/{to}
     * Algoritmo de Dijkstra: encuentra el camino CON MENOR COSTO entre dos almacenes (óptimo para grafos ponderados).
     * Útil para logística: da la ruta más barata.
     */
    @GetMapping(value = "/dijkstra/{from}/{to}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> dijkstra(@PathVariable String from, @PathVariable String to) {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> logisticaService.dijkstraFromEdges(edges, from, to))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    /**
     * GET /logistica/kruskal
     * Algoritmo de Kruskal: construye un Árbol Generador Mínimo (MST) usando enfoque greedy.
     * Utilidad: encontrar las conexiones de MENOR COSTO que conectan TODOS los almacenes sin ciclos.
     */
    @GetMapping(value = "/kruskal", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> kruskal() {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> logisticaService.kruskalFromEdges(edges))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    /**
     * GET /logistica/prim?start=NombreAlmacen
     * Algoritmo de Prim: construye un Árbol Generador Mínimo (MST) comenzando desde un nodo inicial.
     * Similar a Kruskal, pero construye el árbol incrementalmente desde un nodo específico.
     */
    @GetMapping(value = "/prim", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> prim(@RequestParam(required = false) String start) {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> logisticaService.primFromEdges(edges, start))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    /**
     * GET /logistica/edges
     * Devuelve TODAS las aristas (conexiones) del grafo tal como están en la BD.
     * Útil para depuración y entender la estructura de la red logística.
     */
    @GetMapping(value = "/aristas", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Map<String, Object>>> edges() {
        return almacenRepository.findAllEdges().collectList();
    }

    /**
     * GET /logistica/greedy?start=NombreAlmacen
     * Algoritmo TSP Greedy (Nearest Neighbor): resuelve Traveling Salesman Problem de forma heurística.
     * Estrategia: desde el nodo actual, siempre ir al almacén más cercano no visitado.
     * NO garantiza la solución óptima, pero es rápido.
     */
    @GetMapping(value = "/greedy", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> tsp(@RequestParam(required = false) String start) {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> logisticaService.greedyFromEdges(edges, start))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    /**
     * GET /logistica/dynamic-programming
     * Algoritmo Floyd-Warshall (Programación Dinámica): calcula caminos MÍNIMOS entre TODOS los pares de nodos.
     * Construye una matriz N×N donde [i][j] = costo mínimo de i a j.
     * Útil para: análisis global de la red, saber de un vistazo el costo entre cualquier par.
     */
    @GetMapping(value = "/programacion-dinamica", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> floydWarshall() {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() -> logisticaService.floydFromEdges(edges))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    /**
     * GET /logistica/divide-conquer
     * Algoritmo Divide & Conquer (Mergesort): ordena almacenes por grado (cantidad de conexiones).
     * Identifica los hubs (almacenes más conectados) que son críticos en la red logística.
     * Retorna lista de almacenes con cantidad de rutas, ordenados de mayor a menor conectividad.
     */
    @GetMapping(value = "/divide-venceras", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Map<String, Object>>> divideConquerSort() {
        return almacenRepository.findAllEdges()
                .collectList()
                .flatMap(edges -> Mono.fromCallable(() ->
                    logisticaService.sortNodesByDegreeFromEdges(edges)
                ).subscribeOn(Schedulers.boundedElastic()));
    }
    
}