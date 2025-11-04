package com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl;


import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.ProfileDetails;


/**
 * IAMContextFacade
 * @summary
 * Facade interface that exposes selected IAM operations to other bounded contexts.
 */
public interface IAMContextFacade {

    ProfileDetails getProfileDetailsByPersonId(Long personId);

    ProfileDetails getProfileDetailsByEmail(String email);

    ProfileDetails getProfileDetailsById(Long id);

    Long getPersonIdFromEmail(String email);


    String getSpecialtyFromPersonId(Long id);
}