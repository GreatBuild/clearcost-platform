package com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects;


import jakarta.persistence.Embeddable;

/**
 * OrganizationId
 *
 * @summary
 * Value object that encapsulates the identifier of an Organization.
 * Internally wraps a {@code Long} which represents the organization's ID in another bounded context.
 *
 * @param organizationId the numeric identifier of the organization, must be positive and non-null
 *
 * @since 1.0
 */
@Embeddable
public record OrganizationId(Long organizationId) {

    /**
     * Validates the {@code organizationId} description.
     *
     * @throws IllegalArgumentException if {@code organizationId} is null or less than 1
     */
    public OrganizationId {
        if (organizationId == null || organizationId < 1) {
            throw new IllegalArgumentException("Organization id cannot be null or less than 1");
        }
    }


}