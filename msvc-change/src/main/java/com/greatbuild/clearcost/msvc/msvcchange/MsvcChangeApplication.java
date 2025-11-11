package com.greatbuild.clearcost.msvc.msvcchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsvcChangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcChangeApplication.class, args);
	}

}
