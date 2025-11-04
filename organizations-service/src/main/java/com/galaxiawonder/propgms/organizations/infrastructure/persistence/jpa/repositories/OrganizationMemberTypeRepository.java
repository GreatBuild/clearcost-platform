// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationMemberType;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationMemberTypes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationMemberTypeRepository extends JpaRepository<OrganizationMemberType, Long> {

    /**
     * Busca un tipo de miembro por su nombre.
     *
     * @param name el enum {@link OrganizationMemberTypes}
     * @return un {@link Optional} conteniendo el {@link OrganizationMemberType} si se encuentra
     */
    Optional<OrganizationMemberType> findByName(OrganizationMemberTypes name);

    /**
     * Verifica si existe un tipo de miembro por su nombre.
     *
     * @param name el enum {@link OrganizationMemberTypes}
     * @return true si existe, false de lo contrario
     */
    boolean existsByName(OrganizationMemberTypes name);
}