// El paquete se ha actualizado a la estructura de nuestro microservicio
package com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects;

/**
 * PersonName
 *
 * @summary
 * Value object that encapsulates a person's full projectName, split into first and last projectName.
 * Provides basic validation and convenience methods for consistent projectName handling across the domain.
 *
 * @param firstName the person's first projectName, must not be null or blank
 * @param lastName  the person's last projectName, must not be null or blank
 *
 * @since 1.0
 */
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
     * @param firstName the person's first projectName
     * @param lastName  the person's last projectName
     * @throws IllegalArgumentException if either field is {@code null} or blank
     */
    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First projectName must not be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last projectName must not be null or blank");
        }
    }

    /**
     * Returns the person's full projectName in the format "First Last".
     *
     * @return the full projectName string
     */
    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }
}