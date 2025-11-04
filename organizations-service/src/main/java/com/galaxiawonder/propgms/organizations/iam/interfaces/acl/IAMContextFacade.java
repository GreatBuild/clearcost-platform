// Paquete de nuestra nueva capa ACL en el microservicio
package com.galaxiawonder.propgms.organizations.iam.interfaces.acl;


import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;

/**
 * Fachada (ACL) para interactuar con el contexto delimitado de IAM.
 * * Esta interfaz es un "contrato" que define los métodos que el
 * microservicio de Organizaciones necesita del (futuro) microservicio de IAM.
 */
public interface IAMContextFacade {

    /**
     * Obtiene los detalles del perfil de una persona por su ID.
     */
    ProfileDetails getProfileDetailsById(Long id);

    /**
     * Obtiene el ID de una persona a partir de su email.
     */
    Long getPersonIdFromEmail(String email);



    /**
     * Obtiene los detalles del perfil de una persona por su ID de persona.
     */
    ProfileDetails getProfileDetailsByPersonId(Long personId);

    /**
     * Obtiene los detalles del perfil de una persona por su email.
     */
    ProfileDetails getProfileDetailsByEmail(String email);


}