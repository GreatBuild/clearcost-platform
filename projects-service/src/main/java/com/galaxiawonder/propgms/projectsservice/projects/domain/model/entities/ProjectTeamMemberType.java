package com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.ProjectTeamMemberTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeamMemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private ProjectTeamMemberTypes name;

    public ProjectTeamMemberType(ProjectTeamMemberTypes name) {
        this.name = name;
    }

    public static ProjectTeamMemberType getDefaultMemberType() {
        return new ProjectTeamMemberType(ProjectTeamMemberTypes.COORDINATOR);
    }

    public static ProjectTeamMemberType toProjectTeamMemberTypeFromName(String name) {
        return new ProjectTeamMemberType(ProjectTeamMemberTypes.valueOf(name));
    }

    public static List<ProjectTeamMemberType> validateProjectTeamMemberTypeSet(List<ProjectTeamMemberType> types) {
        return types == null || types.isEmpty()
                ? List.of(getDefaultMemberType())
                : types;
    }

    public String getStringName() {
        return name.name();
    }
}