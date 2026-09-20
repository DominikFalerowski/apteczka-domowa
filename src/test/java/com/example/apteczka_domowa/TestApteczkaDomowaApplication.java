package com.example.apteczka_domowa;

import org.springframework.boot.SpringApplication;

public class TestApteczkaDomowaApplication {

	public static void main(String[] args) {
		SpringApplication.from(ApteczkaDomowaApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
