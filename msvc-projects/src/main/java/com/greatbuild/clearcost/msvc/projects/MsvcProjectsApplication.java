package com.greatbuild.clearcost.msvc.projects;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcProjectsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsvcProjectsApplication.class, args);
    }

}
