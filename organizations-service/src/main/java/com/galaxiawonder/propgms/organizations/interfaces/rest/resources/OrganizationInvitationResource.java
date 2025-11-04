// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.resources;

import jakarta.annotation.Nullable;
import java.util.Date;

/**
 * Resource que representa los detalles de una invitación.
 *
 * @param id               el ID de la invitación
 * @param organizationName el nombre de la organización
 * @param invitedBy        el nombre de quien invita
 * @param status           el estado de la invitación (PENDING, ACCEPTED, REJECTED)
 * @param invitedAt        la fecha de creación
 * @param invitedPerson    el nombre de la persona invitada
 */
public record OrganizationInvitationResource(
        Long id,
        @Nullable String organizationName,
        @Nullable String invitedBy,
        String status,
        Date invitedAt,
        @Nullable String invitedPerson
) {
}