/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eci.arep.juancancelado.mavenproject1;

/**
 *
 * @author juane
 */
   public class Response {
        int status = 200;
        String type = "text/plain";
        public Response status(int s) { this.status = s; return this; }
        public Response type(String t) { this.type = t; return this; }
    }
