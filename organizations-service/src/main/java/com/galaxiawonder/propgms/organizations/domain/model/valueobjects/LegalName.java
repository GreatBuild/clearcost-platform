// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.valueobjects;

import java.util.Objects;
/**
 * Legal Name Value Object
 */
public record LegalName(String legalName) {
    /**
     * Default Constructor
     */
    public LegalName(){this("");}
    /**
     * Constructor with validation
     * @param legalName Legal Name
     */
    public LegalName {
        Objects.requireNonNull(legalName, "Legal projectName cannot be null");
    }

    /**
     * Legal Name Getter
     * @return Legal Name
     */
    @Override
    public String toString() { return legalName; }
}