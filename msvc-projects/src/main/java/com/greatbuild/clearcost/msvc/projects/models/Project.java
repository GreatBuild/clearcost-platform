package com.greatbuild.clearcost.msvc.projects.models;

public class Project {

    private Organization organization;
    private String projectName;
    private String description;

    public Project(Organization organization, String projectName, String description) {
        this.organization = organization;
        this.projectName = projectName;
        this.description = description;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
