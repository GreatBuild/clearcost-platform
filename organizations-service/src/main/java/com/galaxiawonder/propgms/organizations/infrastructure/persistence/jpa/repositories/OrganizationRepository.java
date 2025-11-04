// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.Ruc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    /**
     * Verifica si una organización existe por su RUC.
     * @param ruc RUC
     * @return True si existe, false de lo contrario
     */
    boolean existsByRuc(Ruc ruc);

    /**
     * Busca una organización por su RUC.
     * @param ruc RUC
     * @return una Organization
     */
    Organization findByRuc(Ruc ruc);

    /**
     * Busca una organización por su ID.
     * @param id ID de la organización
     * @return una Organization
     */
    Optional<Organization> findById(Long id);

    /**
     * Busca una organización usando el ID de una invitación.
     * @param invitationId ID de la invitación
     * @return la Organization que contiene esa invitación
     */
    @Query("""
    SELECT i.organization
    FROM OrganizationInvitation i
    WHERE i.id = :invitationId
""")
    Optional<Organization> findOrganizationByInvitationId(@Param("invitationId") Long invitationId);

    /**
     * Busca una organización usando el ID de un miembro.
     *
     * @param memberId el ID del miembro de la organización
     * @return la {@link Organization} que contiene al miembro
     */
    @Query("""
    SELECT m.organization
    FROM OrganizationMember m
    WHERE m.id = :memberId
""")
    Optional<Organization> findOrganizationByMemberId(@Param("memberId") Long memberId);

    /**
     * Recupera todas las {@link Organization} donde una persona es miembro.
     *
     * @param personId el ID de la persona
     * @return una lista de organizaciones donde la persona es miembro
     */
    @Query("""
    SELECT o
    FROM Organization o
    JOIN o.members m
    WHERE m.personId.personId = :personId
""")
    Optional<List<Organization>> findAllOrganizationsByOrganizationMemberPersonId(@Param("personId") Long personId);

}