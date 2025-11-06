package com.greatbuild.clearcost.msvc.organizations.services;

import com.greatbuild.clearcost.msvc.organizations.clients.UserFeignClient;
import com.greatbuild.clearcost.msvc.organizations.models.entities.Organization;
import com.greatbuild.clearcost.msvc.organizations.models.entities.OrganizationMember;
import com.greatbuild.clearcost.msvc.organizations.repositories.OrganizationRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger log = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationRepository repository;
    private final UserFeignClient userClient; // ¡Inyectamos el mensajero!

    // ¡Inyección por constructor de AMBAS dependencias!
    public OrganizationServiceImpl(OrganizationRepository repository, UserFeignClient userClient) {
        this.repository = repository;
        this.userClient = userClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Organization save(Organization organization) {
        // --- ¡¡LÓGICA DE MICROSERVICIOS!! ---
        // Antes de guardar la organización, validamos que su "ownerId"
        // sea un usuario real en msvc-users.

        if (organization.getOwnerId() == null || organization.getOwnerId() <= 0) {
            throw new IllegalArgumentException("La organización debe tener un 'ownerId' (dueño) válido.");
        }

        try {
            userClient.getUserById(organization.getOwnerId());
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("El 'ownerId' " + organization.getOwnerId() + " no existe.");
        } catch (Exception e) {
            log.error("Error al validar Owner ID {}: {}", organization.getOwnerId(), e.getMessage());
            throw new IllegalStateException("Error de comunicación con el servicio de usuarios.");
        }

        // Verificar si es una nueva organización (no tiene ID)
        boolean isNewOrganization = organization.getId() == null;
        
        // Guardar la organización
        Organization savedOrg = repository.save(organization);
        
        // Si es nueva, agregar al creador como miembro con rol CONTRACTOR
        if (isNewOrganization) {
            OrganizationMember contractorMember = new OrganizationMember();
            contractorMember.setUserId(savedOrg.getOwnerId());
            contractorMember.setRole(com.greatbuild.clearcost.msvc.organizations.models.enums.OrganizationRole.CONTRACTOR);
            
            savedOrg.addMember(contractorMember);
            savedOrg = repository.save(savedOrg);
            
            log.info("Usuario {} agregado automáticamente como CONTRACTOR de la organización {}", 
                    savedOrg.getOwnerId(), savedOrg.getId());
        }
        
        return savedOrg;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMember> getMembers(Long organizationId) {
        Optional<Organization> org = repository.findById(organizationId);
        return org.map(Organization::getMembers).orElse(Collections.emptyList());
    }

    @Override
    @Transactional
    public void removeMember(Long organizationId, Long memberId, Long requestingUserId) {
        Optional<Organization> orgOpt = repository.findById(organizationId);
        if (orgOpt.isEmpty()) {
            throw new IllegalArgumentException("Organización no encontrada con ID: " + organizationId);
        }
        
        Organization org = orgOpt.get();
        
        // Verificar que el usuario solicitante es el CONTRACTOR (owner)
        if (!org.getOwnerId().equals(requestingUserId)) {
            throw new IllegalArgumentException("Solo el CONTRACTOR puede eliminar miembros de la organización");
        }
        
        OrganizationMember memberToRemove = org.getMembers().stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado con ID: " + memberId));
        
        // Verificar que no se esté intentando eliminar al owner
        if (memberToRemove.getUserId().equals(org.getOwnerId())) {
            throw new IllegalArgumentException("No se puede eliminar al CONTRACTOR de la organización");
        }
        
        org.removeMember(memberToRemove);
        repository.save(org);
    }

    @Override
    @Transactional
    public OrganizationMember addMember(com.greatbuild.clearcost.msvc.organizations.models.dtos.AddMemberDTO dto) {
        // Verificar que la organización existe
        Organization org = repository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organización no encontrada con ID: " + dto.getOrganizationId()));

        // Verificar que el usuario existe en msvc-users
        try {
            userClient.getUserById(dto.getUserId());
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Usuario con ID " + dto.getUserId() + " no existe.");
        } catch (Exception e) {
            log.error("Error al validar usuario {}: {}", dto.getUserId(), e.getMessage());
            throw new IllegalStateException("Error de comunicación con el servicio de usuarios.");
        }

        // Verificar que el usuario no es ya miembro
        boolean alreadyMember = org.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(dto.getUserId()));
        
        if (alreadyMember) {
            throw new IllegalArgumentException("El usuario ya es miembro de la organización.");
        }

        // Crear el nuevo miembro con rol MEMBER por defecto
        OrganizationMember member = new OrganizationMember();
        member.setUserId(dto.getUserId());
        member.setRole(com.greatbuild.clearcost.msvc.organizations.models.enums.OrganizationRole.MEMBER);

        org.addMember(member);
        repository.save(org);
        
        return member;
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.greatbuild.clearcost.msvc.organizations.models.dtos.UserOrganizationResponseDTO> getUserOrganizations(Long userId) {
        List<Organization> organizations = repository.findByOwnerIdOrMemberUserId(userId);
        
        return organizations.stream()
                .map(org -> {
                    // Determinar el rol del usuario en esta organización
                    String userRole;
                    if (org.getOwnerId().equals(userId)) {
                        userRole = "CONTRACTOR";
                    } else {
                        // Buscar el rol en los miembros
                        userRole = org.getMembers().stream()
                                .filter(m -> m.getUserId().equals(userId))
                                .findFirst()
                                .map(m -> m.getRole().name())
                                .orElse("MEMBER"); // Por defecto MEMBER si no se encuentra
                    }
                    
                    return new com.greatbuild.clearcost.msvc.organizations.models.dtos.UserOrganizationResponseDTO(
                            org.getId(),
                            org.getLegalName(),
                            org.getCommercialName(),
                            org.getRuc(),
                            org.getOwnerId(),
                            org.getCreatedAt(),
                            org.getMembers() != null ? org.getMembers().size() : 0,
                            userRole
                    );
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.greatbuild.clearcost.msvc.organizations.models.dtos.MemberResponseDTO> getMembersWithUserData(Long organizationId) {
        // Verificar que la organización existe
        Organization org = repository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organización no encontrada con ID: " + organizationId));

        // Obtener todos los miembros (ahora incluye al CONTRACTOR)
        List<OrganizationMember> members = org.getMembers();
        
        // Ordenar para que CONTRACTOR aparezca primero
        return members.stream()
                .sorted((m1, m2) -> {
                    // CONTRACTOR primero
                    if (m1.getRole() == com.greatbuild.clearcost.msvc.organizations.models.enums.OrganizationRole.CONTRACTOR 
                            && m2.getRole() != com.greatbuild.clearcost.msvc.organizations.models.enums.OrganizationRole.CONTRACTOR) return -1;
                    if (m1.getRole() != com.greatbuild.clearcost.msvc.organizations.models.enums.OrganizationRole.CONTRACTOR 
                            && m2.getRole() == com.greatbuild.clearcost.msvc.organizations.models.enums.OrganizationRole.CONTRACTOR) return 1;
                    return 0;
                })
                .map(member -> {
                    try {
                        // Llamar a msvc-users para obtener datos del usuario
                        com.greatbuild.clearcost.msvc.organizations.models.dtos.UserDTO user = userClient.getUserById(member.getUserId());
                        
                        return new com.greatbuild.clearcost.msvc.organizations.models.dtos.MemberResponseDTO(
                                member.getId(),
                                member.getUserId(),
                                member.getRole().name(), // Convertir enum a String
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName()
                        );
                    } catch (FeignException.NotFound e) {
                        log.warn("Usuario con ID {} no encontrado en msvc-users", member.getUserId());
                        // Retornar DTO con datos básicos si el usuario no existe
                        return new com.greatbuild.clearcost.msvc.organizations.models.dtos.MemberResponseDTO(
                                member.getId(),
                                member.getUserId(),
                                member.getRole().name(), // Convertir enum a String
                                "Usuario no encontrado",
                                "",
                                ""
                        );
                    } catch (Exception e) {
                        log.error("Error al obtener usuario {}: {}", member.getUserId(), e.getMessage());
                        // En caso de error de comunicación, retornar datos básicos
                        return new com.greatbuild.clearcost.msvc.organizations.models.dtos.MemberResponseDTO(
                                member.getId(),
                                member.getUserId(),
                                member.getRole().name(), // Convertir enum a String
                                "Error al obtener datos",
                                "",
                                ""
                        );
                    }
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
