package com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities.ProjectStatus;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.ProjectStatuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Long> {

    Optional<ProjectStatus> findByName(ProjectStatuses name);

    boolean existsByName(ProjectStatuses name);
}