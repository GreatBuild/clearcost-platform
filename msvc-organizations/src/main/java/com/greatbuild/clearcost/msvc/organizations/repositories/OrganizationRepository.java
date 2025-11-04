package com.greatbuild.clearcost.msvc.organizations.repositories;

import com.greatbuild.clearcost.msvc.organizations.models.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByRuc(String ruc);
}
