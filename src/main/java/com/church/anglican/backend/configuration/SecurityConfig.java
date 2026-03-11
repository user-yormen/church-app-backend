package com.church.anglican.backend.configuration;

import com.church.anglican.backend.filters.JwtAuthenticationFilter;
import com.church.anglican.backend.services.auth.JwtService;
import com.church.anglican.backend.services.identity.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String BACKOFFICE_SCOPE = "IDENTIFIER_BACKOFFICE";
    private static final String CHURCH_SCOPE = "IDENTIFIER_CHURCH";

    private final UserService userDetailsService;
    private final JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/bootstrap-admin"
                        ).permitAll()
                        .requestMatchers(POST, "/api/v1/users/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_USER_MANAGE", CHURCH_SCOPE, "ROLE_ASSIGN")
                        .requestMatchers(PUT, "/api/v1/users/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_USER_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/users/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_USER_MANAGE")
                        .requestMatchers(POST, "/api/v1/person-roles/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ROLE_ASSIGN")
                        .requestMatchers(PUT, "/api/v1/person-roles/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ROLE_ASSIGN")
                        .requestMatchers(DELETE, "/api/v1/person-roles/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ROLE_ASSIGN")
                        .requestMatchers("/api/v1/roles/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_ROLE_MANAGE")
                        .requestMatchers(POST, "/api/v1/churches/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_CHURCH_MANAGE")
                        .requestMatchers(PUT, "/api/v1/churches/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_CHURCH_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/churches/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_CHURCH_MANAGE")
                        .requestMatchers(POST, "/api/v1/people/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "PEOPLE_MANAGE")
                        .requestMatchers(PUT, "/api/v1/people/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "PEOPLE_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/people/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "PEOPLE_MANAGE")
                        .requestMatchers(POST, "/api/v1/groups/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "GROUP_MANAGE")
                        .requestMatchers(PUT, "/api/v1/groups/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "GROUP_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/groups/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "GROUP_MANAGE")
                        .requestMatchers(POST, "/api/v1/elections/*/votes").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ELECTION_VOTE", "ELECTION_MANAGE")
                        .requestMatchers(POST, "/api/v1/elections/*/candidates").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ELECTION_MANAGE")
                        .requestMatchers(POST, "/api/v1/elections/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ELECTION_MANAGE")
                        .requestMatchers(PUT, "/api/v1/elections/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ELECTION_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/elections/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ELECTION_MANAGE")
                        .requestMatchers(POST, "/api/v1/shares/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_SHARE_MANAGE")
                        .requestMatchers(PUT, "/api/v1/shares/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_SHARE_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/shares/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_SHARE_MANAGE")
                        .requestMatchers(POST, "/api/v1/achievements/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ACHIEVEMENT_MANAGE")
                        .requestMatchers(PUT, "/api/v1/achievements/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ACHIEVEMENT_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/achievements/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "ACHIEVEMENT_MANAGE")
                        .requestMatchers(POST, "/api/v1/memberships/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "MEMBERSHIP_MANAGE")
                        .requestMatchers(PUT, "/api/v1/memberships/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "MEMBERSHIP_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/memberships/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "MEMBERSHIP_MANAGE")
                        .requestMatchers(POST, "/api/v1/collections/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "FINANCE_MANAGE")
                        .requestMatchers(PUT, "/api/v1/collections/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "FINANCE_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/collections/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "FINANCE_MANAGE")
                        .requestMatchers(POST, "/api/v1/counting-sessions/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "FINANCE_MANAGE")
                        .requestMatchers(PUT, "/api/v1/counting-sessions/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "FINANCE_MANAGE")
                        .requestMatchers(DELETE, "/api/v1/counting-sessions/**").hasAnyAuthority("ADMIN", CHURCH_SCOPE, "FINANCE_MANAGE")
                        .requestMatchers(GET, "/api/v1/shares/**").hasAnyAuthority("ADMIN", BACKOFFICE_SCOPE, "BACKOFFICE_SHARE_MANAGE")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider(passwordEncoder()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.applyPermitDefaultValues();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
