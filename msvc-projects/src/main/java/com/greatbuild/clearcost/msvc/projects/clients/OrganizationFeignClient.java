package com.greatbuild.clearcost.msvc.projects.clients;

import com.greatbuild.clearcost.msvc.projects.models.dtos.OrganizationDTO;
import com.greatbuild.clearcost.msvc.projects.models.dtos.OrganizationMemberDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Cliente Feign para comunicarse con msvc-organizations
 */
@FeignClient(name = "msvc-organizations")
public interface OrganizationFeignClient {

    /**
     * Obtiene información de una organización por su ID
     * Usa endpoint interno sin autenticación
     */
    @GetMapping("/api/organizations/internal/{id}")
    OrganizationDTO getOrganizationById(@PathVariable("id") Long id);

    /**
     * Obtiene la lista de miembros de una organización
     * Usa endpoint interno sin autenticación
     */
    @GetMapping("/api/organizations/internal/{id}/members")
    List<OrganizationMemberDTO> getOrganizationMembers(@PathVariable("id") Long id);
}
