package com.example.uade.TP_Progra3.repository;

import com.example.uade.TP_Progra3.entity.MovieEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

@Repository
public interface MovieRepository extends ReactiveNeo4jRepository<MovieEntity, String> {

    Mono<MovieEntity> findOneByTitle(String title);

    @Query("MATCH (start:Person {name: $name}) " +
           "CALL apoc.path.expandConfig(start, { " +
           "	relationshipFilter: 'ACTED_IN|DIRECTED', " +
           "	minLevel: 1, " +
           "	maxLevel: $maxDepth, " +
           "	uniqueness: 'NODE_GLOBAL', " +
           "	bfs: true " +
           "}) YIELD path " +
           "RETURN [node IN nodes(path) | properties(node)] AS bfsPath")
    Flux<Map<String, Object>> findBfsPath(@Param("name") String name, @Param("maxDepth") int maxDepth);
}