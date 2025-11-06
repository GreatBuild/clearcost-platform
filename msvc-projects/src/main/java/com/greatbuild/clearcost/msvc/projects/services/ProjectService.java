package com.greatbuild.clearcost.msvc.projects.services;

import com.greatbuild.clearcost.msvc.projects.models.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {

    List<Project> findAll();

    Optional<Project> findById(Long id);
}
