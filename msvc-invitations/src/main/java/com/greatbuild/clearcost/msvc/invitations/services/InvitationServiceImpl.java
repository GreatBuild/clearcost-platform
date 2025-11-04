package com.greatbuild.clearcost.msvc.invitations.services;

import com.greatbuild.clearcost.msvc.invitations.models.entities.Invitation;
import com.greatbuild.clearcost.msvc.invitations.repositories.InvitationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitationServiceImpl implements InvitationService {

    private static final Logger log = LoggerFactory.getLogger(InvitationServiceImpl.class);

    private final InvitationRepository repository;

    public InvitationServiceImpl(InvitationRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void createInvitation(Invitation invitation) {
        // (Aquí podrías añadir lógica de negocio, como:
        // "¿Ya existe una invitación PENDIENTE para este usuario en esta org?")

        log.info("Creando nueva invitación para el usuario ID {} en la org ID {}",
                invitation.getInviteeUserId(), invitation.getOrganizationId());

        // El @PrePersist de la entidad se encargará de poner el
        // status PENDING y las fechas.
        repository.save(invitation);
    }
}
