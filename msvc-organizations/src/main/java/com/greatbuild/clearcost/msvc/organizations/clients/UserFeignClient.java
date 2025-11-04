package com.greatbuild.clearcost.msvc.organizations.clients;

import com.greatbuild.clearcost.msvc.organizations.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para comunicarse con msvc-users
 * 
 * Cuando Eureka está deshabilitado:
 * - Usar 'url' con la URL completa del servicio
 * - 'name' es solo un identificador
 * 
 * Cuando Eureka está habilitado:
 * - Cambiar a: @FeignClient(name = "msvc-users")
 * - Remover el 'url'
 */
@FeignClient(
    name = "msvc-users",
    url = "${msvc-users.url:http://localhost:8003}"
)
public interface UserFeignClient {

    /**
     * Obtiene información de un usuario por su ID
     * Endpoint en msvc-users: GET /api/users/{id}
     */
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

}
