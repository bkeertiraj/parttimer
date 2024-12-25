package com.example.PartTimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PartTimerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartTimerApplication.class, args);
	}

}
