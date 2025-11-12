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

       @Query("MATCH (a:Almacen)-[r:RUTA]-(b:Almacen) RETURN {from: a.nombre, to: b.nombre, cost: r.cost} AS edge")
       reactor.core.publisher.Flux<Map<String, Object>> findAllEdges();
}