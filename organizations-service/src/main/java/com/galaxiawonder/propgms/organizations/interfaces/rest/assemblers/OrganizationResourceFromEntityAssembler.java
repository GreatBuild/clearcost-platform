// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.assemblers;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.interfaces.rest.resources.OrganizationResource;

/**
 * Assembler para crear un OrganizationResource desde una entidad Organization.
 */
public class OrganizationResourceFromEntityAssembler {

    public static OrganizationResource toResourceFromEntity(Organization entity) {
        return new OrganizationResource(
                entity.getId(),
                entity.getLegalName().toString(),
                entity.getCommercialName().toString(),
                entity.getRuc().value(),
                entity.getCreatedBy().personId(),
                entity.getStatus().getStringName(),
                entity.getCreatedAt()
        );
    }
}