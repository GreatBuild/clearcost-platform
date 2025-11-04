package com.galaxiawonder.propgms.projectsservice.projects.application.internal.eventhandlers;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.CreateProjectTeamMemberCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.events.ProjectCreatedEvent;
import com.galaxiawonder.propgms.projectsservice.projects.domain.services.ProjectTeamMemberCommandService;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl.OrganizationContextFacade;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ProjectCreatedEventHandler {

    private final OrganizationContextFacade organizationContextFacade; // <-- ¡Se inyectará el FakeOrganizationContextFacade!
    private final ProjectTeamMemberCommandService projectTeamMemberCommandService; // <-- Nuestro servicio interno

    public ProjectCreatedEventHandler(
            OrganizationContextFacade organizationContextFacade,
            ProjectTeamMemberCommandService projectTeamMemberCommandService) {
        this.organizationContextFacade = organizationContextFacade;
        this.projectTeamMemberCommandService = projectTeamMemberCommandService;
    }

    @EventListener
    public void on(ProjectCreatedEvent event) {
        Long organizationId = event.getOrganizationId().organizationId();

        // Usa el STUB FALSO de Organization
        Long personId = this.organizationContextFacade.getContractorIdFromOrganizationId(organizationId);
        Long memberId = this.organizationContextFacade.getOrganizationMemberIdFromPersonAndOrganizationId(personId, organizationId);

        this.projectTeamMemberCommandService.handle(
                new CreateProjectTeamMemberCommand(
                        memberId,
                        event.getProjectId().projectId(),
                        "NON_APPLICABLE", // Valor por defecto del handler
                        "COORDINATOR"     // Valor por defecto del handler
                )
        );
    }
}