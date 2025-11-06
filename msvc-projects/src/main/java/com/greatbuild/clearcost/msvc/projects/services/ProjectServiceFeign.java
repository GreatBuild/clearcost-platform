package com.greatbuild.clearcost.msvc.projects.services;

import com.greatbuild.clearcost.msvc.projects.clients.OrganizationFeignClient;
import com.greatbuild.clearcost.msvc.projects.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceFeign implements ProjectService {

    @Autowired
    private OrganizationFeignClient client;

    @Override
    public List<Project> findAll() {
        return client.findAll().stream().map(organization -> {

            return new Project(organization, "Project 1", "Description 1");
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<Project> findById(Long id) {
        return Optional.of(new Project(client.getById(id), "Project 1", "Description 1"));
    }
}
