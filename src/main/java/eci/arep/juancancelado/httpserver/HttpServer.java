package eci.arep.juancancelado.httpserver;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

import java.io.IOException;

import java.io.File;

public class HttpServer {

    private static final List<Map<String, String>> tasks = new ArrayList<>();
    private static final Map<String, Route> getRoutes = new ConcurrentHashMap<>();
    private static String staticDir = "src/main/webapp";

    public static void staticfiles(String dir) {
        staticDir = dir;
    }

    public static void get(String path, Route route) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        getRoutes.put(path, route);
    }

    public static void start(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Servidor en http://localhost:" + port);
        while (true) {
            Socket client = server.accept();
            new Thread(() -> {
                try {
                    handle(client);
                } catch (Exception ignored) {
                } finally {
                    try {
                        client.close();
                    } catch (IOException ignored) {
                    }
                }
            }).start();
        }
    }

    public static void main(String[] args) throws Exception {
        staticfiles("src/main/webapp");

        get("/App/hello", (req, res) -> "Hello " + req.getValues("name"));
        get("/App/pi", (req, res) -> String.valueOf(Math.PI));

        start(8080);
    }

    private static void handle(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        OutputStream out = client.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return;
        }
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String rawPath = parts[1]; // Ej: "/App/hello?name=juan"

        // separar path y query string
        String pathOnly = rawPath.split("\\?")[0]; // "/App/hello"

        // ignorar headers
        while ((in.readLine()) != null && !in.ready()) {
        }

        Request req = new Request(method, rawPath);
        Response res = new Response();

        // 1) si hay ruta definida
        Route route = getRoutes.get(pathOnly);
        if (route != null) {
            try {
                String body = route.handle(req, res);
                sendText(out, res.status, res.type, body);
            } catch (Exception e) {
                sendText(out, 500, "text/plain", "Error: " + e.getMessage());
            }
            return;
        }

        // 2) endpoint especial de tareas
        if (pathOnly.startsWith("/tasks")|| pathOnly.startsWith("/api/tasks")) {
            handleTasks(out, method, req);
            return;
        }

        if (method.equals("GET")) {
            route = getRoutes.get(pathOnly);
        } else if (method.equals("POST")) {
            route = postRoutes.get(pathOnly);
        }

        if (route != null) {
            try {
                String body = route.handle(req, res);
                sendText(out, res.status, res.type, body);
            } catch (Exception e) {
                sendText(out, 500, "text/plain", "Error: " + e.getMessage());
            }
            return;
        }

        serveStatic(out, pathOnly);
    }

    private static void serveStatic(OutputStream out, String rawPath) throws IOException {
        String path = rawPath.equals("/") ? "/index.html" : rawPath;
        File file = new File(staticDir + path);
        if (file.exists() && file.isFile()) {
            byte[] data = Files.readAllBytes(file.toPath());
            sendBinary(out, 200, guessContentType(path), data);
        } else {
            sendText(out, 404, "text/plain", "404 Not Found: " + path);
        }
    }

    private static void handleTasks(OutputStream out, String method, Request req) throws IOException {
        String name = req.getValues("name");
        String type = req.getValues("type");

        switch (method) {
            case "GET":
                sendText(out, 200, "application/json", tasksToJson());
                break;
            case "POST":
                if (!name.isEmpty()) {
                    Map<String, String> t = new HashMap<>();
                    t.put("name", name);
                    t.put("type", type.isEmpty() ? "otro" : type);
                    tasks.add(t);
                    sendText(out, 200, "text/plain", "Tarea agregada: " + name);
                } else {
                    sendText(out, 400, "text/plain", "Nombre requerido");
                }
                break;
            case "DELETE":
                boolean removed = tasks.removeIf(t -> t.get("name").equals(name) && t.get("type").equals(type));
                sendText(out, 200, "text/plain", removed ? "Tarea eliminada" : "No encontrada");
                break;
            case "RESET":
                tasks.clear();
                sendText(out, 200, "text/plain", "Lista reiniciada");
                break;
            default:
                sendText(out, 405, "text/plain", "Método no soportado");
        }
    }

    // Método para convertir la lista de tareas a JSON
    private static String tasksToJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < tasks.size(); i++) {
            Map<String, String> task = tasks.get(i);
            sb.append("{");
            sb.append("\"name\":\"").append(task.get("name")).append("\",");
            sb.append("\"type\":\"").append(task.get("type")).append("\"");
            sb.append("}");
            if (i < tasks.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static void sendText(OutputStream out, int status, String type, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 " + status + " OK\r\n"
                + "Content-Type: " + type + "; charset=utf-8\r\n"
                + "Content-Length: " + data.length + "\r\n\r\n";
        out.write(headers.getBytes());
        out.write(data);
    }

    private static void sendBinary(OutputStream out, int status, String type, byte[] data) throws IOException {
        String headers = "HTTP/1.1 " + status + " OK\r\n"
                + "Content-Type: " + type + "\r\n"
                + "Content-Length: " + data.length + "\r\n\r\n";
        out.write(headers.getBytes());
        out.write(data);
    }

    private static String guessContentType(String name) {
        if (name.endsWith(".html")) {
            return "text/html";
        }
        if (name.endsWith(".css")) {
            return "text/css";
        }
        if (name.endsWith(".js")) {
            return "application/javascript";
        }
        if (name.endsWith(".png")) {
            return "image/png";
        }
        if (name.endsWith(".jpg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    public static void runServer(String[] args) {
        try {
            staticfiles("src/main/webapp");
            if (args.length < 1) {
                System.err.println("Uso: java -cp target/classes eci.arep.juancancelado.microspringboot.MicroSpringBoot <FQNControlador>");
                return;
            }
            String controllerFqn = args[0];
            Class<?> clazz = Class.forName(controllerFqn);
            eci.arep.juancancelado.microspringboot.MicroSpringBoot.registerController(clazz);
            start(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Map<String, Route> postRoutes = new ConcurrentHashMap<>();

    public static void post(String path, Route route) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        postRoutes.put(path, route);
    }

}
