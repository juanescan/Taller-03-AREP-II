package eci.arep.juancancelado.httpserver;

import eci.arep.juancancelado.model.Task;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class HttpServer {

    private static final List<Task> tasks = new ArrayList<>();

    private static final Map<String, BiFunction<Request, Response, String>> getRoutes = new HashMap<>();
    private static final Map<String, BiFunction<Request, Response, String>> postRoutes = new HashMap<>();
    private static String staticDirectory = "src/main/webapp";

    public static void main(String[] args) {
        staticfiles("src/main/webapp");

        get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        get("/pi", (req, res) -> String.valueOf(Math.PI));

        get("/api/tasks", (req, res) -> {
            if (tasks.isEmpty()) {
                return "[]";
            }
            return toJson(tasks);
        });

        post("/api/tasks", (req, res) -> {
            String body = req.getBody();
            Map<String, String> data = parseJson(body);

            if (data.containsKey("name") && data.containsKey("type") && data.containsKey("price")) {
                try {
                    tasks.add(new Task(data.get("name"), data.get("type")));
                    return "{\"message\": \"Task added successfully\"}";
                } catch (NumberFormatException e) {
                    return "{\"error\": \"Invalid price format\"}";
                }
            }
            return "{\"error\": \"Missing fields\"}";

        });

        start(8080);
    }

    public static void get(String path, BiFunction<Request, Response, String> handler) {
        getRoutes.put(path, handler);
    }

    public static void post(String path, BiFunction<Request, Response, String> handler) {
        postRoutes.put(path, handler);
    }

    public static void staticfiles(String path) {
        staticDirectory = path;
        System.out.println("Archivos estáticos servidos desde: " + new File(staticDirectory).getAbsolutePath());
    }

    public static void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor en ejecución en el puerto " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor");
            System.exit(1);
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); OutputStream out = clientSocket.getOutputStream()) {
            String requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }

            System.out.println("Solicitud: " + requestLine);
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                return;
            }

            String method = requestParts[0];
            String fullPath = requestParts[1];
            String path = fullPath.split("\\?")[0];
            Map<String, String> queryParams = getQueryParams(fullPath);

            Request req = new Request(method, path, queryParams);
            Response res = new Response();

            // 🔹 Si la ruta está definida en el hashmap `routes`
            if (method.equals("GET") && getRoutes.containsKey(path)) {
                String responseBody = getRoutes.get(path).apply(req, res);
                sendResponse(out, 200, "application/json", responseBody);
            } else if (method.equals("POST") && postRoutes.containsKey(path)) {
                String body = readRequestBody(in);
                req.setBody(body);
                String responseBody = postRoutes.get(path).apply(req, res);
                sendResponse(out, 201, "application/json", responseBody);
            } else {
                System.out.println("Buscando archivo estático en: " + staticDirectory + path);
                serveStaticFile(path, out);
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readRequestBody(BufferedReader in) throws IOException {
        StringBuilder body = new StringBuilder();
        int contentLength = 0;

        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.substring(15).trim());
            }
        }

        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer);
        }

        String jsonBody = body.toString();
        System.out.println("JSON Recibido: " + jsonBody);
        return jsonBody;
    }

    private static Map<String, String> getQueryParams(String fullPath) {
        Map<String, String> queryParams = new HashMap<>();
        if (fullPath.contains("?")) {
            String queryString = fullPath.split("\\?")[1];
            String[] params = queryString.split("&");

            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return queryParams;
    }

    private static void serveStaticFile(String path, OutputStream out) throws IOException {
        if (path.equals("/")) {
            path = "/index.html";
        }
        File file = new File("src/main/webapp" + path);
        if (file.exists() && !file.isDirectory()) {
            String contentType = getContentType(path);
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            out.write(("HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\n\r\n").getBytes());
            out.write(fileBytes);
        } else {
            out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
        }
    }

    private static void sendResponse(OutputStream out, int statusCode, String contentType, String body) throws IOException {
        String statusLine = "HTTP/1.1 " + statusCode + " " + getStatusMessage(statusCode) + "\r\n";
        String headers = "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length() + "\r\n"
                + "Connection: close\r\n\r\n";

        out.write((statusLine + headers + body).getBytes());
        out.flush();
    }

    private static String getStatusMessage(int statusCode) {
        return switch (statusCode) {
            case 200 ->
                "OK";
            case 201 ->
                "Created";
            case 400 ->
                "Bad Request";
            case 404 ->
                "Not Found";
            case 405 ->
                "Method Not Allowed";
            case 500 ->
                "Internal Server Error";
            default ->
                "Unknown Status";
        };
    }

    private static String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        if (path.endsWith(".png")) {
            return "image/png";
        }
        if (path.endsWith(".jpeg") || path.endsWith(".jpg")) {
            return "image/jpeg";
        }
        if (path.endsWith(".gif")) {
            return "image/gif";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (path.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "application/octet-stream";
    }

    public static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();

        if (json == null || json.isEmpty()) {
            return map;
        }

        json = json.trim();

        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
        } else {
            return map;
        }

        String[] pairs = json.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                map.put(key, value);
            }
        }
        return map;
    }

    public static String toJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            json.append("{")
                    .append("\"name\":\"").append(task.getName()).append("\",")
                    .append("\"type\":\"").append(task.getType()).append("\"")
                    .append("}");
            if (i < tasks.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }


    public static List<Task> getTasks() {
        return tasks;
    }

    public static void addTask(Task task) {
        tasks.add(task);
    }

    public static void clearTasks() {
        tasks.clear();
    }

    public static Map<String, BiFunction<Request, Response, String>> getRoutes() {
        return getRoutes;
    }

    public static Map<String, BiFunction<Request, Response, String>> postRoutes() {
        return postRoutes;
    }

}
