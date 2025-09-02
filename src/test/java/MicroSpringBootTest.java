

import eci.arep.juancancelado.httpserver.Request;
import eci.arep.juancancelado.microspringboot.MicroSpringBoot;
import eci.arep.juancancelado.microspringboot.examples.GreetingController;
import eci.arep.juancancelado.microspringboot.examples.TaskController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MicroSpringBootTest {

    private GreetingController greetingController;
    private TaskController taskController;

    @BeforeEach
    public void setup() {
        greetingController = new GreetingController();
        taskController = new TaskController();
    }


    @Test
    public void testInvokeTaskControllerGetEmptyList() throws Exception {
        Method method = TaskController.class.getDeclaredMethod("getTask");
        Request req = new Request("GET", "/api/tasks", Map.of());

        String response = MicroSpringBoot.invokeMethod(method, taskController, req);
        assertEquals("[]", response);
    }

    @Test
    public void testInvokeTaskControllerAddTask() throws Exception {
        Method method = TaskController.class.getDeclaredMethod("addTask", Map.class);

        Request req = new Request("POST", "/api/tasks", Map.of());
        req.setBody("{\"name\":\"T1\",\"type\":\"casa\"}");

        String response = MicroSpringBoot.invokeMethod(method, taskController, req);
        assertTrue(response.contains("Task added successfully"));

        // Verificar que el GET ahora devuelve 1 tarea
        Method getMethod = TaskController.class.getDeclaredMethod("getTask");
        String getResponse = MicroSpringBoot.invokeMethod(getMethod, taskController, new Request("GET", "/api/tasks", Map.of()));
        assertTrue(getResponse.contains("\"name\":\"T1\""));
        assertTrue(getResponse.contains("\"type\":\"casa\""));
    }
}
