package com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates.Project;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.PersonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    /**
     * Retrieves all {@link Project} entities where the given person is registered as a project team member.
     *
     * @param personId the ID of the person
     * @return a list of projects where the person is a team member
     */
    @Query("""
    SELECT p
    FROM Project p
    JOIN ProjectTeamMember ptm ON ptm.projectId.projectId = p.id
    WHERE ptm.personId.personId = :personId
""")
    Optional<List<Project>> findAllProjectsByTeamMemberPersonId(@Param("personId") Long personId);

    Optional<List<Project>> findAllProjectsByContractingEntityId(PersonId contractingEntityId);
}