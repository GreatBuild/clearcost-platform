package com.galaxiawonder.propgms.projectsservice.projects.domain.services;



import com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates.Project;

import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.CreateProjectCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.DeleteProjectCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.UpdateProjectCommand;

import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

/**
 * @name ProjectCommandService
 * @summary
 * This interface represents the service to handle project source commands.
 */
public interface ProjectCommandService {
    /**
     * Handles the create project command.
     * @param command The create project command containing the required project details.
     * @return The created project.
     *
     * @throws IllegalArgumentException If any required field in the command is null or blank.
     * @throws EntityNotFoundException If the associated organization or contracting entity is not found.
     * @see CreateProjectCommand
     */
    Optional<Project> handle(CreateProjectCommand command);
    /**
     * Handles the update project command.
     * @param command The update project command containing the updated project data.
     * @return The updated project.
     * @see UpdateProjectCommand
     */
    Optional<Project> handle(UpdateProjectCommand command);
    /**
     * Handles the delete project command.
     * @param command The delete project command containing the project ID.
     * @see DeleteProjectCommand
     */
    void handle(DeleteProjectCommand command);
}