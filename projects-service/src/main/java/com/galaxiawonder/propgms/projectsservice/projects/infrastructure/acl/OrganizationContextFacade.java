package com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl;

public interface OrganizationContextFacade {

    Long getContractorIdFromOrganizationId(Long organizationId);

    Long getOrganizationMemberIdFromPersonAndOrganizationId(Long personId, Long organizationId);

    Long getPersonIdFromOrganizationMemberId(Long organizationMemberId);
}