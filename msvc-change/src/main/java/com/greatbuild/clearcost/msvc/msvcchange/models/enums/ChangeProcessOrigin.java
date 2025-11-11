package com.greatbuild.clearcost.msvc.msvcchange.models.enums;

/**
 * Enum para el origen de un proceso de cambio
 * Por ahora siempre será CHANGE_REQUEST (solicitud del cliente)
 */
public enum ChangeProcessOrigin {
    CHANGE_REQUEST("CHANGE_REQUEST"),      // Solicitado por el cliente
    TECHNICAL_QUERY("TECHNICAL_QUERY");    // Solicitado por la entidad corporativa (futuro)

    private final String value;

    ChangeProcessOrigin(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ChangeProcessOrigin fromValue(String value) {
        for (ChangeProcessOrigin origin : values()) {
            if (origin.value.equalsIgnoreCase(value)) {
                return origin;
            }
        }
        throw new IllegalArgumentException("Origin value inválido: " + value);
    }
}
