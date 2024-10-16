package controller;

import model.Tarea;
import util.FileManager;
import exceptions.FilePersistenceException;
import exceptions.InvalidDataException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class TareaController {
    
    private List<Tarea> tareas;
    private List<Tarea> tareasCompletadas; // Lista para tareas completadas
    private FileManager fileManager;

    public TareaController() {
        fileManager = new FileManager("src/model/persistence/tareas.json");
        try {
            tareas = fileManager.cargarTareas();
            tareasCompletadas = new ArrayList<>(); // Inicializamos la lista de tareas completadas
        } catch (FilePersistenceException e) {
            System.out.println("Error al cargar las tareas: " + e.getMessage());
            tareas = new ArrayList<>();
            tareasCompletadas = new ArrayList<>();
        }
    }

    public void agregarTarea(Tarea tarea) {
        try {
            tareas.add(tarea);
            fileManager.guardarTareas(tareas);
        } catch (FilePersistenceException e) {
            System.out.println("Error al guardar la tarea: " + e.getMessage());
        }
    }

    public void editarTarea(Tarea tarea, String nuevoTitulo, String nuevaDescripcion, LocalDate nuevaFechaLimite, String nuevaPrioridad) {
        try {
            if (nuevoTitulo != null && !nuevoTitulo.isEmpty()) {
                tarea.setTitulo(nuevoTitulo);
            }
            if (nuevaDescripcion != null) {
                tarea.setDescripcion(nuevaDescripcion);
            }
            if (nuevaFechaLimite != null) {
                tarea.setFechaLimite(nuevaFechaLimite);
            }
            if (nuevaPrioridad != null && (nuevaPrioridad.equalsIgnoreCase("Alta") || 
                                            nuevaPrioridad.equalsIgnoreCase("Media") || 
                                            nuevaPrioridad.equalsIgnoreCase("Baja"))) {
                tarea.setPrioridad(nuevaPrioridad);
            } else {
                throw new InvalidDataException("La prioridad debe ser 'Alta', 'Media' o 'Baja'.");
            }
            
            // Guardar los cambios en el archivo
            fileManager.guardarTareas(tareas);
        } catch (FilePersistenceException | InvalidDataException e) {
            System.out.println("Error al editar la tarea: " + e.getMessage());
        }
    }

    public void eliminarTarea(Tarea tarea) {
        try {
            tareas.remove(tarea);
            tareasCompletadas.remove(tarea); // Aseguramos que se elimine de la lista completada también
            fileManager.guardarTareas(tareas);
        } catch (FilePersistenceException e) {
            System.out.println("Error al eliminar la tarea: " + e.getMessage());
        }
    }

    public void marcarComoCompletada(Tarea tarea) {
        tarea.setCompletada(true); // Marcar la tarea como completada
        tareasCompletadas.add(tarea); // Agregar a la lista de tareas completadas
        tareas.remove(tarea); // Eliminar de la lista de tareas pendientes
        try {
            fileManager.guardarTareas(tareas); // Guardar tareas pendientes
            fileManager.guardarTareas(tareasCompletadas); // Guardar tareas completadas
        } catch (FilePersistenceException e) {
            System.out.println("Error al guardar las tareas: " + e.getMessage());
        }
    }

    public List<Tarea> getTareas() {
        return tareas;
    }

    public List<Tarea> getTareasCompletadas() { // Método para obtener tareas completadas
        return tareasCompletadas;
    }

    public List<Tarea> filtrarPorEstado(boolean completada) {
        return tareas.stream().filter(t -> t.isCompletada() == completada).toList();
    }

    public List<Tarea> filtrarPorPrioridad(String prioridad) {
        return tareas.stream().filter(t -> t.getPrioridad().equalsIgnoreCase(prioridad)).toList();
    }

    public List<Tarea> filtrarPorFechaLimite(LocalDate fechaLimite) {
        return tareas.stream().filter(t -> t.getFechaLimite().isEqual(fechaLimite)).toList();
    }
}
