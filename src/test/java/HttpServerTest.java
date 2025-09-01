import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    @Test
    public void testAddTask() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // POST con parámetros válidos
        HttpServerTestHelper.invokeHandleTasks(out, "POST", "/tasks?name=Tarea1&type=Trabajo");

        String response = out.toString();
        assertTrue(response.contains("Tarea agregada: Tarea1 (Trabajo)"));
    }

    @Test
    public void testGetTasks() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Primero añadimos una tarea
        HttpServerTestHelper.invokeHandleTasks(new ByteArrayOutputStream(), "POST", "/tasks?name=Tarea2&type=Casa");

        // Ahora consultamos
        HttpServerTestHelper.invokeHandleTasks(out, "GET", "/tasks");

        String response = out.toString();
        assertTrue(response.contains("Tarea2"));
        assertTrue(response.contains("Casa"));
    }

    @Test
    public void testDeleteTask() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Primero añadimos
        HttpServerTestHelper.invokeHandleTasks(new ByteArrayOutputStream(), "POST", "/tasks?name=Tarea3&type=Escuela");

        // Eliminamos
        HttpServerTestHelper.invokeHandleTasks(out, "DELETE", "/tasks?name=Tarea3&type=Escuela");

        String response = out.toString();
        assertTrue(response.contains("Tarea eliminada: Tarea3 (Escuela)"));
    }

    @Test
    public void testResetTasks() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Añadimos varias
        HttpServerTestHelper.invokeHandleTasks(new ByteArrayOutputStream(), "POST", "/tasks?name=Tarea4&type=Casa");
        HttpServerTestHelper.invokeHandleTasks(new ByteArrayOutputStream(), "POST", "/tasks?name=Tarea5&type=Trabajo");

        // Reseteamos
        HttpServerTestHelper.invokeHandleTasks(out, "RESET", "/tasks");

        String response = out.toString();
        assertTrue(response.contains("Lista reiniciada"));

        // Verificamos que GET ahora está vacío
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        HttpServerTestHelper.invokeHandleTasks(out2, "GET", "/tasks");
        String response2 = out2.toString();
        assertTrue(response2.contains("[]"));
    }

    @Test
    public void testMetodoNoSoportado() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        HttpServerTestHelper.invokeHandleTasks(out, "PUT", "/tasks?name=Algo&type=X");

        String response = out.toString();
        assertTrue(response.contains("Método no soportado: PUT"));
    }
}