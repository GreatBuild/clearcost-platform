package com.greatbuild.clearcost.msvc.organizations.services;

import com.greatbuild.clearcost.msvc.organizations.models.entities.Organization;
import com.greatbuild.clearcost.msvc.organizations.models.entities.OrganizationMember;
import java.util.List;
import java.util.Optional;

public interface OrganizationService {
    List<Organization> findAll();
    Optional<Organization> findById(Long id);
    Organization save(Organization organization);
    void delete(Long id);
    List<OrganizationMember> getMembers(Long organizationId);
    void removeMember(Long organizationId, Long memberId);
}
