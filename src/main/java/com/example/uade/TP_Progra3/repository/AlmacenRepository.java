package com.example.uade.TP_Progra3.repository;

import com.example.uade.TP_Progra3.entity.AlmacenEntity;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.Map;

@Repository
public interface AlmacenRepository extends ReactiveNeo4jRepository<AlmacenEntity, String> {

    // Algoritmo BFS (Búsqueda en Anchura)
    // Ahora devolvemos una sola fila con la colección de caminos: paths = [[nodeProps,...], [nodeProps,...], ...]
    @Query("MATCH (start:Almacen {nombre: $nombre}) " +
           "CALL apoc.path.expandConfig(start, { " +
           "\trelationshipFilter: 'RUTA', " +
           "\tminLevel: 1, " +
           "\tmaxLevel: $maxDepth, " +
           "\tuniqueness: 'NODE_GLOBAL', " +
           "\tbfs: true " +
           "}) YIELD path " +
           "WITH [node IN nodes(path) | properties(node)] AS pathNodes " +
           "RETURN {paths: collect(pathNodes)} AS result")
    Mono<Map<String, Object>> findBfsPath(@Param("nombre") String nombre, @Param("maxDepth") int maxDepth);

    // Algoritmo DFS (Búsqueda en Profundidad)
    // Misma idea que BFS pero con dfs: false
    @Query("MATCH (start:Almacen {nombre: $nombre}) " +
           "CALL apoc.path.expandConfig(start, { " +
           "\trelationshipFilter: 'RUTA', " +
           "\tminLevel: 1, " +
           "\tmaxLevel: $maxDepth, " +
           "\tuniqueness: 'NODE_GLOBAL', " +
           "\tbfs: false " + // La única diferencia con BFS
           "}) YIELD path " +
           "WITH [node IN nodes(path) | properties(node)] AS pathNodes " +
           "RETURN {paths: collect(pathNodes)} AS result")
    Mono<Map<String, Object>> findDfsPath(@Param("nombre") String nombre, @Param("maxDepth") int maxDepth);
}