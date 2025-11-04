package com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities.ProjectTeamMemberType;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.ProjectTeamMemberTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectTeamMemberTypeRepository extends JpaRepository<ProjectTeamMemberType, Long> {

    Optional<ProjectTeamMemberType> findByName(ProjectTeamMemberTypes name);

    boolean existsByName(ProjectTeamMemberTypes name);
}