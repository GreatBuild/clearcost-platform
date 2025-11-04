package com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities;

import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.ProjectStatuses;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ProjectStatuses name;

    public ProjectStatus(ProjectStatuses name) {
        this.name = name;
    }

    public static ProjectStatus getDefaultStatus() {
        return new ProjectStatus(ProjectStatuses.BASIC_STUDIES);
    }

    public static ProjectStatus toProjectStatusFromName(String name) {
        return new ProjectStatus(ProjectStatuses.valueOf(name));
    }

    public static List<ProjectStatus> validateStatusSet(List<ProjectStatus> statuses) {
        return statuses == null || statuses.isEmpty()
                ? List.of(getDefaultStatus())
                : statuses;
    }

    public String getStringName() {
        return name.name();
    }
}