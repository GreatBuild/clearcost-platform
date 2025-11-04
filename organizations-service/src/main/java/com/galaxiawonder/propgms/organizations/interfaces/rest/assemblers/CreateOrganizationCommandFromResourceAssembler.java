// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.assemblers;


import com.galaxiawonder.propgms.organizations.domain.model.commands.CreateOrganizationCommand;
import com.galaxiawonder.propgms.organizations.interfaces.rest.resources.CreateOrganizationResource;

/**
 * Assembler para transformar CreateOrganizationResource en CreateOrganizationCommand.
 */
public class CreateOrganizationCommandFromResourceAssembler {

    public static CreateOrganizationCommand toCommandFromResource(CreateOrganizationResource resource) {
        return new CreateOrganizationCommand(resource.legalName(), resource.commercialName(), resource.ruc(), resource.createdBy());
    }
}