package com.example.bachend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@SpringBootApplication
public class BachendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BachendApplication.class, args);
	}
	@GetMapping("/")
	public String getMethodName() {
		return "hello world!";
	}
	

}
