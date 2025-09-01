/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author juane
 */
import eci.arep.juancancelado.mavenproject1.HttpServer;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerTestHelper {
    public static void invokeHandleTasks(OutputStream out, String method, String path) throws IOException {
        try {
            Method m = HttpServer.class.getDeclaredMethod("handleTasks", OutputStream.class, String.class, String.class);
            m.setAccessible(true);
            m.invoke(null, out, method, path);
        } catch (Exception e) {
            throw new IOException("Error invocando handleTasks", e);
        }
    }
}

