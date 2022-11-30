package com.orange.reactivetranscribeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
//@EnableResourceServer
@EnableWebFluxSecurity
public class ReactiveTranscribeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveTranscribeServiceApplication.class, args);
	}

}
