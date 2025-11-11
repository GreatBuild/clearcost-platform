package com.greatbuild.clearcost.msvc.projects.clients;

import com.greatbuild.clearcost.msvc.projects.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para comunicarse con msvc-users
 */
@FeignClient(name = "msvc-users")
public interface UserFeignClient {

    /**
     * Obtiene información de un usuario por su ID
     * Endpoint INTERNO en msvc-users: GET /api/users/internal/{id}
     */
    @GetMapping("/api/users/internal/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}
