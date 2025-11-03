package com.greatbuild.clearcost.msvc.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Esta es la "fábrica" oficial de PasswordEncoder
        return new BCryptPasswordEncoder();
    }
}
