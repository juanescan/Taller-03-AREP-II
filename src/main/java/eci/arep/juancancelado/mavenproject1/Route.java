/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eci.arep.juancancelado.mavenproject1;

/**
 *
 * @author juane
 */
@FunctionalInterface
    public interface Route {
        String handle(Request req, Response res) throws Exception;
    }