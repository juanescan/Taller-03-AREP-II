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
   git clone https://github.com/juanescan/Taller-03-AREP-II.git
   cd Taller-03-AREP-II

2. Compila el proyecto con Maven:
    ```bash
   mvn clean install
3. Asegúrate de tener Java 11+ y Maven instalados.
## ▶️️ Ejecución

    java -cp target/classes eci.arep.juancancelado.microspringboot.MicroSpringBoot eci.arep.juancancelado.microspringboot.examples.TaskController 

![imagenes](/imagenes/Taller3.png)

El servidor se ejecutará en el puerto 8080:

👉 http://localhost:8080


## 🏗️ Arquitectura

El sistema está compuesto por tres capas principales:

1. MicroSpringBoot (Framework casero)
    - Levanta un servidor HTTP sobre ServerSocket.
    - Soporta anotaciones para definir controladores REST (@RestController, @GetMapping, @PostMapping, @RequestBody).
    - Expone servicios configurados en clases como TaskController.

2. Servidor HTTP
    - Sirve archivos estáticos desde la carpeta src/main/webapp.
    - Procesa solicitudes GET y POST registradas en los controladores.
    - Serializa objetos Task a formato JSON.

3. Aplicación Web (Frontend)
    - index.html: interfaz gráfica para gestionar tareas.
    - script.js: comunicación con el servidor usando fetch().
    - La lista de tareas se actualiza dinámicamente sin recargar la página.

## API REST

- GET /api/tasks → Listar tareas en JSON.
- POST /api/tasks → Agregar tarea enviando JSON:

{
  "name": "Estudiar AREP",
  "type": "Universidad"
}

## Estructura

```
src/
  main/
    java/
      eci/
        arep/
         juancancelado/ 
            httpserver/
                HttpServer.java
                Request.java
                Response.java
                Route.java
            microspringboot/
                annotations/
                    GetMapping.java
                    PostMapping.java
                    RequestBody.java
                    RequestParam.java
                    RestController.java
                examples/
                    GreetingController.java
                    TaskController.java
            model/
                Task.java
    webapp/
        fondo.png
        index.html
        script.js
        style.css
  test/
    java/
pom.xml
README.md

```

## Test

![Test](/imagenes/testT3.png)

## APP

![App](/imagenes/APP1.png)

![App](/imagenes/APP2.png)

![App](/imagenes/APP3.png)

![App](/imagenes/APP4.png) 

![App](/imagenes/APP5.png) 

![App](/imagenes/APP6.png)

![App](/imagenes/APP7.png)

## Built With

- Java SE - Lenguaje de programación

- Maven - Herramienta de gestión de dependencias y construcción

## Authors 
- Juan Esteban Cancelado Sanchez - *AREP* *Taller 1* - [juanescan](https://github.com/juanescan)