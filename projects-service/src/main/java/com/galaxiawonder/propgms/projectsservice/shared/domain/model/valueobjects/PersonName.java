package com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * PersonName
 *
 * @summary
 * Value object that encapsulates a person's full name, split into first and last name.
 * Provides basic validation and convenience methods for consistent name handling across the domain.
 *
 * @param firstName the person's first name, must not be null or blank
 * @param lastName  the person's last name, must not be null or blank
 *
 * @since 1.0
 */
@Embeddable
public record PersonName(String firstName, String lastName) {

    /**
     * Default constructor required by frameworks (e.g., JPA).
     * Initializes both fields as {@code null}.
     */
    public PersonName() {
        this(null, null);
    }

    /**
     * Constructs a {@code PersonName} with validation.
     *
     * @param firstName the person's first name
     * @param lastName  the person's last name
     * @throws IllegalArgumentException if either field is {@code null} or blank
     */
    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null or blank");
        }
    }

    /**
     * Returns the person's full name in the format "First Last".
     *
     * @return the full name string
     */
    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }
}