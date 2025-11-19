package com.greatbuild.clearcost.msvc.projects.services;

import com.greatbuild.clearcost.msvc.projects.clients.OrganizationFeignClient;
import com.greatbuild.clearcost.msvc.projects.clients.UserFeignClient;
import com.greatbuild.clearcost.msvc.projects.models.dtos.*;
import com.greatbuild.clearcost.msvc.projects.models.entities.Project;
import com.greatbuild.clearcost.msvc.projects.models.entities.ProjectMember;
import com.greatbuild.clearcost.msvc.projects.models.enums.ProjectRole;
import com.greatbuild.clearcost.msvc.projects.models.enums.ProjectStatus;
import com.greatbuild.clearcost.msvc.projects.models.enums.Specialty;
import com.greatbuild.clearcost.msvc.projects.repositories.ProjectRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository repository;
    private final UserFeignClient userClient;
    private final OrganizationFeignClient organizationClient;

    public ProjectServiceImpl(ProjectRepository repository, UserFeignClient userClient, 
                            OrganizationFeignClient organizationClient) {
        this.repository = repository;
        this.userClient = userClient;
        this.organizationClient = organizationClient;
    }

    @Override
    @Transactional
    public Project createProject(CreateProjectDTO dto, Long creatorUserId) {
        // 1. Validar que la organización existe
        OrganizationDTO organization;
        try {
            organization = organizationClient.getOrganizationById(dto.getOrganizationId());
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("La organización con ID " + dto.getOrganizationId() + " no existe");
        } catch (Exception e) {
            log.error("Error al validar organización {}: {}", dto.getOrganizationId(), e.getMessage());
            throw new IllegalStateException("Error de comunicación con el servicio de organizaciones");
        }

        // 2. Validar que el creador es CONTRACTOR de la organización
        if (!organization.getOwnerId().equals(creatorUserId)) {
            throw new IllegalArgumentException("Solo el CONTRACTOR de la organización puede crear proyectos");
        }

        // 3. Validar que el contractingEntity existe y tiene rol ROLE_CLIENT
        UserDTO contractingEntity;
        try {
            contractingEntity = userClient.getUserByEmail(dto.getContractingEntityEmail());
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("El usuario con email " + dto.getContractingEntityEmail() + " no existe");
        } catch (Exception e) {
            log.error("Error al validar contractingEntity {}: {}", dto.getContractingEntityEmail(), e.getMessage());
            throw new IllegalStateException("Error de comunicación con el servicio de usuarios");
        }

        // TODO: Validar que contractingEntity tiene rol ROLE_CLIENT
        // Necesitaríamos un endpoint en msvc-users que retorne los roles del usuario

        // 4. Crear el proyecto
        Project project = new Project();
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(LocalDate.now());
        project.setEndDate(dto.getEndDate());
        project.setOrganizationId(dto.getOrganizationId());
        project.setContractingEntityId(contractingEntity.getId());
        project.setStatus(ProjectStatus.BASIC_STUDIES);

        // 5. Guardar el proyecto
        Project savedProject = repository.save(project);

        // 6. Agregar al creador como COORDINATOR con specialty NON_APPLICABLE
        ProjectMember coordinator = new ProjectMember();
        coordinator.setUserId(creatorUserId);
        coordinator.setRole(ProjectRole.COORDINATOR);
        coordinator.setSpecialty(Specialty.NON_APPLICABLE);

        savedProject.addMember(coordinator);
        savedProject = repository.save(savedProject);

        log.info("Proyecto {} creado por usuario {} (COORDINATOR)", savedProject.getId(), creatorUserId);

        return savedProject;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Project> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Project updateProject(Long id, CreateProjectDTO dto) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + id));

        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setEndDate(dto.getEndDate());
        // No permitimos cambiar organizationId ni contractingEntityId

        return repository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Proyecto no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Project updateStatus(Long id, ProjectStatus status) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + id));

        project.setStatus(status);
        return repository.save(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getMyProjectsByOrganization(Long organizationId, Long userId) {
        // 1. Obtener la organización para saber quién es el CONTRACTOR
        OrganizationDTO organization;
        try {
            organization = organizationClient.getOrganizationById(organizationId);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("La organización con ID " + organizationId + " no existe");
        } catch (Exception e) {
            log.error("Error al validar organización {}: {}", organizationId, e.getMessage());
            throw new IllegalStateException("Error de comunicación con el servicio de organizaciones");
        }

        // 2. Verificar si el usuario es CONTRACTOR o MEMBER de la organización
        boolean isContractor = organization.getOwnerId().equals(userId);

        List<OrganizationMemberDTO> orgMembers;
        try {
            orgMembers = organizationClient.getOrganizationMembers(organizationId);
        } catch (Exception e) {
            log.error("Error al obtener miembros de organización {}: {}", organizationId, e.getMessage());
            throw new IllegalStateException("Error de comunicación con el servicio de organizaciones");
        }

        boolean isMember = orgMembers.stream()
                .anyMatch(m -> m.getUserId().equals(userId));

        if (!isContractor && !isMember) {
            throw new IllegalArgumentException("No tienes permisos para ver proyectos de esta organización");
        }

        // 3. Si es CONTRACTOR, retornar todos los proyectos de la organización
        if (isContractor) {
            return repository.findByOrganizationId(organizationId);
        }

        // 4. Si es MEMBER, retornar solo proyectos donde es miembro
        return repository.findByOrganizationIdAndUserId(organizationId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getProjectsAsClient(Long userId) {
        return repository.findByContractingEntityId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberResponseDTO> getProjectMembers(Long projectId) {
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + projectId));

        return project.getMembers().stream()
                .map(member -> {
                    try {
                        UserDTO user = userClient.getUserById(member.getUserId());
                        String fullName = user.getFirstName() + " " + user.getLastName();

                        return new ProjectMemberResponseDTO(
                                member.getId(),
                                member.getUserId(),
                                fullName,
                                user.getEmail(),
                                member.getRole(),
                                member.getSpecialty()
                        );
                    } catch (FeignException.NotFound e) {
                        log.warn("Usuario con ID {} no encontrado en msvc-users", member.getUserId());
                        return new ProjectMemberResponseDTO(
                                member.getId(),
                                member.getUserId(),
                                "Usuario no encontrado",
                                "",
                                member.getRole(),
                                member.getSpecialty()
                        );
                    } catch (Exception e) {
                        log.error("Error al obtener usuario {}: {}", member.getUserId(), e.getMessage());
                        return new ProjectMemberResponseDTO(
                                member.getId(),
                                member.getUserId(),
                                "Error al obtener datos",
                                "",
                                member.getRole(),
                                member.getSpecialty()
                        );
                    }
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public void addMember(Long projectId, AddProjectMemberDTO dto) {
        // 1. Verificar que el proyecto existe
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + projectId));

        // 2. Validar reglas de specialty según el rol
        if (dto.getRole() == ProjectRole.COORDINATOR && dto.getSpecialty() != Specialty.NON_APPLICABLE) {
            throw new IllegalArgumentException("El rol COORDINATOR debe tener specialty NON_APPLICABLE");
        }

        if (dto.getRole() == ProjectRole.SPECIALIST && dto.getSpecialty() == Specialty.NON_APPLICABLE) {
            throw new IllegalArgumentException("El rol SPECIALIST debe tener una specialty específica (no NON_APPLICABLE)");
        }

        // 3. Verificar que el usuario es miembro de la organización
        List<OrganizationMemberDTO> orgMembers;
        try {
            orgMembers = organizationClient.getOrganizationMembers(project.getOrganizationId());
        } catch (Exception e) {
            log.error("Error al obtener miembros de organización {}: {}", project.getOrganizationId(), e.getMessage());
            throw new IllegalStateException("Error de comunicación con el servicio de organizaciones");
        }

        boolean isOrgMember = orgMembers.stream()
                .anyMatch(m -> m.getUserId().equals(dto.getUserId()));

        if (!isOrgMember) {
            throw new IllegalArgumentException("El usuario no es miembro de la organización");
        }

        // 4. Verificar que el usuario no es ya miembro del proyecto
        boolean alreadyMember = project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(dto.getUserId()));

        if (alreadyMember) {
            throw new IllegalArgumentException("El usuario ya es miembro del proyecto");
        }

        // 5. Agregar el miembro
        ProjectMember member = new ProjectMember();
        member.setUserId(dto.getUserId());
        member.setRole(dto.getRole());
        member.setSpecialty(dto.getSpecialty());

        project.addMember(member);
        repository.save(project);

        log.info("Usuario {} agregado al proyecto {} con rol {} y specialty {}", 
                dto.getUserId(), projectId, dto.getRole(), dto.getSpecialty());
    }

    @Override
    @Transactional
    public void removeMember(Long projectId, Long memberId) {
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + projectId));

        ProjectMember memberToRemove = project.getMembers().stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado con ID: " + memberId));

        project.removeMember(memberToRemove);
        repository.save(project);

        log.info("Miembro {} eliminado del proyecto {}", memberId, projectId);
    }
}
