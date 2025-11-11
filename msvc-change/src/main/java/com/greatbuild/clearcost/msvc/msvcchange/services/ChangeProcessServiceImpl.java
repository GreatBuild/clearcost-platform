package com.greatbuild.clearcost.msvc.msvcchange.services;

import com.greatbuild.clearcost.msvc.msvcchange.clients.OrganizationFeignClient;
import com.greatbuild.clearcost.msvc.msvcchange.clients.ProjectFeignClient;
import com.greatbuild.clearcost.msvc.msvcchange.models.dtos.*;
import com.greatbuild.clearcost.msvc.msvcchange.models.entities.ChangeProcess;
import com.greatbuild.clearcost.msvc.msvcchange.models.enums.ChangeProcessOrigin;
import com.greatbuild.clearcost.msvc.msvcchange.models.enums.ChangeProcessStatus;
import com.greatbuild.clearcost.msvc.msvcchange.repositories.ChangeProcessRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de solicitudes de cambio
 */
@Service
public class ChangeProcessServiceImpl implements ChangeProcessService {

    private static final Logger log = LoggerFactory.getLogger(ChangeProcessServiceImpl.class);

    private final ChangeProcessRepository repository;
    private final ProjectFeignClient projectClient;
    private final OrganizationFeignClient organizationClient;

    public ChangeProcessServiceImpl(
            ChangeProcessRepository repository,
            ProjectFeignClient projectClient,
            OrganizationFeignClient organizationClient) {
        this.repository = repository;
        this.projectClient = projectClient;
        this.organizationClient = organizationClient;
    }

    @Override
    @Transactional
    public ChangeProcess createChangeProcess(CreateChangeProcessDTO dto, Long userId) {
        log.info("Creando solicitud de cambio para proyecto {} por usuario {}", dto.getProjectId(), userId);

        // 1. Validar que el proyecto existe
        ProjectDTO project = getProjectOrThrow(dto.getProjectId());

        // 2. Validar que el proyecto NO esté en status APPROVED
        if ("APPROVED".equalsIgnoreCase(project.getStatus())) {
            throw new IllegalArgumentException("No se pueden solicitar cambios en proyectos aprobados");
        }

        // 3. Validar que el usuario sea el contractingEntity del proyecto
        if (!project.getContractingEntityId().equals(userId)) {
            throw new IllegalArgumentException("Solo el contractingEntity puede solicitar cambios");
        }

        // 4. Crear la solicitud de cambio
        ChangeProcess changeProcess = new ChangeProcess();
        changeProcess.setProjectId(dto.getProjectId());
        changeProcess.setCreatedBy(userId);
        changeProcess.setOrigin(ChangeProcessOrigin.CHANGE_REQUEST.getValue());
        changeProcess.setStatusId(ChangeProcessStatus.PENDING.getId());
        changeProcess.setJustification(dto.getJustification());
        changeProcess.setResponse(null);  // No hay respuesta aún
        changeProcess.setCreatedAt(LocalDate.now());
        changeProcess.setUpdatedAt(null);  // No ha sido actualizada aún

        ChangeProcess saved = repository.save(changeProcess);
        log.info("Solicitud de cambio creada con ID: {}", saved.getId());

        return saved;
    }

    @Override
    @Transactional
    public ChangeProcess updateChangeProcess(Long changeProcessId, UpdateChangeProcessDTO dto, Long userId) {
        log.info("Actualizando solicitud de cambio {} por usuario {}", changeProcessId, userId);

        // 1. Validar que la solicitud existe
        ChangeProcess changeProcess = repository.findById(changeProcessId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de cambio no encontrada"));

        // 2. Validar que la solicitud esté en status PENDING
        if (!changeProcess.getStatusId().equals(ChangeProcessStatus.PENDING.getId())) {
            throw new IllegalArgumentException("Solo se pueden actualizar solicitudes en estado PENDING");
        }

        // 3. Obtener el proyecto
        ProjectDTO project = getProjectOrThrow(changeProcess.getProjectId());

        // 4. Validar que el usuario sea COORDINATOR del proyecto
        if (!isUserCoordinator(changeProcess.getProjectId(), userId)) {
            throw new IllegalArgumentException("Solo el COORDINATOR puede responder solicitudes de cambio");
        }

        // 5. Validar el nuevo status
        ChangeProcessStatus newStatus;
        try {
            newStatus = ChangeProcessStatus.fromName(dto.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + dto.getStatus() + ". Debe ser APPROVED o REJECTED");
        }

        // 6. Validar que el nuevo status no sea PENDING
        if (newStatus == ChangeProcessStatus.PENDING) {
            throw new IllegalArgumentException("No se puede cambiar a status PENDING");
        }

        // 7. Actualizar la solicitud
        changeProcess.setStatusId(newStatus.getId());
        changeProcess.setResponse(dto.getResponse());
        changeProcess.setUpdatedAt(LocalDate.now());

        ChangeProcess updated = repository.save(changeProcess);
        log.info("Solicitud de cambio {} actualizada a status {}", changeProcessId, newStatus.getName());

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChangeProcess> getChangeProcessesByProject(Long projectId, Long userId) {
        log.info("Obteniendo solicitudes de cambio para proyecto {} por usuario {}", projectId, userId);

        // 1. Validar que el proyecto existe
        ProjectDTO project = getProjectOrThrow(projectId);

        // 2. Validar permisos: CONTRACTOR, COORDINATOR o contractingEntity
        boolean isContractingEntity = project.getContractingEntityId().equals(userId);
        boolean isCoordinator = isUserCoordinator(projectId, userId);
        boolean isContractor = isUserContractor(project.getOrganizationId(), userId);

        if (!isContractingEntity && !isCoordinator && !isContractor) {
            throw new IllegalArgumentException("No tiene permisos para ver las solicitudes de cambio de este proyecto");
        }

        // 3. Obtener las solicitudes
        List<ChangeProcess> changeProcesses = repository.findByProjectId(projectId);
        log.info("Se encontraron {} solicitudes de cambio para el proyecto {}", changeProcesses.size(), projectId);

        return changeProcesses;
    }

    /**
     * Obtiene un proyecto o lanza excepción si no existe
     */
    private ProjectDTO getProjectOrThrow(Long projectId) {
        try {
            ProjectDTO project = projectClient.getProjectById(projectId);
            if (project == null) {
                throw new IllegalArgumentException("Proyecto no encontrado: " + projectId);
            }
            return project;
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Proyecto no encontrado: " + projectId);
        } catch (FeignException e) {
            log.error("Error al comunicarse con msvc-projects: {}", e.getMessage());
            throw new IllegalStateException("Error de comunicación con el servicio de proyectos");
        }
    }

    /**
     * Valida si el usuario es COORDINATOR del proyecto
     */
    private boolean isUserCoordinator(Long projectId, Long userId) {
        try {
            List<ProjectMemberDTO> members = projectClient.getProjectMembers(projectId);
            return members.stream()
                    .anyMatch(m -> m.getUserId().equals(userId) && "COORDINATOR".equalsIgnoreCase(m.getRole()));
        } catch (FeignException e) {
            log.error("Error al obtener miembros del proyecto: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida si el usuario es CONTRACTOR de la organización
     */
    private boolean isUserContractor(Long organizationId, Long userId) {
        try {
            List<OrganizationMemberDTO> members = organizationClient.getOrganizationMembers(organizationId);
            return members.stream()
                    .anyMatch(m -> m.getUserId().equals(userId) && "CONTRACTOR".equalsIgnoreCase(m.getRole()));
        } catch (FeignException e) {
            log.error("Error al obtener miembros de la organización: {}", e.getMessage());
            return false;
        }
    }
}
