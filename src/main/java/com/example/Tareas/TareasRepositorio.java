package com.example.Tareas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareasRepositorio extends JpaRepository<Tarea, Long> {
    /*
    esta interfaz permite guardar, buscar, actualizar y borrar tareas
    sin tener que escribir codigo SQL manualmente.
     */
}
