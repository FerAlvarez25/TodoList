package util;
import controller.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.FilePersistenceException;
import model.Tarea;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;   
import com.google.gson.GsonBuilder;
import java.time.LocalDate;

    public class FileManager {
        private String filePath;
        private Gson gson;
    

        public FileManager(String filePath) {
            this.filePath = filePath;
            this.gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())  // Corregir registro de adaptador
                            .create();
            crearDirectorioSiNoExiste();
        }
        
    
        // El resto de la clase FileManager permanece igual

    

    // Método para crear el directorio si no existe
    private void crearDirectorioSiNoExiste() {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();  // Crea todos los directorios necesarios
        }
    }

    // Método para guardar la lista de tareas en un archivo JSON
    public void guardarTareas(List<Tarea> tareas) throws FilePersistenceException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(tareas, writer);
        } catch (IOException e) {
            throw new FilePersistenceException("Error al guardar las tareas en el archivo.", e);
        }
    }

    // Método para cargar las tareas desde un archivo JSON
    public List<Tarea> cargarTareas() throws FilePersistenceException {
        try (Reader reader = new FileReader(filePath)) {
            Type tareaListType = new TypeToken<List<Tarea>>(){}.getType();
            List<Tarea> tareas = gson.fromJson(reader, tareaListType);  // Deserialización

            // Si el archivo existe pero está vacío, también devolvemos una lista modificable
            return (tareas != null) ? new ArrayList<>(tareas) : new ArrayList<>();
        } catch (FileNotFoundException e) {
            // Si el archivo no existe, devolvemos una lista vacía modificable
            return new ArrayList<>();
        } catch (IOException e) {
            throw new FilePersistenceException("Error al cargar las tareas desde el archivo.", e);
        }
    }
}
