package view;

import controller.TareaController;
import exceptions.InvalidDataException;
import model.Tarea;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TareaView extends JFrame {
    private TareaController controller;
    private DefaultListModel<String> modeloLista;
    private JList<String> listaTareas;

    public TareaView(TareaController controller) {
        this.controller = controller;
        setTitle("Gestión de Tareas");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloLista = new DefaultListModel<>();
        listaTareas = new JList<>(modeloLista);
        actualizarLista();

        JButton agregarButton = new JButton("Agregar Tarea");
        agregarButton.addActionListener(e -> agregarTarea());

        JButton marcarCompletadaButton = new JButton("Cambiar estado");
        marcarCompletadaButton.addActionListener(e -> marcarComoCompletada());

        JButton editarButton = new JButton("Editar tarea");
        editarButton.addActionListener(e -> editarTarea());

        JButton eliminarButton = new JButton("Eliminar Tarea");
        eliminarButton.addActionListener(e -> eliminarTarea());

        JPanel filtroPanel = new JPanel();
        JButton filtrarPorEstadoButton = new JButton("Filtrar por Estado");
        filtrarPorEstadoButton.addActionListener(e -> filtrarPorEstado());
        JButton filtrarPorPrioridadButton = new JButton("Filtrar por Prioridad");
        filtrarPorPrioridadButton.addActionListener(e -> filtrarPorPrioridad());
        JButton filtrarPorFechaButton = new JButton("Filtrar por Fecha Límite");
        filtrarPorFechaButton.addActionListener(e -> filtrarPorFecha());

        JButton limpiarFiltroButton = new JButton("Limpiar Filtro");
        limpiarFiltroButton.addActionListener(e -> actualizarLista());

        filtroPanel.add(filtrarPorEstadoButton);
        filtroPanel.add(filtrarPorPrioridadButton);
        filtroPanel.add(filtrarPorFechaButton);
        filtroPanel.add(limpiarFiltroButton);

        JPanel botonesPanel = new JPanel();
        botonesPanel.add(agregarButton);
        botonesPanel.add(marcarCompletadaButton);
        botonesPanel.add(editarButton);
        botonesPanel.add(eliminarButton);

        add(new JScrollPane(listaTareas), BorderLayout.CENTER);
        add(botonesPanel, BorderLayout.SOUTH);
        add(filtroPanel, BorderLayout.NORTH);
    }

    private void agregarTarea() {
        String titulo = null;
        String descripcion = null;
        LocalDate fechaLimite = null;
        String prioridad = null;

        boolean datosCompletos = false;

        // Bucle hasta que se hayan ingresado correctamente todos los datos
        while (!datosCompletos) {
            try {
                // Solicitar y validar el título
                if (titulo == null) {
                    titulo = JOptionPane.showInputDialog(this, "Título:");
                    if (titulo == null || titulo.trim().isEmpty()) {
                        throw new InvalidDataException("El título no puede estar vacío o solo contener espacios en blanco.");
                    }
                }

                // Solicitar y validar la descripción
                if (descripcion == null) {
                    descripcion = JOptionPane.showInputDialog(this, "Descripción:");
                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        throw new InvalidDataException("La descripción no puede estar vacía o solo contener espacios en blanco.");
                    }
                }

                // Solicitar y validar la fecha límite
                if (fechaLimite == null) {
                    String fechaLimiteStr = JOptionPane.showInputDialog(this, "Fecha límite (YYYY-MM-DD):");
                    if (fechaLimiteStr == null || fechaLimiteStr.trim().isEmpty()) {
                        throw new InvalidDataException("La fecha límite no puede estar vacía.");
                    }
                    try {
                        fechaLimite = LocalDate.parse(fechaLimiteStr);
                    } catch (Exception ex) {
                        throw new InvalidDataException("La fecha límite no tiene el formato correcto (YYYY-MM-DD).");
                    }
                }

                // Solicitar y validar la prioridad
                if (prioridad == null) {
                    prioridad = JOptionPane.showInputDialog(this, "Prioridad (Alta, Media, Baja):");
                    if (prioridad == null || !(prioridad.equalsIgnoreCase("Alta") ||
                            prioridad.equalsIgnoreCase("Media") ||
                            prioridad.equalsIgnoreCase("Baja"))) {
                        throw new InvalidDataException("La prioridad debe ser 'Alta', 'Media' o 'Baja'.");
                    }
                }

                // Si todos los datos son válidos, se crea la tarea
                Tarea nuevaTarea = new Tarea(titulo, descripcion, fechaLimite, prioridad);
                controller.agregarTarea(nuevaTarea);
                actualizarLista();
                datosCompletos = true;  // Salir del bucle si todos los datos son válidos

            } catch (InvalidDataException e) {
                // Mostrar el mensaje de error y permitir que el usuario vuelva a ingresar solo el dato incorrecto
                JOptionPane.showMessageDialog(this, e.getMessage(), "Datos inválidos", JOptionPane.ERROR_MESSAGE);
                // Reiniciar la variable correspondiente al campo que falló
                if (e.getMessage().contains("título")) {
                    titulo = null; // Reiniciar el título si hubo error
                } else if (e.getMessage().contains("descripción")) {
                    descripcion = null; // Reiniciar la descripción si hubo error
                } else if (e.getMessage().contains("fecha límite")) {
                    fechaLimite = null; // Reiniciar la fecha si hubo error
                } else if (e.getMessage().contains("prioridad")) {
                    prioridad = null; // Reiniciar la prioridad si hubo error
                }
            } catch (Exception e) {
                e.printStackTrace();  // Mostrar el stack trace completo para depurar
                JOptionPane.showMessageDialog(this, "Error al agregar tarea: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void marcarComoCompletada() {
        int selectedIndex = listaTareas.getSelectedIndex();
        if (selectedIndex != -1) {
            Tarea tareaSeleccionada = controller.getTareas().get(selectedIndex);
            controller.marcarComoCompletada(tareaSeleccionada);
            actualizarLista();
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea primero", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarTarea() {
        int selectedIndex = listaTareas.getSelectedIndex();
        if (selectedIndex != -1) {
            Tarea tareaSeleccionada = controller.getTareas().get(selectedIndex);
            controller.eliminarTarea(tareaSeleccionada);
            actualizarLista();
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea primero", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarLista() {
        modeloLista.clear();
        List<Tarea> tareas = controller.getTareas();
        tareas.forEach(t -> modeloLista.addElement(t.toString()));
    }

    public static void main(String[] args) {
        TareaController controller = new TareaController();
        new TareaView(controller).setVisible(true);
    }

    private void editarTarea() {
        int selectedIndex = listaTareas.getSelectedIndex();
        if (selectedIndex != -1) {
            Tarea tareaSeleccionada = controller.getTareas().get(selectedIndex);

            String nuevoTitulo = tareaSeleccionada.getTitulo();
            String nuevaDescripcion = tareaSeleccionada.getDescripcion();
            LocalDate nuevaFechaLimite = tareaSeleccionada.getFechaLimite();
            String nuevaPrioridad = tareaSeleccionada.getPrioridad();

            boolean datosCompletos = false;

            // Bucle hasta que se hayan ingresado correctamente todos los datos
            while (!datosCompletos) {
                try {
                    // Solicitar y validar el nuevo título
                    String inputTitulo = JOptionPane.showInputDialog(this, "Nuevo Título :", nuevoTitulo);
                    if (inputTitulo != null && inputTitulo.trim().isEmpty()) {
                        throw new InvalidDataException("El título no puede estar vacío o solo contener espacios en blanco.");
                    } else if (inputTitulo != null) {
                        nuevoTitulo = inputTitulo; // Solo se cambia si es válido
                    }

                    // Solicitar y validar la nueva descripción
                    String inputDescripcion = JOptionPane.showInputDialog(this, "Nueva Descripción :", nuevaDescripcion);
                    if (inputDescripcion != null && inputDescripcion.trim().isEmpty()) {
                        throw new InvalidDataException("La descripción no puede estar vacía o solo contener espacios en blanco.");
                    } else if (inputDescripcion != null) {
                        nuevaDescripcion = inputDescripcion; // Solo se cambia si es válido
                    }

                    // Solicitar y validar la nueva fecha límite
                    String nuevaFechaLimiteStr = JOptionPane.showInputDialog(this, "Nueva Fecha límite (YYYY-MM-DD):", nuevaFechaLimite != null ? nuevaFechaLimite.toString() : "");
                    if (nuevaFechaLimiteStr != null && !nuevaFechaLimiteStr.trim().isEmpty()) {
                        try {
                            nuevaFechaLimite = LocalDate.parse(nuevaFechaLimiteStr);
                        } catch (Exception ex) {
                            throw new InvalidDataException("La fecha límite no tiene el formato correcto (YYYY-MM-DD).");
                        }
                    }

                    // Solicitar y validar la nueva prioridad
                    String inputPrioridad = JOptionPane.showInputDialog(this, "Nueva Prioridad (Alta, Media, Baja):", nuevaPrioridad);
                    if (inputPrioridad != null && !(inputPrioridad.equalsIgnoreCase("Alta") ||
                            inputPrioridad.equalsIgnoreCase("Media") ||
                            inputPrioridad.equalsIgnoreCase("Baja"))) {
                        throw new InvalidDataException("La prioridad debe ser 'Alta', 'Media' o 'Baja'.");
                    } else if (inputPrioridad != null) {
                        nuevaPrioridad = inputPrioridad; // Solo se cambia si es válido
                    }

                    // Si todos los datos son válidos, se actualiza la tarea
                    controller.editarTarea(tareaSeleccionada, nuevoTitulo, nuevaDescripcion, nuevaFechaLimite, nuevaPrioridad);
                    actualizarLista();
                    datosCompletos = true; // Salir del bucle si todos los datos son válidos

                } catch (InvalidDataException e) {
                    // Mostrar el mensaje de error y permitir que el usuario vuelva a ingresar solo el dato incorrecto
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Datos inválidos", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();  // Mostrar el stack trace completo para depurar
                    JOptionPane.showMessageDialog(this, "Error al editar tarea: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea primero", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarPorEstado() {
        String estado = JOptionPane.showInputDialog(this, "Filtrar por Estado (completada / pendiente):");
        if (estado != null && !estado.trim().isEmpty()) {
            boolean completada = estado.equalsIgnoreCase("completada");
            List<Tarea> tareasFiltradas = controller.filtrarPorEstado(completada);
            modeloLista.clear();
            tareasFiltradas.forEach(t -> modeloLista.addElement(t.toString()));
        }
    }

    private void filtrarPorPrioridad() {
        String prioridad = JOptionPane.showInputDialog(this, "Filtrar por Prioridad (Alta, Media, Baja):");
        if (prioridad != null && (prioridad.equalsIgnoreCase("Alta") || prioridad.equalsIgnoreCase("Media") || prioridad.equalsIgnoreCase("Baja"))) {
            List<Tarea> tareasFiltradas = controller.filtrarPorPrioridad(prioridad);
            modeloLista.clear();
            tareasFiltradas.forEach(t -> modeloLista.addElement(t.toString()));
        } else {
            JOptionPane.showMessageDialog(this, "Prioridad inválida. Debe ser Alta, Media o Baja.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarPorFecha() {
        String fechaStr = JOptionPane.showInputDialog(this, "Filtrar por Fecha límite (YYYY-MM-DD):");
        if (fechaStr != null && !fechaStr.trim().isEmpty()) {
            try {
                LocalDate fechaLimite = LocalDate.parse(fechaStr);
                List<Tarea> tareasFiltradas = controller.filtrarPorFechaLimite(fechaLimite);
                modeloLista.clear();
                tareasFiltradas.forEach(t -> modeloLista.addElement(t.toString()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Debe ser YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
