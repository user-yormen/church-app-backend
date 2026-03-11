package com.church.anglican.backend.services.auth;

import com.church.anglican.backend.dto.auth.AuthResponse;
import com.church.anglican.backend.dto.auth.AuthUserResponse;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.Permission;
import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.entities.identity.RefreshToken;
import com.church.anglican.backend.repositories.identity.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final long accessTokenTtlSeconds;

    public AuthService(
            AuthenticationManager authenticationManager,
            AppUserRepository appUserRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            @org.springframework.beans.factory.annotation.Value("${app.security.jwt.access-ttl-minutes:15}") long accessTokenTtlMinutes
    ) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.accessTokenTtlSeconds = accessTokenTtlMinutes * 60;
    }

    public AuthResponse login(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return buildResponse(user, accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshToken token = refreshTokenService.verifyRefreshToken(refreshToken);
        AppUser user = token.getUser();

        refreshTokenService.revoke(token);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(user);

        return buildResponse(user, newAccessToken, newRefreshToken);
    }

    private AuthResponse buildResponse(AppUser user, String accessToken, String refreshToken) {
        AuthUserResponse userResponse = new AuthUserResponse();
        userResponse.setUserId(user.getId());
        userResponse.setPersonId(user.getPerson().getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setFirstName(user.getPerson().getFirstName());
        userResponse.setLastName(user.getPerson().getLastName());
        userResponse.setImageUrl(user.getPerson().getImageUrl());
        List<String> roles = user.getRoles().stream()
                .map(AppRole::getName)
                .collect(Collectors.toList());
        userResponse.setRoles(roles);
        userResponse.setRoleIdentifiers(user.getRoles().stream()
                .map(AppRole::getIdentifier)
                .filter(identifier -> identifier != null && !identifier.isBlank())
                .distinct()
                .collect(Collectors.toList()));
        userResponse.setPermissions(user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .collect(Collectors.toList()));

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresInSeconds(accessTokenTtlSeconds);
        response.setUser(userResponse);
        return response;
    }
}
