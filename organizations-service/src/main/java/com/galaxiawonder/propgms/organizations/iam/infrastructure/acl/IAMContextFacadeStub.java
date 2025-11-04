// Paquete de nuestra implementación "falsa"
package com.galaxiawonder.propgms.organizations.iam.infrastructure.acl;


import com.galaxiawonder.propgms.organizations.iam.interfaces.acl.IAMContextFacade;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.EmailAddress;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.PersonName;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;
import org.springframework.stereotype.Service;

/**
 * Implementación "Falsa" (Stub) de la fachada de IAM.
 * * Esta clase simula la respuesta del microservicio de IAM, devolviendo
 * datos de prueba fijos. Esto nos permite desarrollar nuestro microservicio
 * de Organizaciones de forma independiente.
 * * La anotación @Service(name = "iamContextFacade") asegura que Spring
 * use esta clase cuando otra pida @Autowired IAMContextFacade.
 */
@Service(value = "iamContextFacade") // Registramos este Stub con el nombre de la interfaz
public class IAMContextFacadeStub implements IAMContextFacade {

    /**
     * Simula la obtención de detalles de perfil, devolviendo siempre un usuario de prueba.
     */
    @Override
    public ProfileDetails getProfileDetailsById(Long id) {
        return createFakeProfileDetails();
    }

    /**
     * Simula la obtención de un ID a partir de un email, devolviendo siempre 1.
     */
    @Override
    public Long getPersonIdFromEmail(String email) {
        return 1L; // Devuelve un ID de persona falso
    }

    /**
     * Simula la obtención de detalles de perfil, devolviendo siempre un usuario de prueba.
     */
    @Override
    public ProfileDetails getProfileDetailsByPersonId(Long personId) {
        return createFakeProfileDetails();
    }

    /**
     * Simula la obtención de detalles de perfil, devolviendo siempre un usuario de prueba.
     */
    @Override
    public ProfileDetails getProfileDetailsByEmail(String email) {
        return createFakeProfileDetails();
    }

    /**
     * Helper para crear un perfil de prueba consistente.
     */
    private ProfileDetails createFakeProfileDetails() {
        // Usamos los Value Objects que ya migramos
        var fakeName = new PersonName("Usuario", "de Prueba (Stub)");
        var fakeEmail = new EmailAddress("test@example.com");
        return new ProfileDetails(fakeName, fakeEmail);
    }
}