package com.greatbuild.clearcost.msvc.users.services;

import com.greatbuild.clearcost.msvc.users.models.dtos.RegisterRequestDTO;
import com.greatbuild.clearcost.msvc.users.models.entities.AuthProvider;
import com.greatbuild.clearcost.msvc.users.models.entities.Role;
import com.greatbuild.clearcost.msvc.users.models.entities.User;
import com.greatbuild.clearcost.msvc.users.repositories.RoleRepository;
import com.greatbuild.clearcost.msvc.users.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Lógica de Negocio (para el Controller) ---

    @Override
    @Transactional
    public User registerNewUser(RegisterRequestDTO registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Error: El email ya está en uso.");
        }
        Role userRole = roleRepository.findByName(registerRequest.getRoleName())
                .orElseThrow(() -> new RuntimeException("Error: Rol no válido."));
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setRoles(new HashSet<>(Set.of(userRole)));
        user.setProvider(AuthProvider.LOCAL);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserRole(String email, String newRoleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        Role newRole = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new RuntimeException("Error: Rol no válido: " + newRoleName));

        user.setRoles(new HashSet<>(Set.of(newRole)));
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // --- Lógica para Spring Security (Usada por el JwtFilter) ---
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("[DEBUG] loadUserByUsername: Buscando usuario por email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[DEBUG] loadUserByUsername: Usuario no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        // ¡ARREGLADO! Aceptamos usuarios de LOCAL y GOOGLE
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword() != null ? user.getPassword() : "", // Maneja 'null' para Google
                authorities
        );
    }
}
