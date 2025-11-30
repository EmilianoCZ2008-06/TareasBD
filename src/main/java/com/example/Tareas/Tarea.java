package com.example.Tareas;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data   // lombok (getters, setters)
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    private boolean realizada;



    public Tarea(String titulo, String descripcion, boolean realizada) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.realizada = realizada;
    }
}
