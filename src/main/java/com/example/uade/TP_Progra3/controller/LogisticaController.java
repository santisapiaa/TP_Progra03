package com.example.uade.TP_Progra3.controller;

import com.example.uade.TP_Progra3.entity.AlmacenEntity;
import com.example.uade.TP_Progra3.repository.AlmacenRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.List;

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
}