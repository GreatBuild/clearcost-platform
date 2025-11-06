package com.greatbuild.clearcost.msvc.invitations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.greatbuild.clearcost.msvc.invitations.clients")
@EnableConfigurationProperties
@EnableDiscoveryClient
public class MsvcInvitationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsvcInvitationsApplication.class, args);
    }

}
