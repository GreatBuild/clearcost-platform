package com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects;


import jakarta.persistence.Embeddable;

/**
 * OrganizationMemberId
 *
 * @summary
 * Value object that encapsulates the identifier of an OrganizationMember.
 * Internally wraps a {@code Long} which represents the ID in another bounded context.
 *
 * @param organizationMemberId the numeric identifier of the organization member, must be positive and non-null
 *
 * @since 1.0
 */
@Embeddable
public record OrganizationMemberId(Long organizationMemberId) {

    /**
     * Validates the {@code organizationMemberId} description.
     *
     * @throws IllegalArgumentException if {@code organizationMemberId} is null or less than 1
     */
    public OrganizationMemberId {
        if (organizationMemberId == null || organizationMemberId < 1) {
            throw new IllegalArgumentException("Organization member id cannot be null or less than 1");
        }
    }
}