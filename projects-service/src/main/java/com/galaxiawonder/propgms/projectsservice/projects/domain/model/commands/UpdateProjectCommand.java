package com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands;

import java.util.Date;

/**
 * Command representing the data required to update a Project.
 * Fields are allowed to be empty or null-safe by design.
 */
public record UpdateProjectCommand(
        Long projectId,
        String name,
        String description,
        String status,
        Date endingDate
) {}