/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eci.arep.juancancelado.microspringboot.examples;

import eci.arep.juancancelado.clase.Task;
import eci.arep.juancancelado.microspringboot.annotations.GetMapping;
import eci.arep.juancancelado.microspringboot.annotations.PostMapping;
import eci.arep.juancancelado.microspringboot.annotations.RequestParam;
import eci.arep.juancancelado.microspringboot.annotations.RestController;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juane
 */
@RestController
public class TaskController {

    private static final List<Task> tasks = new ArrayList<>();

    @GetMapping("/api/tasks")
    public String getTasks() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            sb.append("{\"name\":\"").append(t.getName())
                    .append("\",\"type\":\"").append(t.getType()).append("\"}");
            if (i < tasks.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @PostMapping("/api/tasks")
    public String addTask(
            @RequestParam("name") String name,
            @RequestParam(value = "type", defaultValue = "otro") String type) {
        tasks.add(new Task(name, type));
        return "{\"status\":\"ok\",\"message\":\"Tarea agregada\"}";
    }

}
