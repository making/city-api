package com.example.city;

import org.springframework.boot.SpringApplication;

public class TestCityApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(CityApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
