package com.greatbuild.clearcost.msvc.organizations.repositories;

import com.greatbuild.clearcost.msvc.organizations.models.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByRuc(String ruc);
    
    /**
     * Encuentra todas las organizaciones donde el usuario es el owner (CONTRACTOR)
     * o es un miembro (MEMBER)
     */
    @Query("SELECT DISTINCT o FROM Organization o LEFT JOIN o.members m " +
           "WHERE o.ownerId = :userId OR m.userId = :userId")
    List<Organization> findByOwnerIdOrMemberUserId(@Param("userId") Long userId);
}
