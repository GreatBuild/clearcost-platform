package com.galaxiawonder.propgms.projectsservice.projects.application.internal.commandservices;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates.Project;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.CreateProjectCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.CreateProjectTeamMemberCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.DeleteProjectCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.UpdateProjectCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities.ProjectStatus;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.ProjectStatuses;
import com.galaxiawonder.propgms.projectsservice.projects.domain.services.ProjectCommandService;
import com.galaxiawonder.propgms.projectsservice.projects.domain.services.ProjectTeamMemberCommandService;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl.IAMContextFacade;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl.OrganizationContextFacade;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories.ProjectRepository;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories.ProjectStatusRepository;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.PersonId;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.ProfileDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectCommandServiceImpl implements ProjectCommandService {

    private final ProjectRepository projectRepository;
    private final IAMContextFacade iamContextFacade;
    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectTeamMemberCommandService projectTeamMemberCommandService;
    private final OrganizationContextFacade organizationContextFacade;

    public ProjectCommandServiceImpl(ProjectRepository projectRepository,
                                     IAMContextFacade iamContextFacade,
                                     ProjectStatusRepository projectStatusRepository,
                                     ProjectTeamMemberCommandService projectTeamMemberCommandService,
                                     OrganizationContextFacade organizationContextFacade) {
        this.projectRepository = projectRepository;
        this.iamContextFacade = iamContextFacade;
        this.projectStatusRepository = projectStatusRepository;
        this.projectTeamMemberCommandService = projectTeamMemberCommandService;
        this.organizationContextFacade = organizationContextFacade;
    }

    @Override
    public Optional<Project> handle(CreateProjectCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("CreateProjectCommand must not be null");
        }

        // Usa el STUB FALSO de IAM
        Long contractingEntityId = iamContextFacade.getPersonIdFromEmail(command.contractingEntityEmail());
        ProfileDetails details = iamContextFacade.getProfileDetailsById(contractingEntityId);

        ProjectStatus initialStatus = getProjectStatus(ProjectStatuses.BASIC_STUDIES);

        var project = new Project(
                command,
                initialStatus,
                new PersonId(contractingEntityId),
                details.name(),
                details.email()
        );

        var createdProject = projectRepository.save(project);

        // Usa el STUB FALSO de Organization
        var contractorPersonId = organizationContextFacade.getContractorIdFromOrganizationId(command.organizationId());
        var contractorOrganizationMemberId = organizationContextFacade.getOrganizationMemberIdFromPersonAndOrganizationId(contractorPersonId, command.organizationId());


        // 1. Obtenemos el String de especialidad desde el STUB FALSO de IAM
        String specialtyName = iamContextFacade.getSpecialtyFromPersonId(contractorPersonId);

        // 2. Pasamos el String al comando de nuestro servicio interno
        projectTeamMemberCommandService.handle(
                new CreateProjectTeamMemberCommand(
                        contractorOrganizationMemberId,
                        createdProject.getId(),
                        specialtyName, // String limpio
                        "COORDINATOR"
                )
        );

        return Optional.of(createdProject);
    }

    private ProjectStatus getProjectStatus(ProjectStatuses status) {
        return this.projectStatusRepository.findByName(status)
                .orElseThrow(() -> new IllegalStateException("Project status not found"));
    }

    @Override
    public void handle(DeleteProjectCommand command) {
        var project = projectRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + command.id()));

        projectRepository.delete(project);
    }

    @Override
    public Optional<Project> handle(UpdateProjectCommand command) {
        var result = projectRepository.findById(command.projectId());
        if (result.isEmpty()) throw new IllegalArgumentException("Project doesn't exist");

        var projectToUpdate = result.get();

        ProjectStatus newStatus = projectToUpdate.getStatus();

        if (command.status() != null && !command.status().isBlank()) {
            ProjectStatuses statusEnum = ProjectStatuses.valueOf(command.status().toUpperCase());
            newStatus = getProjectStatus(statusEnum);
        }

        projectToUpdate.updateInformation(
                command.name(),
                command.description(),
                newStatus,
                command.endingDate()
        );

        projectRepository.save(projectToUpdate);

        return Optional.of(projectToUpdate);
    }
}