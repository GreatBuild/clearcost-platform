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
            // Hacemos la llamada HTTP síncrona al otro servicio
            log.info("Validando Owner ID: {} en msvc-users", organization.getOwnerId());
            // (userClient.getUserById() devolverá un UserDTO o lanzará una excepción)
            userClient.getUserById(organization.getOwnerId());
            log.info("Owner ID validado exitosamente.");

        } catch (FeignException.NotFound e) {
            // ¡El msvc-users nos dijo que el usuario NO existe!
            log.warn("Validación fallida. Usuario con ID {} no encontrado en msvc-users.", organization.getOwnerId());
            throw new IllegalArgumentException("El 'ownerId' " + organization.getOwnerId() + " no existe.");
        } catch (Exception e) {
            // ¡El msvc-users está caído o hay un error de red!
            log.error("Error al validar Owner ID {}: {}", organization.getOwnerId(), e.getMessage());
            throw new IllegalStateException("Error de comunicación con el servicio de usuarios.");
        }

        return repository.save(organization);
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
    public void removeMember(Long organizationId, Long memberId) {
        Optional<Organization> orgOpt = repository.findById(organizationId);
        if (orgOpt.isEmpty()) {
            throw new IllegalArgumentException("Organización no encontrada con ID: " + organizationId);
        }
        
        Organization org = orgOpt.get();
        OrganizationMember memberToRemove = org.getMembers().stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado con ID: " + memberId));
        
        org.removeMember(memberToRemove);
        repository.save(org);
        log.info("Miembro {} removido de la organización {}", memberId, organizationId);
    }
}
