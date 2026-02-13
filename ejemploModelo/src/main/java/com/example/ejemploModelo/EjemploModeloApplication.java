package com.example.ejemploModelo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EjemploModeloApplication {

	public static void main(String[] args) {
		SpringApplication.run(EjemploModeloApplication.class, args);
	}

}
