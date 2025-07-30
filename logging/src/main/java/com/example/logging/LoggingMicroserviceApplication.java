package com.example.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class LoggingMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoggingMicroserviceApplication.class, args);
	}

}
