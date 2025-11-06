package com.greatbuild.clearcost.msvc.projects.clients;

import com.greatbuild.clearcost.msvc.projects.models.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(url = "localhost:8001", name = "msvc-organizations")
public interface OrganizationFeignClient {

    @GetMapping
    List<Organization> findAll();

    @GetMapping("/{id}")
    Organization getById(@PathVariable Long id);
}
