package com.example.city;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CityController {

	private final CityRepository cityRepository;

	public CityController(CityRepository cityRepository) {
		this.cityRepository = cityRepository;
	}

	@GetMapping(path = "/cities")
	public List<City> getCities() {
		return this.cityRepository.findAll();
	}

	@PostMapping(path = "/cities")
	@ResponseStatus(HttpStatus.CREATED)
	public City postCities(@RequestBody City city) {
		return cityRepository.save(city);
	}

}
