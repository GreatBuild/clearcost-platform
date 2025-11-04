// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.entities;


import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationInvitationStatuses;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organization_invitation_status") // Añadido nombre de tabla
public class OrganizationInvitationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false) // Añadido 'nullable = false'
    private OrganizationInvitationStatuses name;

    public OrganizationInvitationStatus(OrganizationInvitationStatuses name) {
        this.name = name;
    }

    public static OrganizationInvitationStatus getDefaultStatus() {
        return new OrganizationInvitationStatus(OrganizationInvitationStatuses.PENDING);
    }

    public static OrganizationInvitationStatus toOrganizationInvitationStatusFromName(String name) {
        return new OrganizationInvitationStatus(OrganizationInvitationStatuses.valueOf(name));
    }

    public static List<OrganizationInvitationStatus> validateStatusSet(List<OrganizationInvitationStatus> statuses) {
        return statuses == null || statuses.isEmpty()
                ? List.of(getDefaultStatus())
                : statuses;
    }

    public String getStringName() {
        return name.name();
    }
}