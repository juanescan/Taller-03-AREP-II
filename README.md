# Taller-03-AREP-II

## Gestor de Tareas con MicroSpringBoot en Java

Este proyecto implementa un **micro framework estilo Spring Boot** en Java puro que permite definir controladores REST usando anotaciones personalizadas (`@RestController`, `@GetMapping`, `@PostMapping`, etc.).  
Sobre esta base, se desarrolla una aplicación web de gestión de tareas con frontend en HTML/JS y backend en Java.

La aplicación permite:

- Agregar tareas con nombre y tipo (casa, universidad, trabajo, otro).  
- Listar las tareas en la interfaz web de forma dinámica.  
- Persistencia en memoria (las tareas se almacenan en una lista durante la ejecución del servidor).  

## ⚙️ Instalación

1. Clona este repositorio o descarga los archivos:  
   ```bash
   git clone https://github.com/juanescan/Taller-03-AREP.git
   cd Taller-03-AREP

2. Compila el proyecto con Maven:
    ```bash
   mvn clean install
3. Asegúrate de tener Java 11+ y Maven instalados.
## ▶️️ Ejecución

    java -cp target/classes eci.arep.juancancelado.microspringboot.MicroSpringBoot eci.arep.juancancelado.microspringboot.examples.TaskController 

![imagenes](/imagenes/Taller3.png)

El servidor se ejecutará en el puerto 8080:

👉 http://localhost:8080
