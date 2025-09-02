

import eci.arep.juancancelado.httpserver.HttpServer;
import eci.arep.juancancelado.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    @BeforeEach
    public void setup() {
        // Limpiar la lista de tareas antes de cada test
        HttpServer.getRoutes().clear();
        HttpServer.postRoutes().clear();
        HttpServer.clearTasks();
    }

    @Test
    public void testParseJsonValid() {
        String json = "{\"name\":\"Tarea 1\",\"type\":\"casa\"}";
        Map<String, String> result = HttpServer.parseJson(json);

        assertEquals(2, result.size());
        assertEquals("Tarea 1", result.get("name"));
        assertEquals("casa", result.get("type"));
    }

    @Test
    public void testParseJsonEmpty() {
        String json = "{}";
        Map<String, String> result = HttpServer.parseJson(json);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToJsonSingleTask() {
        Task task = new Task("Tarea 1", "casa");
        List<Task> tasks = List.of(task);
        String json = HttpServer.toJson(tasks);

        assertEquals("[{\"name\":\"Tarea 1\",\"type\":\"casa\"}]", json);
    }

    @Test
    public void testToJsonMultipleTasks() {
        Task t1 = new Task("T1", "casa");
        Task t2 = new Task("T2", "trabajo");
        List<Task> tasks = List.of(t1, t2);

        String json = HttpServer.toJson(tasks);
        assertEquals("[{\"name\":\"T1\",\"type\":\"casa\"},{\"name\":\"T2\",\"type\":\"trabajo\"}]", json);
    }

    @Test
    public void testAddAndRetrieveTask() {
        // Simular POST
        HttpServer.addTask(new Task("Test", "Trabajo"));

        List<Task> tasks = HttpServer.getTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test", tasks.get(0).getName());
        assertEquals("Trabajo", tasks.get(0).getType());
    }

    @Test
    public void testEmptyTaskListToJson() {
        List<Task> tasks = HttpServer.getTasks();
        String json = HttpServer.toJson(tasks);
        assertEquals("[]", json);
    }
}
