package com.galaxiawonder.propgms.organizations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // Importar la anotación

@SpringBootApplication
@EnableJpaAuditing // Habilita la auditoría de JPA (@CreatedDate, @LastModifiedDate)
public class OrganizationsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizationsServiceApplication.class, args);
    }

}