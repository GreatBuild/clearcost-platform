package com.greatbuild.clearcost.msvc.invitations.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long organizationId; // ID de la organización (de msvc-organizations)

    @NotNull
    private Long inviterId; // ID del usuario que invita (de msvc-users)

    @NotNull
    private Long inviteeUserId; // ID del usuario invitado (de msvc-users)

    @Email
    private String inviteeEmail; // Email del invitado (para notificaciones)

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(7); // ¡Tip! Las invitaciones deben expirar
        this.status = InvitationStatus.PENDING;
    }

    // --- Getters y Setters ---
    // (Omitidos por brevedad, pero debes añadirlos)
}
