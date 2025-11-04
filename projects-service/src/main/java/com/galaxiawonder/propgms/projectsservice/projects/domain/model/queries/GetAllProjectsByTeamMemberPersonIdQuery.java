package com.galaxiawonder.propgms.projectsservice.projects.domain.model.queries;




/**
 * Query object used to retrieve all Project entities
 * in which a specific person is registered as a member.
 *
 * @param personId the unique identifier of the person
 *
 * @since 1.0
 */
public record GetAllProjectsByTeamMemberPersonIdQuery(
        Long personId
) {
}