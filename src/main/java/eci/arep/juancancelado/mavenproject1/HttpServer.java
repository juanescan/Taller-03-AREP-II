package eci.arep.juancancelado.mavenproject1;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import com.google.gson.Gson; // Usa Gson (añádelo a tu pom.xml si usas Maven)
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class HttpServer {

    private static final List<Map<String, String>> tasks = new ArrayList<>();
    private static final Gson gson = new Gson();
    private static final Map<String, Route> getRoutes = new ConcurrentHashMap<>();
    private static String staticDir = "src/main/webapp";
    
     public static void staticfiles(String dir) {
    staticDir = dir;
    }
       public static void get(String path, Route route) {
        if (!path.startsWith("/")) path = "/" + path;
        getRoutes.put(path, route);
    }
       
      public static void start(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Servidor en http://localhost:" + port);
        while (true) {
            Socket client = server.accept();
            new Thread(() -> {
                try { handle(client); } catch(Exception ignored) {}
                finally { try { client.close(); } catch(IOException ignored){} }
            }).start();
        }
    }

    public static void main(String[] args) throws Exception {
    staticfiles("src/main/webapp");

    get("/App/hello", (req,res) -> "Hello " + req.getValues("name"));
    get("/App/pi", (req,res) -> String.valueOf(Math.PI));

    start(8080);
}


     private static void handle(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        OutputStream out = client.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) return;
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String rawPath = parts[1];

        // ignorar headers
        while ((in.readLine()) != null && !in.ready()) {}

        Request req = new Request(method, rawPath);
        Response res = new Response();

        // 1) si hay ruta definida
        Route route = getRoutes.get(req.path());
        if (route != null) {
            try {
                String body = route.handle(req, res);
                sendText(out, res.status, res.type, body);
            } catch(Exception e) {
                sendText(out, 500, "text/plain", "Error: "+e.getMessage());
            }
            return;
        }

        // 2) endpoint especial de tareas
        if (req.path().startsWith("/tasks")) {
            handleTasks(out, method, req);
            return;
        }

        // 3) servir estáticos
        serveStatic(out, rawPath);
    }
     
     private static void serveStatic(OutputStream out, String rawPath) throws IOException {
    // Si no hay ruta ("/"), devolver index.html
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
        switch(method) {
            case "GET":
                sendText(out, 200, "application/json", gson.toJson(tasks));
                break;
            case "POST":
                if (!name.isEmpty()) {
                    Map<String,String> t = new HashMap<>();
                    t.put("name", name);
                    t.put("type", type.isEmpty()?"otro":type);
                    tasks.add(t);
                    sendText(out, 200, "text/plain", "Tarea agregada: "+name);
                } else {
                    sendText(out, 400, "text/plain", "Nombre requerido");
                }
                break;
            case "DELETE":
                boolean removed = tasks.removeIf(t -> t.get("name").equals(name) && t.get("type").equals(type));
                sendText(out, 200, "text/plain", removed?"Tarea eliminada":"No encontrada");
                break;
            case "RESET":
                tasks.clear();
                sendText(out, 200, "text/plain", "Lista reiniciada");
                break;
            default:
                sendText(out, 405, "text/plain", "Método no soportado");
        }
    }

    private static void sendText(OutputStream out, int status, String type, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 "+status+" OK\r\n"+
                         "Content-Type: "+type+"; charset=utf-8\r\n"+
                         "Content-Length: "+data.length+"\r\n\r\n";
        out.write(headers.getBytes());
        out.write(data);
    }

      private static void sendBinary(OutputStream out, int status, String type, byte[] data) throws IOException {
        String headers = "HTTP/1.1 "+status+" OK\r\n"+
                         "Content-Type: "+type+"\r\n"+
                         "Content-Length: "+data.length+"\r\n\r\n";
        out.write(headers.getBytes());
        out.write(data);
    }

     private static String guessContentType(String name) {
        if (name.endsWith(".html")) return "text/html";
        if (name.endsWith(".css")) return "text/css";
        if (name.endsWith(".js")) return "application/javascript";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg")) return "image/jpeg";
        return "application/octet-stream";
    }

    private static void sendResponse(OutputStream out, String contentType, String content) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + content.getBytes().length + "\r\n"
                + "\r\n"
                + content;
        out.write(response.getBytes());
    }

}