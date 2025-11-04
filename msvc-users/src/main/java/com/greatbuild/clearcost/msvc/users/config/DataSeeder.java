package com.greatbuild.clearcost.msvc.users.config;

import com.greatbuild.clearcost.msvc.users.models.entities.Role;
import com.greatbuild.clearcost.msvc.users.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    // ¡Usamos inyección por constructor!
    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Sembrar los roles principales del sistema
        // ROLE_CLIENT: Cliente que contrata servicios de construcción
        // ROLE_WORKER: Trabajador de construcción que puede crear/unirse a organizaciones
        // ROLE_PENDING_SELECTION: Usuario OAuth2 que aún no ha seleccionado su rol
        seedRole("ROLE_CLIENT");
        seedRole("ROLE_WORKER");
        seedRole("ROLE_PENDING_SELECTION");
    }

    private void seedRole(String roleName) {
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            Role newRole = new Role();
            newRole.setName(roleName);
            roleRepository.save(newRole);
            System.out.println("Rol sembrado: " + roleName);
        }
    }
}