/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eci.arep.juancancelado.microspringboot;

import eci.arep.juancancelado.httpserver.HttpServer;
import eci.arep.juancancelado.httpserver.Request;
import eci.arep.juancancelado.httpserver.Response;
import eci.arep.juancancelado.httpserver.Route;
import eci.arep.juancancelado.microspringboot.annotations.GetMapping;
import eci.arep.juancancelado.microspringboot.annotations.PostMapping;
import eci.arep.juancancelado.microspringboot.annotations.RequestParam;
import eci.arep.juancancelado.microspringboot.annotations.RestController;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;

/**
 *
 * @author juane
 */
public class MicroSpringBoot {

    public static void main(String[] args) {
        System.out.println("Starting MicroSpringBoot!");
        HttpServer.runServer(args);
    }

    /**
     * Registra controladores con @RestController y métodos @GetMapping.
     */
    public static void registerController(Class<?> clazz) throws Exception {
    if (!clazz.isAnnotationPresent(RestController.class)) {
        System.out.println("Clase " + clazz.getName() + " no tiene @RestController.");
        return;
    }

    Object controller = clazz.getDeclaredConstructor().newInstance();

    for (Method m : clazz.getDeclaredMethods()) {
        String path = null;
        boolean isGet = false, isPost = false;

        if (m.isAnnotationPresent(GetMapping.class)) {
            path = m.getAnnotation(GetMapping.class).value();
            isGet = true;
        } else if (m.isAnnotationPresent(PostMapping.class)) {
            path = m.getAnnotation(PostMapping.class).value();
            isPost = true;
        }

        if (path == null) continue;
        if (!path.startsWith("/")) path = "/" + path;
        final String finalPath = path;

        Route route = (Request req, Response res) -> {
            Parameter[] params = m.getParameters();
            Object[] args = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                RequestParam rp = getRequestParam(params[i].getAnnotations());
                String raw = req.getValues(rp.value());
                if (raw.isEmpty()) raw = rp.defaultValue();
                args[i] = coerce(raw, params[i].getType());
            }

            Object result = m.invoke(controller, args);
            return result == null ? "" : result.toString();
        };

        if (isGet) {
            HttpServer.get(finalPath, route);
            System.out.println("Ruta registrada: GET " + finalPath);
        }
        if (isPost) {
            HttpServer.post(finalPath, route);
            System.out.println("Ruta registrada: POST " + finalPath);
        }
    }
}


    private static RequestParam getRequestParam(Annotation[] anns) {
        for (Annotation a : anns) {
            if (a instanceof RequestParam) {
                return (RequestParam) a;
            }
        }
        return null;
    }

    private static Object coerce(String raw, Class<?> type) {
        if (type == String.class) {
            return raw;
        }
        if (type == int.class || type == Integer.class) {
            return raw.isEmpty() ? 0 : Integer.parseInt(raw);
        }
        if (type == long.class || type == Long.class) {
            return raw.isEmpty() ? 0L : Long.parseLong(raw);
        }
        if (type == double.class || type == Double.class) {
            return raw.isEmpty() ? 0.0 : Double.parseDouble(raw);
        }
        return raw;
    }
}
