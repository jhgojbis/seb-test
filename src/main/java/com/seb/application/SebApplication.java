package com.seb.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.seb")
public class SebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SebApplication.class, args);
	}

}
