package com.greatbuild.clearcost.msvc.invitations.services;

import com.greatbuild.clearcost.msvc.invitations.clients.OrganizationFeignClient;
import com.greatbuild.clearcost.msvc.invitations.clients.UserFeignClient;
import com.greatbuild.clearcost.msvc.invitations.models.dtos.*;
import com.greatbuild.clearcost.msvc.invitations.models.entities.Invitation;
import com.greatbuild.clearcost.msvc.invitations.models.entities.InvitationStatus;
import com.greatbuild.clearcost.msvc.invitations.repositories.InvitationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationServiceImpl implements InvitationService {

    private static final Logger log = LoggerFactory.getLogger(InvitationServiceImpl.class);

    private final InvitationRepository repository;
    private final UserFeignClient userFeignClient;
    private final OrganizationFeignClient organizationFeignClient;
    private final RabbitTemplate rabbitTemplate;

    public InvitationServiceImpl(
            InvitationRepository repository,
            UserFeignClient userFeignClient,
            OrganizationFeignClient organizationFeignClient,
            RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.userFeignClient = userFeignClient;
        this.organizationFeignClient = organizationFeignClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public InvitationResponseDTO createInvitation(CreateInvitationDTO dto, Long inviterId) {
        // 1. Verificar que la organización existe
        OrganizationDTO organization;
        try {
            organization = organizationFeignClient.getOrganizationById((dto.getOrganizationId()));
        } catch (Exception e) {
            log.error("Error al obtener organización {}: {} - {}", 
                dto.getOrganizationId(), e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException("La organización no existe");
        }

        // 2. Verificar que el invitador es el creador de la organización
        if (!organization.getCreatorId().equals(inviterId)) {
            throw new RuntimeException("Solo el creador de la organización puede enviar invitaciones");
        }

        // 3. Buscar el usuario invitado por email
        UserDTO invitee;
        try {
            invitee = userFeignClient.getUserByEmail(dto.getInviteeEmail());
            log.info("Usuario encontrado por email {}: ID {}", dto.getInviteeEmail(), invitee.getId());
        } catch (Exception e) {
            log.error("Error al obtener usuario por email {}: {} - {}", 
                dto.getInviteeEmail(), e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException("No se encontró un usuario con el email: " + dto.getInviteeEmail());
        }

        // 4. Validar que el usuario invitado tiene el rol ROLE_WORKER
        if (!invitee.hasRole("ROLE_WORKER")) {
            throw new RuntimeException("Solo se pueden invitar usuarios con rol ROLE_WORKER. El usuario " + dto.getInviteeEmail() + " tiene otro rol.");
        }

        // 5. Verificar que no existe una invitación PENDIENTE previa
        repository.findByOrganizationIdAndInviteeUserIdAndStatus(
                dto.getOrganizationId(),
                invitee.getId(),
                InvitationStatus.PENDING
        ).ifPresent(existing -> {
            throw new RuntimeException("Ya existe una invitación pendiente para este usuario en esta organización");
        });

        // 6. Crear la invitación
        Invitation invitation = new Invitation();
        invitation.setOrganizationId(dto.getOrganizationId());
        invitation.setInviterId(inviterId);
        invitation.setInviteeUserId(invitee.getId()); // Usar el ID del usuario encontrado por email
        invitation.setInviteeEmail(invitee.getEmail()); // Usar el email del usuario encontrado

        invitation = repository.save(invitation);

        // 7. Publicar evento en RabbitMQ
        publishInvitationCreatedEvent(invitation);

        return mapToResponseDTO(invitation, organization, invitee);
    }

    @Override
    @Transactional
    public void createInvitation(Invitation invitation) {
        // Verificar que no exista ya una invitación PENDIENTE
        repository.findByOrganizationIdAndInviteeUserIdAndStatus(
                invitation.getOrganizationId(),
                invitation.getInviteeUserId(),
                InvitationStatus.PENDING
        ).ifPresent(existing -> {
            throw new RuntimeException("Ya existe una invitación pendiente");
        });

        repository.save(invitation);
    }

    @Override
    @Transactional
    public InvitationResponseDTO acceptInvitation(Long invitationId, Long userId) {
        Invitation invitation = repository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitación no encontrada"));

        // Verificar que la invitación es para este usuario
        if (!invitation.getInviteeUserId().equals(userId)) {
            throw new RuntimeException("Esta invitación no es para ti");
        }

        // Verificar que está en estado PENDING
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Esta invitación ya ha sido procesada");
        }

        // Verificar que no ha expirado
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            repository.save(invitation);
            throw new RuntimeException("Esta invitación ha expirado");
        }

        // Cambiar estado a ACCEPTED
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation = repository.save(invitation);

        // Agregar el usuario a la organización con rol WORKER
        AddMemberDTO addMemberDTO = new AddMemberDTO();
        addMemberDTO.setUserId(userId);
        addMemberDTO.setOrganizationId(invitation.getOrganizationId());
        addMemberDTO.setRole("WORKER");

        try {
            organizationFeignClient.addMember(invitation.getOrganizationId(), addMemberDTO);
        } catch (Exception e) {
            log.error("Error al agregar miembro a la organización: {}", e.getMessage());
            throw new RuntimeException("Error al agregar el usuario a la organización");
        }

        // Publicar evento de invitación aceptada
        publishInvitationAcceptedEvent(invitation);

        return mapToResponseDTO(invitation);
    }

    @Override
    @Transactional
    public InvitationResponseDTO rejectInvitation(Long invitationId, Long userId) {
        Invitation invitation = repository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitación no encontrada"));

        // Verificar que la invitación es para este usuario
        if (!invitation.getInviteeUserId().equals(userId)) {
            throw new RuntimeException("Esta invitación no es para ti");
        }

        // Verificar que está en estado PENDING
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Esta invitación ya ha sido procesada");
        }

        // Cambiar estado a REJECTED
        invitation.setStatus(InvitationStatus.REJECTED);
        invitation = repository.save(invitation);

        // Publicar evento de invitación rechazada
        publishInvitationRejectedEvent(invitation);

        return mapToResponseDTO(invitation);
    }

    @Override
    public List<InvitationResponseDTO> getUserInvitations(Long userId) {
        List<Invitation> invitations = repository.findByInviteeUserId(userId);
        return invitations.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvitationResponseDTO> getPendingInvitations(Long userId) {
        List<Invitation> invitations = repository.findByInviteeUserIdAndStatus(userId, InvitationStatus.PENDING);
        return invitations.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Métodos auxiliares para publicar eventos
    private void publishInvitationCreatedEvent(Invitation invitation) {
        try {
            InvitationEventDTO event = new InvitationEventDTO(
                invitation.getId(),
                invitation.getOrganizationId(),
                invitation.getInviterId(),
                invitation.getInviteeUserId(),
                invitation.getInviteeEmail(),
                invitation.getStatus(),
                invitation.getCreatedAt(),
                invitation.getExpiresAt()
            );
            rabbitTemplate.convertAndSend("invitation.exchange", "invitation.created", event);
        } catch (Exception e) {
            log.error("Error al publicar evento de invitación creada: {}", e.getMessage());
        }
    }

    private void publishInvitationAcceptedEvent(Invitation invitation) {
        try {
            InvitationEventDTO event = new InvitationEventDTO(
                invitation.getId(),
                invitation.getOrganizationId(),
                invitation.getInviterId(),
                invitation.getInviteeUserId(),
                invitation.getInviteeEmail(),
                invitation.getStatus(),
                invitation.getCreatedAt(),
                invitation.getExpiresAt()
            );
            rabbitTemplate.convertAndSend("invitation.exchange", "invitation.accepted", event);
        } catch (Exception e) {
            log.error("Error al publicar evento de invitación aceptada: {}", e.getMessage());
        }
    }

    private void publishInvitationRejectedEvent(Invitation invitation) {
        try {
            InvitationEventDTO event = new InvitationEventDTO(
                invitation.getId(),
                invitation.getOrganizationId(),
                invitation.getInviterId(),
                invitation.getInviteeUserId(),
                invitation.getInviteeEmail(),
                invitation.getStatus(),
                invitation.getCreatedAt(),
                invitation.getExpiresAt()
            );
            rabbitTemplate.convertAndSend("invitation.exchange", "invitation.rejected", event);
        } catch (Exception e) {
            log.error("Error al publicar evento de invitación rechazada: {}", e.getMessage());
        }
    }

    // Métodos auxiliares para mapear entidades a DTOs
    private InvitationResponseDTO mapToResponseDTO(Invitation invitation) {
        InvitationResponseDTO dto = new InvitationResponseDTO();
        dto.setId(invitation.getId());
        dto.setOrganizationId(invitation.getOrganizationId());
        dto.setInviterId(invitation.getInviterId());
        dto.setInviteeUserId(invitation.getInviteeUserId());
        dto.setInviteeEmail(invitation.getInviteeEmail());
        dto.setStatus(invitation.getStatus());
        dto.setCreatedAt(invitation.getCreatedAt());
        dto.setExpiresAt(invitation.getExpiresAt());

        // Intentar obtener nombres de organización y usuario
        try {
            OrganizationDTO org = organizationFeignClient.getOrganizationById(invitation.getOrganizationId());
            dto.setOrganizationName(org.getName());
        } catch (Exception e) {
            log.warn("No se pudo obtener el nombre de la organización");
        }

        try {
            UserDTO inviter = userFeignClient.getUserById(invitation.getInviterId());
            dto.setInviterName(inviter.getFullName());
        } catch (Exception e) {
            log.warn("No se pudo obtener el nombre del invitador");
        }

        return dto;
    }

    private InvitationResponseDTO mapToResponseDTO(Invitation invitation, OrganizationDTO organization, UserDTO invitee) {
        InvitationResponseDTO dto = mapToResponseDTO(invitation);
        dto.setOrganizationName(organization.getName());
        return dto;
    }
}
