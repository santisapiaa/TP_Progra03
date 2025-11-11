package com.example.uade.TP_Progra3.repository;

import com.example.uade.TP_Progra3.entity.PersonEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

public interface PersonRepository extends ReactiveNeo4jRepository<PersonEntity, String> {
    @Query("MATCH (p:Person) WHERE p.name = $name RETURN p")
    Flux<PersonEntity> findByName(@Param("name") String name);
}