package com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Description(String description) {

    public Description() {
        this(null);
    }

    public Description {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description must not be null or blank");
        }
        if (description.length() > 200) {
            throw new IllegalArgumentException("Description must not exceed 200 characters");
        }
    }
}