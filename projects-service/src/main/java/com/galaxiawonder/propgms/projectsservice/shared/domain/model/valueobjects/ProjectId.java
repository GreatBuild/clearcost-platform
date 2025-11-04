package com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects;


import jakarta.persistence.Embeddable;

/**
 * ProjectId
 *
 * @summary
 * Value object that encapsulates the identifier of a Project.
 * Enhances domain expressiveness and ensures type safety when referencing projects across the system.
 * Internally wraps a {@code Long} value.
 *
 * @param projectId the numeric identifier of the project, must be positive and non-null
 *
 * @since 1.0
 */
@Embeddable
public record ProjectId(Long projectId) {

    /**
     * Validates the {@code projectId} value.
     *
     * @throws IllegalArgumentException if {@code projectId} is null or less than 1
     */
    public ProjectId {
        if (projectId == null || projectId < 1) {
            throw new IllegalArgumentException("Project id cannot be null or less than 1");
        }
    }
}