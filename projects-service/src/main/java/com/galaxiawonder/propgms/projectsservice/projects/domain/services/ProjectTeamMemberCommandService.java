package com.galaxiawonder.propgms.projectsservice.projects.domain.services;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates.ProjectTeamMember;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.CreateProjectTeamMemberCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.DeleteProjectTeamMemberCommand;

import java.util.Optional;

public interface ProjectTeamMemberCommandService {
    /**
     * Handles the creation of a new {@link ProjectTeamMember} based on the provided command.
     *
     * @param command the command containing necessary data to create the team member
     * @return an {@link Optional} containing the created {@link ProjectTeamMember} if successful
     */
    Optional<ProjectTeamMember> handle(CreateProjectTeamMemberCommand command);

    void handle(DeleteProjectTeamMemberCommand command);
}