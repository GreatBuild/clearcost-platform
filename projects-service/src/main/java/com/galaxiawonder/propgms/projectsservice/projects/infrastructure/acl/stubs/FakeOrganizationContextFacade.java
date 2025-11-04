package com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl.stubs;

import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl.OrganizationContextFacade;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * FakeOrganizationContextFacade
 *
 * @summary
 * Stub implementation of the OrganizationContextFacade interface.
 * This implementation is only active in non-production environments.
 * It returns hardcoded IDs to simulate responses from the Organizations microservice.
 */
@Service
@Profile("!production") // ¡MUY IMPORTANTE!
public class FakeOrganizationContextFacade implements OrganizationContextFacade {

    /**
     * Simula la obtención del ID del contratista de una organización.
     * @return Siempre devuelve 1L (PersonId).
     */
    @Override
    public Long getContractorIdFromOrganizationId(Long organizationId) {
        return 1L; // Devuelve un ID de Persona (Contratista) falso
    }

    /**
     * Simula la obtención de un ID de Miembro de Organización.
     * @return Siempre devuelve 100L.
     */
    @Override
    public Long getOrganizationMemberIdFromPersonAndOrganizationId(Long personId, Long organizationId) {
        return 100L; // Devuelve un ID de Miembro de Organización falso
    }

    /**
     * Simula la obtención de un ID de Persona desde un ID de Miembro.
     * @return Siempre devuelve 1L.
     */
    @Override
    public Long getPersonIdFromOrganizationMemberId(Long organizationMemberId) {
        return 1L; // Devuelve un ID de Persona falso
    }
}