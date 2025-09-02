

import eci.arep.juancancelado.microspringboot.examples.TaskController;
import eci.arep.juancancelado.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TaskControllerTest {

    private TaskController taskController;

    @BeforeEach
    public void setup() {
        taskController = new TaskController();
    }

    @Test
    public void testGetTaskInitiallyEmpty() {
        List<Task> tasks = taskController.getTask();
        assertNotNull(tasks, "La lista no debe ser null");
        assertEquals(0, tasks.size(), "La lista debe estar vacía al inicio");
    }

    @Test
    public void testAddTaskSuccessfully() {
        Map<String, String> data = Map.of(
                "name", "Tarea 1",
                "type", "casa"
        );

        Map<String, String> response = taskController.addTask(data);
        assertEquals("Task added successfully", response.get("message"));

        List<Task> tasks = taskController.getTask();
        assertEquals(1, tasks.size());
        assertEquals("Tarea 1", tasks.get(0).getName());
        assertEquals("casa", tasks.get(0).getType());
    }

    @Test
    public void testAddTaskMissingFields() {
        Map<String, String> data = Map.of(
                "name", "Tarea 2"
        );

        Map<String, String> response = taskController.addTask(data);
        assertEquals("Missing fields", response.get("error"));

        List<Task> tasks = taskController.getTask();
        assertEquals(0, tasks.size(), "No se debe agregar tarea si faltan campos");
    }

    @Test
    public void testAddMultipleTasks() {
        taskController.addTask(Map.of("name", "T1", "type", "casa"));
        taskController.addTask(Map.of("name", "T2", "type", "trabajo"));

        List<Task> tasks = taskController.getTask();
        assertEquals(2, tasks.size());
        assertEquals("T1", tasks.get(0).getName());
        assertEquals("T2", tasks.get(1).getName());
    }
}
