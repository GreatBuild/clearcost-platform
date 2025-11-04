package com.greatbuild.clearcost.msvc.invitations.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private final FeignClientInterceptor feignClientInterceptor;

    public FeignConfig(FeignClientInterceptor feignClientInterceptor) {
        this.feignClientInterceptor = feignClientInterceptor;
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return feignClientInterceptor;
    }
}
