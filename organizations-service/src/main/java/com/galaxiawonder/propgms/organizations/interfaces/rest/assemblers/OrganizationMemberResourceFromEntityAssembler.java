// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.assemblers;


import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationMember;
import com.galaxiawonder.propgms.organizations.interfaces.rest.resources.OrganizationMemberResource;

public class OrganizationMemberResourceFromEntityAssembler {

    public static OrganizationMemberResource toResourceFromEntity(
            OrganizationMember entity
    ) {
        return new OrganizationMemberResource(
                entity.getId(),
                entity.getName().getFullName(),
                entity.getMemberType().getStringName(),
                entity.getCreatedAt()
        );
    }
}