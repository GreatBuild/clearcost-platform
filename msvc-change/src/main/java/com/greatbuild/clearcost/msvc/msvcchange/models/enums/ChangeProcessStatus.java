package com.greatbuild.clearcost.msvc.msvcchange.models.enums;

/**
 * Enum para los estados de un proceso de cambio
 * Mapeado a la tabla change_process_statuses en BD
 */
public enum ChangeProcessStatus {
    PENDING(1L, "PENDING"),
    APPROVED(2L, "APPROVED"),
    REJECTED(3L, "REJECTED");

    private final Long id;
    private final String name;

    ChangeProcessStatus(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Obtiene el enum por su ID
     */
    public static ChangeProcessStatus fromId(Long id) {
        for (ChangeProcessStatus status : values()) {
            if (status.id.equals(id)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status ID inválido: " + id);
    }

    /**
     * Obtiene el enum por su nombre
     */
    public static ChangeProcessStatus fromName(String name) {
        for (ChangeProcessStatus status : values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status name inválido: " + name);
    }
}
