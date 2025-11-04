package com.greatbuild.clearcost.msvc.users.services;

import com.greatbuild.clearcost.msvc.users.models.dtos.RegisterRequestDTO;
import com.greatbuild.clearcost.msvc.users.models.entities.User;

import java.util.Optional;

public interface UserService {
    User registerNewUser(RegisterRequestDTO registerRequest);
    Optional<User> findByEmail(String email);
    User updateUserRole(String email, String newRoleName);
    Optional<User> findById(Long id);
}
