package com.greatbuild.clearcost.msvc.projects.repositories;

import com.greatbuild.clearcost.msvc.projects.models.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Encuentra todos los proyectos de una organización
     */
    List<Project> findByOrganizationId(Long organizationId);

    /**
     * Encuentra proyectos donde el usuario es contractingEntity (cliente)
     */
    List<Project> findByContractingEntityId(Long contractingEntityId);

    /**
     * Encuentra proyectos donde el usuario es miembro
     */
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.members m " +
           "WHERE p.organizationId = :organizationId AND m.userId = :userId")
    List<Project> findByOrganizationIdAndUserId(@Param("organizationId") Long organizationId, 
                                                 @Param("userId") Long userId);
}
