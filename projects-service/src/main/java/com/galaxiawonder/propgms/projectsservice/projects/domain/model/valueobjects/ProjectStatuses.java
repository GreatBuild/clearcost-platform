package com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects;

/**
 * Enumeration that defines the possible lifecycle statuses of a project
 * within the Project Management domain.
 */
public enum ProjectStatuses {

    /** The project is in the basic studies stage. */
    BASIC_STUDIES,

    /** The project is currently in the design process. */
    DESIGN_IN_PROCESS,

    /** The project is under review by the relevant authority. */
    UNDER_REVIEW,

    /** A change to the project has been formally requested. */
    CHANGE_REQUESTED,

    /** A requested change is pending approval. */
    CHANGE_PENDING,

    /** The project has been officially approved. */
    APPROVED
}