package com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands;

import java.util.Date;

/**
 * CreateProjectCommand
 * @summary Command object representing the data required to create a new project.
 */
public record CreateProjectCommand(
        String projectName,
        String description,
        Date startDate,
        Date endDate,
        Long organizationId,
        String contractingEntityEmail
) {
    public CreateProjectCommand {
        if (projectName == null || projectName.isBlank()) {
            throw new IllegalArgumentException("projectName cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description cannot be null or blank");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate cannot be null");
        }
        if (organizationId == null) {
            throw new IllegalArgumentException("organizationId cannot be null");
        }
        if (contractingEntityEmail == null || contractingEntityEmail.isBlank()) {
            throw new IllegalArgumentException("contractingEntityEmail cannot be null or blank");
        }
    }
}