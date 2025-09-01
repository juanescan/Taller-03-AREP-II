/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eci.arep.juancancelado.microspringboot.examples;

import eci.arep.juancancelado.microspringboot.annotations.GetMapping;
import eci.arep.juancancelado.microspringboot.annotations.RequestParam;
import eci.arep.juancancelado.microspringboot.annotations.RestController;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author juane
 */
@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/greeting")
	public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hola " + name;
	}
}
