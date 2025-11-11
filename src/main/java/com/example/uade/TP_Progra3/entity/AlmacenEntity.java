package com.example.uade.TP_Progra3.entity;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Almacen")
public class AlmacenEntity {
    @Id
    private final String nombre;

    public AlmacenEntity(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}