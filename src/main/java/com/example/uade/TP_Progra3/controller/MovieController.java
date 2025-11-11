package com.example.uade.TP_Progra3.controller;

import com.example.uade.TP_Progra3.entity.MovieEntity;
import com.example.uade.TP_Progra3.repository.MovieRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Endpoint base para crear/actualizar película
    @PutMapping
    Mono<MovieEntity> createOrUpdateMovie(@RequestBody MovieEntity newMovie) {
        return movieRepository.save(newMovie);
    }

    // Endpoint base para obtener todas las películas (stream)
    @GetMapping(value = { "/" }, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<MovieEntity> getMovies() {
        return movieRepository.findAll();
    }

    // Nuevo endpoint para el algoritmo BFS actualizado
    @GetMapping(value = "/bfs/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    Flux<Map<String, Object>> bfs(@PathVariable String name, @RequestParam(defaultValue = "2") int depth) {
        return movieRepository.findBfsPath(name, depth);
    }
}