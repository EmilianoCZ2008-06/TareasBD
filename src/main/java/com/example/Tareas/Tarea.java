package com.example.Tareas;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data   //lombok: genera automaticamente getters, setters, toString, etc.
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   //identificador único de cada tarea en la BD

    private String titulo;        //título que se muestra en la lista
    private String descripcion;   //explicación o contenido de la tarea
    private boolean fechaChk;     //true = fecha automática, false = fecha manual
    private LocalDate fecha;      //fecha asignada a la tarea
    private boolean realizada;    //estado: true = tarea completada

    public Tarea() {
        // Constructor vacio requerido por JPA
    }

    public Tarea(String titulo, String descripcion, boolean realizada, boolean fechaChk) {
        //constructor usado al crear una nueva tarea desde la app
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.realizada = realizada;
        this.fechaChk = fechaChk;
    }

    @PrePersist
    public void asignarFecha() {
        //si la tarea no tiene fecha asignada, se coloca la fecha actual
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }
    }
}
