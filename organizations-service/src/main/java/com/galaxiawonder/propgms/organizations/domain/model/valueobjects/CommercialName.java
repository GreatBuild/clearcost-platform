// Paquete actualizado para el nuevo microservicio
package com.galaxiawonder.propgms.organizations.domain.model.valueobjects;

import java.util.Objects;
/**
 * Commercial Name Value Object
 */
public record CommercialName(String commercialName) {
    /**
     * Default Constructor
     */
    public CommercialName(){this("");}
    /**
     * Constructor with validation
     * @param commercialName Legal Name
     */
    public CommercialName {
        Objects.requireNonNull(commercialName, "Commercial projectName cannot be null");
    }
    /**
     * Commercial Name Getter
     * @return Commercial Name
     */
    public String toString() { return commercialName; }
}