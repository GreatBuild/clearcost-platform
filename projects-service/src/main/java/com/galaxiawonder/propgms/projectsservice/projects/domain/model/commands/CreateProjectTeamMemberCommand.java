package com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands;

public record CreateProjectTeamMemberCommand(
        Long organizationMemberId,
        Long projectId,
        String specialty,
        String memberType
) {
    /**
     * @throws IllegalArgumentException if organizationMemberId is null
     * @throws IllegalArgumentException if projectId is null or zero
     * @throws IllegalArgumentException if specialty is null
     * @throws IllegalArgumentException if memberType is null
     */
    public CreateProjectTeamMemberCommand {
        if (organizationMemberId == null) {
            throw new IllegalArgumentException("Organization member id cannot be null");
        }
        if (projectId == null || projectId.equals(0L)) {
            throw new IllegalArgumentException("Project id cannot be null or zero");
        }
        if (specialty == null) {
            throw new IllegalArgumentException("Speciality cannot be null");
        }
        if (memberType == null) {
            throw new IllegalArgumentException("Project team member type cannot be null");
        }
    }
}