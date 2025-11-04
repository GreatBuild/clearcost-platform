package com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities.Specialty;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.Specialties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    Optional<Specialty> findByName(Specialties name);

    boolean existsByName(Specialties name);
}