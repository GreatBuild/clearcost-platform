package com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects;

/**
 * Represents the member's type in an organization.
 * - COORDINATOR: Project member with all the permissions.
 * - SPECIALIST: Project member with some permissions.
 */
public enum ProjectTeamMemberTypes {
    COORDINATOR,
    SPECIALIST
}