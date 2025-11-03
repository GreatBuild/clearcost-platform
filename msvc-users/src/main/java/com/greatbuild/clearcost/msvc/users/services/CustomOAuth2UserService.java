package com.greatbuild.clearcost.msvc.users.services;

import com.greatbuild.clearcost.msvc.users.models.entities.AuthProvider;
import com.greatbuild.clearcost.msvc.users.models.entities.Role;
import com.greatbuild.clearcost.msvc.users.models.entities.User;
import com.greatbuild.clearcost.msvc.users.repositories.RoleRepository;
import com.greatbuild.clearcost.msvc.users.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Obtenemos el OAuth2User de Google
        OAuth2User oauthUser = super.loadUser(userRequest);

        // 2. Extraemos el email
        String email = oauthUser.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("No se pudo obtener el email de Google.");
        }

        // 3. Procesamos el usuario (guardar o actualizar en BD)
        User user;
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getProvider() == AuthProvider.LOCAL) {
                user.setProvider(AuthProvider.GOOGLE);
                user.setPassword(null);
            }
            user = updateExistingUser(user, oauthUser);
        } else {
            user = registerNewUserFromGoogle(oauthUser);
        }

        // 4. Creamos un OAuth2User personalizado con el EMAIL como nombre principal
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        // Este OAuth2User tiene el EMAIL como getName(), no el 'sub' de Google
        return new DefaultOAuth2User(
                authorities,
                oauthUser.getAttributes(),
                "email" // ¡Esto hace que getName() retorne el email!
        );
    }

    private User registerNewUserFromGoogle(OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        Role pendingRole = roleRepository.findByName("ROLE_PENDING_SELECTION")
                .orElseThrow(() -> new RuntimeException("El rol ROLE_PENDING_SELECTION no existe en la BD"));

        User user = new User();
        user.setEmail(email);
        user.setProvider(AuthProvider.GOOGLE);
        user.setRoles(new HashSet<>(Set.of(pendingRole)));
        
        // Usamos valores por defecto si Google no proporciona nombres
        user.setFirstName((firstName != null && !firstName.isBlank()) ? firstName : "Usuario");
        user.setLastName((lastName != null && !lastName.isBlank()) ? lastName : "Google");

        return userRepository.saveAndFlush(user);
    }

    private User updateExistingUser(User existingUser, OAuth2User oauthUser) {
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        if (firstName != null && !firstName.isBlank()) {
            existingUser.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isBlank()) {
            existingUser.setLastName(lastName);
        }

        return userRepository.saveAndFlush(existingUser);
    }
}