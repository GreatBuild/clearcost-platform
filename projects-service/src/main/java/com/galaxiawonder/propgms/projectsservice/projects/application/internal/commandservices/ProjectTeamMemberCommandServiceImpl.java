package com.galaxiawonder.propgms.projectsservice.projects.application.internal.commandservices;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates.ProjectTeamMember;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.CreateProjectTeamMemberCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.DeleteProjectTeamMemberCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.ProjectTeamMemberTypes;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.Specialties;
import com.galaxiawonder.propgms.projectsservice.projects.domain.services.ProjectTeamMemberCommandService;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl.IAMContextFacade;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.acl.OrganizationContextFacade;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories.ProjectTeamMemberRepository;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories.ProjectTeamMemberTypeRepository;
import com.galaxiawonder.propgms.projectsservice.projects.infrastructure.persistence.jpa.repositories.SpecialtyRepository;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.OrganizationMemberId;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.PersonId;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.ProjectId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectTeamMemberCommandServiceImpl implements ProjectTeamMemberCommandService {

    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final IAMContextFacade iamContextFacade;
    private final SpecialtyRepository specialtyRepository;
    private final ProjectTeamMemberTypeRepository projectTeamMemberTypeRepository;
    private final OrganizationContextFacade organizationContextFacade;

    public ProjectTeamMemberCommandServiceImpl(ProjectTeamMemberRepository projectTeamMemberRepository,
                                               IAMContextFacade iamContextFacade,
                                               SpecialtyRepository specialtyRepository,
                                               ProjectTeamMemberTypeRepository projectTeamMemberTypeRepository,
                                               OrganizationContextFacade organizationContextFacade) {
        this.projectTeamMemberRepository = projectTeamMemberRepository;
        this.iamContextFacade = iamContextFacade;
        this.specialtyRepository = specialtyRepository;
        this.projectTeamMemberTypeRepository = projectTeamMemberTypeRepository;
        this.organizationContextFacade = organizationContextFacade;
    }

    @Override
    public Optional<ProjectTeamMember> handle(CreateProjectTeamMemberCommand command) {
        var existingProjectTeamMember = projectTeamMemberRepository.findByOrganizationMemberIdAndProjectId(
                new OrganizationMemberId(command.organizationMemberId()),
                new ProjectId(command.projectId())
        );

        if (existingProjectTeamMember.isPresent()) {
            throw new IllegalArgumentException("Project team member already exists...");
        }

        // Usa el STUB FALSO de Organization
        var personId = organizationContextFacade.getPersonIdFromOrganizationMemberId(
                command.organizationMemberId()
        );

        var projectTeamMember = new ProjectTeamMember(command);

        // Usa el STUB FALSO de IAM
        var personInformation = iamContextFacade.getProfileDetailsById(personId);
        if (personInformation == null) {
            throw new IllegalArgumentException("Person with ID " + personId + " not found (from fake facade)");
        }

        // Busca la especialidad en NUESTRA BD usando el String del comando
        var specialty = specialtyRepository.findByName(Specialties.valueOf(command.specialty()))
                .orElseThrow(() -> new IllegalArgumentException("Specialty not found: " + command.specialty()));
        projectTeamMember.assignSpecialty(specialty);

        // Busca el tipo en NUESTRA BD usando el String del comando
        var type = projectTeamMemberTypeRepository.findByName(ProjectTeamMemberTypes.valueOf(command.memberType()))
                .orElseThrow(() -> new IllegalArgumentException("Project team member type not found: " + command.memberType()));
        projectTeamMember.assignTeamMemberType(type);

        projectTeamMember.setPersonalInformation(new PersonId(personId), personInformation.name(), personInformation.email());

        var createdTeamMember = projectTeamMemberRepository.save(projectTeamMember);
        return Optional.of(createdTeamMember);
    }

    @Override
    public void handle(DeleteProjectTeamMemberCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("DeleteProjectTeamMemberCommand must not be null");
        }
        var projectTeamMember = projectTeamMemberRepository.findById(command.teamMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Project team member not found with ID: " + command.teamMemberId()));
        projectTeamMemberRepository.delete(projectTeamMember);
    }
}