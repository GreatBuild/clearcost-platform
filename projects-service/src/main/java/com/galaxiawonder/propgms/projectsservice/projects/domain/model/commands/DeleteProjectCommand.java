package com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands;

/**
 * Command to delete a project
 * @param id the id of a project
 * Cannot be null
 */
public record DeleteProjectCommand(Long id) {
    /**
     * Constructor
     * @param id of the id of
     * @throws IllegalArgumentException if id is null
     */
    public DeleteProjectCommand {
        if(id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
    }
}