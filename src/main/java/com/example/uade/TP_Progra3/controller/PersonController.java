package com.example.uade.TP_Progra3.controller;

import com.example.uade.TP_Progra3.entity.PersonEntity;
import com.example.uade.TP_Progra3.repository.PersonRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PersonEntity> getAllPersons() {
        return personRepository.findAll();
    }

    @GetMapping("/{name}")
    public Mono<PersonEntity> getPersonByName(@PathVariable String name) {
        return personRepository.findByName(name).next();
    }

    @PutMapping
    public Mono<PersonEntity> createOrUpdatePerson(@RequestBody PersonEntity person) {
        return personRepository.save(person);
    }
}