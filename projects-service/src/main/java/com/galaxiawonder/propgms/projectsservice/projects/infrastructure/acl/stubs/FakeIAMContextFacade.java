package com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl.stubs;

import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl.IAMContextFacade;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.EmailAddress;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.PersonName;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.ProfileDetails;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * FakeIAMContextFacade
 *
 * @summary
 * Stub implementation of the IAMContextFacade interface.
 * This implementation is only active in non-production environments (e.g., dev, test).
 * It returns hardcoded data to simulate the responses from the IAM microservice,
 * allowing the 'projects-service' to run in isolation.
 */
@Service
@Profile("!production") // ¡MUY IMPORTANTE! Solo se activa si el perfil NO es 'production'.
public class FakeIAMContextFacade implements IAMContextFacade {

    /**
     * Simula la obtención de un ID de persona desde un email.
     * @return Siempre devuelve 1L.
     */
    @Override
    public Long getPersonIdFromEmail(String email) {
        return 1L; // Devuelve un ID de persona falso
    }

    /**
     * Simula la obtención de detalles de perfil.
     * @return Siempre devuelve un perfil falso ("Fake User").
     */
    @Override
    public ProfileDetails getProfileDetailsById(Long id) {
        return new ProfileDetails(
                new PersonName("Fake", "User"),
                new EmailAddress("fake.user@galaxiawonder.com")
        );
    }

    // Simula los otros métodos
    @Override
    public ProfileDetails getProfileDetailsByPersonId(Long personId) {
        return getProfileDetailsById(personId);
    }

    @Override
    public ProfileDetails getProfileDetailsByEmail(String email) {
        return getProfileDetailsById(1L);
    }

    /**
     * Simula la obtención de la especialidad de una persona (corregido a String).
     * @return Siempre devuelve "ARCHITECTURE".
     */
    @Override
    public String getSpecialtyFromPersonId(Long id) {
        return "ARCHITECTURE"; // Devuelve un String de especialidad falso
    }
}