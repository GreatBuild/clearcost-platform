package com.greatbuild.clearcost.msvc.msvcchange.clients;

import com.greatbuild.clearcost.msvc.msvcchange.models.dtos.ProjectDTO;
import com.greatbuild.clearcost.msvc.msvcchange.models.dtos.ProjectMemberDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Cliente Feign para comunicarse con msvc-projects
 */
@FeignClient(name = "msvc-projects")
public interface ProjectFeignClient {

    /**
     * Obtiene información de un proyecto por su ID
     * Usa endpoint interno sin autenticación
     */
    @GetMapping("/api/projects/internal/{id}")
    ProjectDTO getProjectById(@PathVariable("id") Long id);

    /**
     * Obtiene la lista de miembros de un proyecto
     * Usa endpoint interno sin autenticación
     */
    @GetMapping("/api/projects/internal/{id}/members")
    List<ProjectMemberDTO> getProjectMembers(@PathVariable("id") Long id);
}
