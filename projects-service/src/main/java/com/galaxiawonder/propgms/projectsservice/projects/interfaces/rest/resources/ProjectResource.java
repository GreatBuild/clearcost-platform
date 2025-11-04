package com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.resources;

import java.util.Date;

/**
 * Resource that represents a Project to be returned from the API.
 */
public record ProjectResource(
        Long id,
        String projectName,
        String description,
        String status,
        Date startDate,
        Date endDate,
        Long organizationId,
        Long contractingEntityId
) {}