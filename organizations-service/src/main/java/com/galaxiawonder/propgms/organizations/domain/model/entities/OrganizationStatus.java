// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.entities;


import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationStatuses;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organization_status") // Añadido nombre de tabla por buenas prácticas
public class OrganizationStatus {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false) // Añadido 'nullable = false'
    private OrganizationStatuses name;

    public OrganizationStatus(OrganizationStatuses name) {
        this.name = name;
    }

    public static OrganizationStatus getDefaultUserType() {
        return new OrganizationStatus(OrganizationStatuses.ACTIVE);
    }

    public static OrganizationStatus toOrganizationStatusFromName(String name) {
        return new OrganizationStatus(OrganizationStatuses.valueOf(name));
    }

    public static List<OrganizationStatus> validateOrganizationStatusSet(List<OrganizationStatus> types) {
        return types == null || types.isEmpty()
                ? List.of(getDefaultUserType())
                : types;
    }

    public String getStringName() {
        return name.name();
    }
}