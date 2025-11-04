package com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects;


import jakarta.persistence.Embeddable;

/**
 * PersonId
 *
 * @summary
 * Value object that encapsulates the identifier of a Person.
 * Internally wraps a {@code Long} which represents the person's ID in another bounded context (like IAM).
 *
 * @param personId the numeric identifier of the person, must be positive and non-null
 *
 * @since 1.0
 */
@Embeddable
public record PersonId(Long personId) {

    /**
     * Validates the {@code personId} description.
     *
     * @throws IllegalArgumentException if {@code personId} is null or less than 1
     */
    public PersonId {
        if (personId == null || personId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
    }
}