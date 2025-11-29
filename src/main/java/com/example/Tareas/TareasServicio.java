package com.example.Tareas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TareasServicio {

    @Autowired
    private TareasRepositorio tareasRepositorio;

    // -------------------------------------------------------
    // 1. obtener todas las tareas
    // -------------------------------------------------------
    public List<Tarea> findAll() {
        return tareasRepositorio.findAll();
    }

    // -------------------------------------------------------
    // 2. guardar una nueva tarea
    // -------------------------------------------------------
    public void save(Tarea tarea) {
        tareasRepositorio.save(tarea);
    }

    // -------------------------------------------------------
    // 3. buscar tarea por ID
    // -------------------------------------------------------
    public Tarea findById(Long idTarea) {
        return tareasRepositorio.findById(idTarea).orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
    }

    // -------------------------------------------------------
    // 4. eliminar tarea por ID
    // -------------------------------------------------------
    public void delete(Long idTarea) {
        tareasRepositorio.deleteById(idTarea);
    }

    // -------------------------------------------------------
    // 5. actualizar una tarea existente
    // -------------------------------------------------------
    public void update(Long idTarea, Tarea tareaActualizada) {
        // buscar en BD
        Optional<Tarea> tareaExistente = tareasRepositorio.findById(idTarea);
        if (tareaExistente.isPresent()) {

            // actualizar campos en la entidad con sus getters y setters definidos por Lombok
            tareaExistente.get().setTitulo(tareaActualizada.getTitulo());
            tareaExistente.get().setDescripcion(tareaActualizada.getDescripcion());
            tareaExistente.get().setRealizada(tareaActualizada.isRealizada());
            tareasRepositorio.save(tareaExistente.get());
        }
    }
}
