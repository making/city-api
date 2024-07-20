package com.example.city;

import java.util.List;

import am.ik.spring.http.client.RetryableClientHttpRequestInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureJsonTesters
@TestMethodOrder(OrderAnnotation.class)
@Testcontainers(disabledWithoutDocker = true)
class CityControllerTest {

	@LocalServerPort
	int port;

	@Autowired
	RestClient.Builder restClientBuilder;

	RestClient restClient;

	@Autowired
	JacksonTester<City> cityTester;

	@Autowired
	JacksonTester<List<City>> listTester;

	@BeforeEach
	void setUp() {
		this.restClient = this.restClientBuilder.baseUrl("http://localhost:" + port)
			.defaultStatusHandler(new DefaultResponseErrorHandler() {
				@Override
				public void handleError(ClientHttpResponse response) {
					// NO-OP
				}
			})
			.requestInterceptor(new RetryableClientHttpRequestInterceptor(new FixedBackOff(100, 2)))
			.build();
	}

	@Test
	@Order(1)
	void getCities() throws Exception {
		ResponseEntity<List<City>> response = this.restClient.get()
			.uri("/cities")
			.retrieve()
			.toEntity(new ParameterizedTypeReference<>() {
			});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(this.listTester.write(response.getBody())).isEqualToJson("""
				[
				  {
				    "id": 1,
				    "name": "Tokyo"
				  },
				  {
				    "id": 2,
				    "name": "Osaka"
				  },
				  {
				    "id": 3,
				    "name": "Kyoto"
				  }
				]
				""");
	}

	@Test
	@Order(2)
	void postCities() throws Exception {
		{
			ResponseEntity<City> response = this.restClient.post().uri("/cities").body("""
						{"name": "Toyama"}
					""").contentType(MediaType.APPLICATION_JSON).retrieve().toEntity(City.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(this.cityTester.write(response.getBody())).isEqualToJson("""
					{
					  "id": 4,
					  "name": "Toyama"
					}
					""");
		}
		{
			ResponseEntity<List<City>> response = this.restClient.get()
				.uri("/cities")
				.retrieve()
				.toEntity(new ParameterizedTypeReference<>() {
				});
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(this.listTester.write(response.getBody())).isEqualToJson("""
					[
					  {
					    "id": 1,
					    "name": "Tokyo"
					  },
					  {
					    "id": 2,
					    "name": "Osaka"
					  },
					  {
					    "id": 3,
					    "name": "Kyoto"
					  },
					  {
					    "id": 4,
					    "name": "Toyama"
					  }
					]
					""");
		}
	}

}