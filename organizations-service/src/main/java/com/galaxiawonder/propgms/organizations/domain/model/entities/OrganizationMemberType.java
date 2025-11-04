// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.entities;


import com.galaxiawonder.propgms.organizations.domain.model.valueobjects.OrganizationMemberTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organization_member_type") // Añadido nombre de tabla
public class OrganizationMemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false) // Añadido 'nullable = false'
    private OrganizationMemberTypes name;

    public OrganizationMemberType(OrganizationMemberTypes name) {
        this.name = name;
    }

    public static OrganizationMemberType getDefaultMemberType() {
        return new OrganizationMemberType(OrganizationMemberTypes.WORKER);
    }

    public static OrganizationMemberType toOrganizationMemberTypeFromName(String name) {
        return new OrganizationMemberType(OrganizationMemberTypes.valueOf(name));
    }

    public static List<OrganizationMemberType> validateOrganizationMemberTypeSet(List<OrganizationMemberType> types) {
        return types == null || types.isEmpty()
                ? List.of(getDefaultMemberType())
                : types;
    }

    public String getStringName() {
        return name.name();
    }
}