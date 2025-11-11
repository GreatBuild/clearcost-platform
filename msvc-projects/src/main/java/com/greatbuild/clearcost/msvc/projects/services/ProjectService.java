package com.greatbuild.clearcost.msvc.projects.services;

import com.greatbuild.clearcost.msvc.projects.models.dtos.AddProjectMemberDTO;
import com.greatbuild.clearcost.msvc.projects.models.dtos.CreateProjectDTO;
import com.greatbuild.clearcost.msvc.projects.models.dtos.ProjectMemberResponseDTO;
import com.greatbuild.clearcost.msvc.projects.models.entities.Project;
import com.greatbuild.clearcost.msvc.projects.models.enums.ProjectStatus;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    
    /**
     * Crea un nuevo proyecto
     * Valida organización, contractingEntity y agrega al creador como COORDINATOR
     */
    Project createProject(CreateProjectDTO dto, Long creatorUserId);
    
    /**
     * Obtiene un proyecto por ID
     */
    Optional<Project> findById(Long id);
    
    /**
     * Actualiza un proyecto
     */
    Project updateProject(Long id, CreateProjectDTO dto);
    
    /**
     * Elimina un proyecto
     */
    void deleteProject(Long id);
    
    /**
     * Actualiza el status de un proyecto
     */
    Project updateStatus(Long id, ProjectStatus status);
    
    /**
     * Obtiene proyectos de una organización según el rol del usuario
     * - Si es CONTRACTOR: retorna todos los proyectos de la organización
     * - Si es MEMBER: retorna solo los proyectos donde es miembro
     */
    List<Project> getMyProjectsByOrganization(Long organizationId, Long userId);
    
    /**
     * Obtiene proyectos donde el usuario es contractingEntity (cliente)
     */
    List<Project> getProjectsAsClient(Long userId);
    
    /**
     * Obtiene los miembros de un proyecto con sus datos de usuario
     */
    List<ProjectMemberResponseDTO> getProjectMembers(Long projectId);
    
    /**
     * Agrega un miembro al proyecto
     * Valida que sea miembro de la organización y las reglas de specialty
     */
    void addMember(Long projectId, AddProjectMemberDTO dto);
    
    /**
     * Elimina un miembro del proyecto
     */
    void removeMember(Long projectId, Long memberId);
}
