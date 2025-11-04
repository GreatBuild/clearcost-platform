package com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates.ProjectTeamMember;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.OrganizationMemberId;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.ProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectTeamMemberRepository extends JpaRepository<ProjectTeamMember, Long> {
    Optional<ProjectTeamMember> findByOrganizationMemberIdAndProjectId(OrganizationMemberId organizationMemberId, ProjectId projectId);
    Optional<List<ProjectTeamMember>> findAllByProjectId(ProjectId projectId);
}