// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.assemblers;


import com.galaxiawonder.propgms.organizations.domain.model.commands.InvitePersonToOrganizationByEmailCommand;
import com.galaxiawonder.propgms.organizations.interfaces.rest.resources.InvitePersonToOrganizationResource;

/**
 * Assembler para convertir InvitePersonToOrganizationResource en InvitePersonToOrganizationByEmailCommand.
 */
public class InvitePersonToOrganizationCommandFromResource {

    public static InvitePersonToOrganizationByEmailCommand toCommandFromResource(
            InvitePersonToOrganizationResource resource) {
        return new InvitePersonToOrganizationByEmailCommand(resource.organizationId(), resource.email());
    }
}