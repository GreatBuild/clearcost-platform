package com.greatbuild.clearcost.msvc.invitations.models.entities;

public enum InvitationStatus {
    PENDING_PROCESSING,  // Esperando validación asíncrona
    PENDING,             // Validada, esperando respuesta del invitado
    ACCEPTED,
    REJECTED,
    EXPIRED
}
