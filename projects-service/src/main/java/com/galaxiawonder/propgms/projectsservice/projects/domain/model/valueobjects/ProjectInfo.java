package com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects;

import lombok.Getter;

@Getter
public class ProjectInfo{
    public Long Id;
    public String Name;
    public String Status;
    public ProjectInfo(Long Id, String Name, String Status) {
        this.Id = Id;
        this.Name = Name;
        this.Status = Status;
    }
}