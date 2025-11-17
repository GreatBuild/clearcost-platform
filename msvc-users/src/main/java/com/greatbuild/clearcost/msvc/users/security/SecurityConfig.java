package com.greatbuild.clearcost.msvc.users.security;

import com.greatbuild.clearcost.msvc.users.services.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService oauth2UserService;
    private final OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService; // Es tu UserServiceImpl
    private final PasswordEncoder passwordEncoder;     // Es tu BCrypt (de BeansConfig)

    // ¡Inyección por Constructor para todas las dependencias de seguridad!
    public SecurityConfig(CustomOAuth2UserService oauth2UserService,
                          OAuth2AuthenticationSuccessHandler oauth2SuccessHandler,
                          JwtAuthenticationFilter jwtAuthFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.oauth2UserService = oauth2UserService;
        this.oauth2SuccessHandler = oauth2SuccessHandler;
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    // Bean para el AuthenticationManager (lo usa el AuthController para el /login)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }

    // Bean para la Cadena de Filtros de Seguridad (el "guardián" principal)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF (común en APIs stateless)

                // 1. Definimos los permisos de las rutas
                .authorizeHttpRequests(authz -> authz
                        // --- Endpoints PÚBLICOS ---
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/login/**",               // Flujo interno de Spring Security (OAuth2 callback)
                                "/api/auth/users/**",      // Endpoints antiguos de usuarios
                                "/api/users/internal/**",  // Endpoints INTERNOS para comunicación entre microservicios (SIN JWT)
                                "/oauth2/**",              // Flujo interno de Spring Security (OAuth2 inicio)
                                "/v3/api-docs/**",         // Swagger JSON
                                "/swagger-ui/**",          // Swagger UI
                                "/swagger-ui.html"         // Swagger UI
                        ).permitAll() // ¡Permitir a todos!

                        // --- Endpoints PRIVADOS ---
                        .anyRequest().authenticated() // El resto, protegidos
                )

                // 2. Sesiones: STATELESS para peticiones JWT
                // OAuth2 login crea su propia sesión temporal durante el flujo, luego JWT toma control
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Configuración de OAuth2 (Login con Google)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauth2UserService)
                        )
                        .successHandler(oauth2SuccessHandler)
                )

                // 4. Configurar entry point para errores de autenticación
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // 5. ¡¡AÑADIMOS NUESTRO FILTRO JWT!!
                // Le decimos a Spring: "Antes de cada petición, pasa por este filtro"
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
