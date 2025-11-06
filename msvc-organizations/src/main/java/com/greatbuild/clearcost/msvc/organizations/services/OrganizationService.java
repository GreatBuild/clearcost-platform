package com.greatbuild.clearcost.msvc.organizations.services;

import com.greatbuild.clearcost.msvc.organizations.models.dtos.AddMemberDTO;
import com.greatbuild.clearcost.msvc.organizations.models.dtos.MemberResponseDTO;
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
    void removeMember(Long organizationId, Long memberId, Long requestingUserId);
    OrganizationMember addMember(AddMemberDTO dto);
    
    /**
     * Obtiene todas las organizaciones donde el usuario es CONTRACTOR (owner) o MEMBER
     * Incluye el rol del usuario en cada organización
     */
    List<com.greatbuild.clearcost.msvc.organizations.models.dtos.UserOrganizationResponseDTO> getUserOrganizations(Long userId);
    
    /**
     * Obtiene la lista de miembros de una organización con sus datos de usuario
     */
    List<MemberResponseDTO> getMembersWithUserData(Long organizationId);
}
