package model;

import java.time.LocalDate;
import exceptions.InvalidDataException;

public class Tarea {
    private String titulo;
    private String descripcion;
    private LocalDate fechaLimite;
    private String prioridad;
    private boolean completada;

    public Tarea(String titulo, String descripcion, LocalDate fechaLimite, String prioridad) throws InvalidDataException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new InvalidDataException("El título no puede estar vacío o solo contener espacios en blanco.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new InvalidDataException("La descripción no puede estar vacía o solo contener espacios en blanco.");
        }
        if (fechaLimite == null) {
            throw new InvalidDataException("La fecha límite no puede ser nula.");
        }
        if (prioridad == null || !(prioridad.equalsIgnoreCase("Alta") || prioridad.equalsIgnoreCase("Media") || prioridad.equalsIgnoreCase("Baja"))) {
            throw new InvalidDataException("La prioridad debe ser 'Alta', 'Media' o 'Baja'.");
        }

        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.prioridad = prioridad;
        this.completada = false;
    }

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    @Override
    public String toString() {
        return String.format("%s | %s | Fecha límite: %s | Prioridad: %s | Completada: %s", 
                titulo, descripcion, fechaLimite, prioridad, completada ? "Sí" : "No");
    }
}
