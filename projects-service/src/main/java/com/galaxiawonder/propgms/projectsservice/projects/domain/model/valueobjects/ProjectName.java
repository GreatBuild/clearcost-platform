package com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record ProjectName(String projectName) {

    public ProjectName() {
        this(null);
    }

    public ProjectName {
        if (projectName == null || projectName.isBlank()) {
            throw new IllegalArgumentException("Project name must not be null or blank");
        }
        if (projectName.length() > 30) {
            throw new IllegalArgumentException("Project name must not exceed 30 characters");
        }
    }
}