package com.greatbuild.clearcost.msvc.invitations.clients;

import com.greatbuild.clearcost.msvc.invitations.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-users", url = "${msvc.users.url:http://localhost:8003}")
public interface UserFeignClient {

    /**
     * Obtiene un usuario por ID usando el endpoint INTERNO (sin autenticación JWT)
     * Endpoint: /api/users/internal/{id}
     */
    @GetMapping("/api/users/internal/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    /**
     * Obtiene un usuario por email usando el endpoint INTERNO (sin autenticación JWT)
     * Endpoint: /api/users/internal/email/{email}
     */
    @GetMapping("/api/users/internal/email/{email}")
    UserDTO getUserByEmail(@PathVariable("email") String email);
}
