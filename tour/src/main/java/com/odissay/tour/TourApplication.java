package com.odissay.tour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
public class TourApplication {

	public static void main(String[] args) {
		SpringApplication.run(TourApplication.class, args);
	}

}
