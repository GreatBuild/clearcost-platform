// Paquete actualizado
package com.galaxiawonder.propgms.organizations.interfaces.rest.controllers;


import com.galaxiawonder.propgms.organizations.domain.model.aggregates.Organization;
import com.galaxiawonder.propgms.organizations.domain.model.commands.*;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationInvitation;
import com.galaxiawonder.propgms.organizations.domain.model.entities.OrganizationMember;
import com.galaxiawonder.propgms.organizations.domain.model.queries.*;
import com.galaxiawonder.propgms.organizations.domain.services.OrganizationCommandService;
import com.galaxiawonder.propgms.organizations.domain.services.OrganizationQueryService;
import com.galaxiawonder.propgms.organizations.interfaces.rest.assemblers.*;
import com.galaxiawonder.propgms.organizations.interfaces.rest.resources.*;
import com.galaxiawonder.propgms.organizations.shared.domain.model.valueobjects.ProfileDetails;
import com.galaxiawonder.propgms.organizations.shared.interfaces.rest.resources.GenericMessageResource;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(value="/api/v1/organizations", produces = APPLICATION_JSON_VALUE)
@Tag(name="Organizations", description="Endpoints para Organizaciones")
public class OrganizationController {
    private final OrganizationCommandService organizationCommandService;
    private final OrganizationQueryService organizationQueryService;

    public OrganizationController(OrganizationCommandService organizationCommandService, OrganizationQueryService organizationQueryService) {
        this.organizationCommandService = organizationCommandService;
        this.organizationQueryService = organizationQueryService;
    }

    @Operation( summary = "Crear una Organización")
    @ApiResponses(value={
            @ApiResponse(responseCode = "201", description = "Organización creada"),
            @ApiResponse(responseCode = "400", description = "Petición inválida")
    })
    @PostMapping
    public ResponseEntity<OrganizationResource>
    createOrganization(@RequestBody CreateOrganizationResource resource){
        Optional<Organization> organization = organizationCommandService
                .handle(CreateOrganizationCommandFromResourceAssembler.toCommandFromResource(resource));
        return organization.map(source -> new ResponseEntity<>(OrganizationResourceFromEntityAssembler.toResourceFromEntity(source), CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation( summary = "Obtener una Organización por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organización encontrada"),
            @ApiResponse(responseCode = "404", description = "Organización no encontrada")
    })
    @GetMapping("{id}")
    public ResponseEntity<OrganizationResource>
    getOrganizationById(@PathVariable Long id){
        Optional<Organization> organization =
                organizationQueryService.handle(new GetOrganizationByIdQuery(id));
        return organization.map( source ->
                        ResponseEntity.ok(OrganizationResourceFromEntityAssembler.toResourceFromEntity(source)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation( summary = "Eliminar una Organización por RUC")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organización eliminada"),
            @ApiResponse(responseCode = "404", description = "Organización no encontrada")
    })
    @DeleteMapping("{ruc}")
    public ResponseEntity<GenericMessageResource> deleteOrganization(
            @Parameter(description = "RUC")
            @PathVariable String ruc) {
        var deleteOrganizationCommand = new DeleteOrganizationCommand(ruc);
        organizationCommandService.handle(deleteOrganizationCommand);
        return ResponseEntity.ok(new GenericMessageResource("Organization with given RUC successfully deleted"));
    }

    @Operation( summary = "Actualizar una Organización por ID")
    @PatchMapping("{id}")
    public ResponseEntity<GenericMessageResource> updateOrganization(
            @PathVariable Long id,
            @RequestBody UpdateOrganizationResource resource) {

        var command = new UpdateOrganizationCommand(
                id,
                resource.commercialName() != null ? resource.commercialName() : "",
                resource.legalName() != null ? resource.legalName() : ""
        );

        organizationCommandService.handle(command);
        return ResponseEntity.ok(new GenericMessageResource("Organization with given ID successfully updated"));
    }

    @Operation( summary = "Invitar a una persona a una organización por email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invitación creada"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "Organización no encontrada")
    })
    @PostMapping("/invitations")
    public ResponseEntity<OrganizationInvitationResource> invitePersonToOrganization(
            @RequestBody InvitePersonToOrganizationResource resource) {

        Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> invitation = organizationCommandService
                .handle(InvitePersonToOrganizationCommandFromResource.toCommandFromResource(resource));

        return buildOrganizationInvitationResource(invitation);
    }

    @Operation( summary = "Aceptar una invitación por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitación aceptada"),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Invitación no encontrada")
    })
    @PatchMapping("/invitations/{id}/accept")
    public ResponseEntity<OrganizationInvitationResource> acceptInvitation(
            @Parameter(description = "ID de la invitación a aceptar", required = true)
            @PathVariable Long id) {

        Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> updatedInvitation = organizationCommandService
                .handle(new AcceptInvitationCommand(id));

        return buildOrganizationInvitationResource(updatedInvitation);
    }

    @Operation( summary = "Rechazar una invitación por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitación rechazada"),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Invitación no encontrada")
    })
    @PatchMapping("/invitations/{id}/reject")
    public ResponseEntity<OrganizationInvitationResource> rejectInvitation(
            @Parameter(description = "ID de la invitación a rechazar", required = true)
            @PathVariable Long id) {

        Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> updatedInvitation = organizationCommandService
                .handle(new RejectInvitationCommand(id));

        return buildOrganizationInvitationResource(updatedInvitation);
    }

    private static ResponseEntity<OrganizationInvitationResource> buildOrganizationInvitationResource(
            Optional<Triple<Organization, OrganizationInvitation, ProfileDetails>> invitationTriple) {

        return invitationTriple
                .map(triple -> {
                    OrganizationInvitationResource resourceResponse =
                            OrganizationInvitationResourceFromEntityAssembler.toResourceFromEntity(triple);
                    return new ResponseEntity<>(resourceResponse, HttpStatus.CREATED);
                })
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation( summary = "Obtener todas las invitaciones por ID de organización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitaciones encontradas"),
            @ApiResponse(responseCode = "404", description = "Organización no encontrada")
    })
    @GetMapping("/{organizationId}/invitations")
    public ResponseEntity<List<OrganizationInvitationResource>> getAllInvitationsByOrganizationId(
            @Parameter(description = "ID de la organización", required = true)
            @PathVariable Long organizationId) {

        List<ImmutablePair<OrganizationInvitation, ProfileDetails>> organizationInvitations =
                organizationQueryService.handle(new GetAllInvitationsByOrganizationIdQuery(organizationId));

        List<OrganizationInvitationResource> resources = organizationInvitations.stream()
                .map(OrganizationInvitationResourceFromEntityAssembler::toResourceFromPair)
                .toList();

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @Operation( summary = "Obtener todas las invitaciones por ID de persona")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitaciones encontradas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron invitaciones")
    })
    @GetMapping("/invitations/by-person-id/{personId}")
    public ResponseEntity<List<OrganizationInvitationResource>> getAllInvitationsByPersonId(
            @Parameter(description = "ID de la persona", required = true)
            @PathVariable Long personId) {

        List<Triple<Organization, OrganizationInvitation, ProfileDetails>> organizationInvitations =
                organizationQueryService.handle(new GetAllInvitationsByPersonIdQuery(personId));

        List<OrganizationInvitationResource> resources = organizationInvitations.stream()
                .map(OrganizationInvitationResourceFromEntityAssembler::toResourceFromEntity)
                .filter(Objects::nonNull)
                .toList();

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @Operation( summary = "Obtener todos los miembros por ID de organización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Miembros encontrados"),
            @ApiResponse(responseCode = "404", description = "Organización no encontrada")
    })
    @GetMapping("/{organizationId}/members")
    public ResponseEntity<List<OrganizationMemberResource>> getAllMembersByOrganizationId(
            @Parameter(description = "ID de la organización", required = true)
            @PathVariable Long organizationId) {

        List<OrganizationMember> organizationMembers =
                organizationQueryService.handle(new GetAllMembersByOrganizationIdQuery(organizationId));

        List<OrganizationMemberResource> resources = organizationMembers.stream()
                .map(OrganizationMemberResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @Operation( summary = "Eliminar un miembro de la organización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Miembro eliminado"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "404", description = "Miembro no encontrado")
    })
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMemberById(
            @Parameter(description = "ID del miembro", required = true)
            @PathVariable Long memberId
    ) {
        organizationCommandService.handle(new DeleteOrganizationMemberCommand(memberId));
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Obtener organizaciones por ID de persona (miembro)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organizaciones encontradas"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    })
    @GetMapping("/by-person-id/{id}")
    public ResponseEntity<List<OrganizationResource>> getOrganizationsByPersonId(
            @Parameter(description = "ID de la persona", required = true)
            @PathVariable("id") Long personId
    ) {
        List<Organization> organizations = organizationQueryService.handle(
                new GetAllOrganizationsByMemberPersonIdQuery(personId)
        );

        List<OrganizationResource> resources = organizations.stream()
                .map(OrganizationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
}