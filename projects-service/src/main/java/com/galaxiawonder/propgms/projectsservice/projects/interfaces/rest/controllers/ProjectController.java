package com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.controllers;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.aggregates.Project;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.commands.DeleteProjectCommand;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.queries.GetAllProjectsByContractingEntityIdQuery;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.queries.GetAllProjectsByTeamMemberPersonIdQuery;
import com.galaxiawonder.propgms.projectsservice.projects.domain.model.queries.GetProjectByProjectIdQuery;
import com.galaxiawonder.propgms.projectsservice.projects.domain.services.ProjectCommandService;
import com.galaxiawonder.propgms.projectsservice.projects.domain.services.ProjectQueryService;
import com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.assemblers.CreateProjectCommandFromResourceAssembler;
import com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.assemblers.ProjectResourceFromEntityAssembler;
import com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.assemblers.UpdateProjectCommandFromResourceAssembler;
import com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.resources.CreateProjectResource;
import com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.resources.ProjectResource;
import com.galaxiawonder.propgms.projectsservice.projects.interfaces.rest.resources.UpdateProjectResource;
import com.galaxiawonder.propgms.projectsservice.shared.interfaces.rest.resources.GenericMessageResource;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/projects", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Projects", description = "Endpoints for Projects")
public class ProjectController {
    private final ProjectCommandService projectCommandService;
    private final ProjectQueryService projectQueryService;

    /**
     * Constructor for ProjectController.
     * @param projectCommandService Project command service
     * @param projectQueryService Project query service}
     */
    ProjectController(ProjectCommandService projectCommandService,
                      ProjectQueryService projectQueryService) {
        this.projectCommandService = projectCommandService;
        this.projectQueryService = projectQueryService;
    }

    /**
     * Creates a Project
     * @param resource CreateProjectResource containing the required params
     * @return ResponseEntity with the created project resource
     */
    @PostMapping
    public ResponseEntity<ProjectResource> createProject(@RequestBody CreateProjectResource resource) {
        Optional<Project> project = projectCommandService
                .handle(CreateProjectCommandFromResourceAssembler.toCommandFromResource(resource));

        return project
                .map(source -> new ResponseEntity<>(ProjectResourceFromEntityAssembler.toResourceFromEntity(source), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Get project by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResource> getProjectById(@PathVariable Long projectId) {
        var query = new GetProjectByProjectIdQuery(projectId);
        Optional<Project> project = projectQueryService.handle(query);
        return project.map(source -> new ResponseEntity<>(ProjectResourceFromEntityAssembler.toResourceFromEntity(source), HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get project by contracting entity Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Projects not found")
    })
    @GetMapping("by-contracting-entity-id/{contractingEntityId}")
    public ResponseEntity<List<ProjectResource>> getProjectsByContractingEntityId(@PathVariable Long contractingEntityId) {
        var query = new GetAllProjectsByContractingEntityIdQuery(contractingEntityId);
        Optional<List<Project>> projects = projectQueryService.handle(query);

        List<ProjectResource> resources = projects.map(source -> source.stream()
                        .map(ProjectResourceFromEntityAssembler::toResourceFromEntity)
                        .toList())
                .orElseGet(List::of);

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @Operation(summary = "Get projects by person ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found or is not assigned to any projects")
    })
    @GetMapping("/by-team-member-person-id/{id}")
    public ResponseEntity<List<ProjectResource>> getAllProjectsByTeamMemberPersonId(
            @Parameter(description = "ID of the person", required = true)
            @PathVariable("id") Long personId
    ) {
        List<Project> projects = projectQueryService.handle(
                new GetAllProjectsByTeamMemberPersonIdQuery(personId)
        );

        List<ProjectResource> resources = projects.stream()
                .map(ProjectResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete project by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project deleted successfully"), // Cambiado de 204 para devolver mensaje
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<GenericMessageResource> deleteProject(@PathVariable Long id) {
        var deleteProjectCommand = new DeleteProjectCommand(id);
        projectCommandService.handle(deleteProjectCommand);
        return ResponseEntity.ok(new GenericMessageResource("Project successfully deleted"));
    }

    @PatchMapping("{id}")
    @Operation(summary = "Update project by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<GenericMessageResource> updateProject(
            @PathVariable Long id,
            @RequestBody UpdateProjectResource resource
    ) {
        projectCommandService.handle(UpdateProjectCommandFromResourceAssembler.toCommandFromResource(id, resource));
        return ResponseEntity.ok(new GenericMessageResource("Project with given ID successfully updated"));
    }
}