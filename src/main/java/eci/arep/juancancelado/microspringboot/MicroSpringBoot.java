/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eci.arep.juancancelado.microspringboot;

import eci.arep.juancancelado.model.Task;
import eci.arep.juancancelado.httpserver.HttpServer;
import eci.arep.juancancelado.httpserver.Request;
import eci.arep.juancancelado.microspringboot.annotations.GetMapping;
import eci.arep.juancancelado.microspringboot.annotations.PostMapping;
import eci.arep.juancancelado.microspringboot.annotations.RequestBody;
import eci.arep.juancancelado.microspringboot.annotations.RequestParam;
import eci.arep.juancancelado.microspringboot.annotations.RestController;
import eci.arep.juancancelado.microspringboot.examples.GreetingController;
import eci.arep.juancancelado.microspringboot.examples.TaskController;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;


public class MicroSpringBoot {

    public static void main(String[] args) {
        System.out.println("Iniciando MicroSpringBoot...");
        loadControllers();
        HttpServer.staticfiles("src/main/webapp");
        HttpServer.start(8080);
        System.out.println("El servidor está corriendo en http://localhost:8080");
    }

    private static void loadControllers() {
        registerController(new GreetingController()); // http://localhost:8080/greeting?name=Juan
        registerController(new TaskController());     // http://localhost:8080/api/tasks
    }

    private static void registerController(Object controllerInstance) {
        Class<?> beanClass = controllerInstance.getClass();
        if (!beanClass.isAnnotationPresent(RestController.class)) {
            System.err.println("La clase " + beanClass.getName() + " no está anotada con @RestController.");
            return;
        }

        Method[] methods = beanClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = m.getAnnotation(GetMapping.class);
                String path = mapping.value();
                HttpServer.get(path, (req, res) -> {
                    try {
                        return invokeMethod(m, controllerInstance, req);
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                        return "{\"error\":\"Internal Server Error\"}";
                    }
                });
            }
            if (m.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = m.getAnnotation(PostMapping.class);
                String path = mapping.value();
                HttpServer.post(path, (req, res) -> {
                    try {
                        return invokeMethod(m, controllerInstance, req);
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                        return "{\"error\":\"Internal Server Error\"}";
                    }
                });
            }
        }
    }

    public static String invokeMethod(Method method, Object instance, Request req) throws InvocationTargetException {
        try {
            Parameter[] parameters = method.getParameters();
            Object[] argsForMethod = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (parameter.isAnnotationPresent(RequestBody.class)) {
                    if (Map.class.isAssignableFrom(parameter.getType())) {
                        argsForMethod[i] = HttpServer.parseJson(req.getBody());
                    } else {
                        argsForMethod[i] = req.getBody();
                    }
                } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                    RequestParam reqParam = parameter.getAnnotation(RequestParam.class);
                    String paramName = reqParam.value();
                    String value = req.getQueryParams().get(paramName);
                    if (value == null || value.isEmpty()) {
                        value = reqParam.defaultValue();
                    }
                    argsForMethod[i] = value;
                } else {
                    argsForMethod[i] = null;
                }
            }

            Object result = method.invoke(instance, argsForMethod);

            // 🔹 Serializar listas de Task como JSON
            if (result instanceof List<?>) {
                List<?> list = (List<?>) result;
                if (!list.isEmpty() && list.get(0) instanceof Task) {
                    return HttpServer.toJson((List<Task>) list);
                }
            }

            // 🔹 Map como JSON simple
            if (result instanceof Map<?, ?>) {
                StringBuilder sb = new StringBuilder("{");
                Map<?, ?> map = (Map<?, ?>) result;
                int count = 0;
                for (var entry : map.entrySet()) {
                    sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
                    if (++count < map.size()) sb.append(",");
                }
                sb.append("}");
                return sb.toString();
            }

            return result != null ? result.toString() : "";

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return "{\"error\": \"Internal Server Error\"}";
        }
    }
}
