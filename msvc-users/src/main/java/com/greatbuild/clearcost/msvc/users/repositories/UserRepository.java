package com.greatbuild.clearcost.msvc.users.repositories;

import com.greatbuild.clearcost.msvc.users.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
