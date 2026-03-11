package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.entities.identity.Permission;
import com.church.anglican.backend.repositories.identity.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;

    public UserServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(role -> Stream.concat(
                        Stream.of(
                                role.getName(),
                                role.getIdentifier() != null && !role.getIdentifier().isBlank()
                                        ? "IDENTIFIER_" + role.getIdentifier()
                                        : null
                        ).filter(value -> value != null && !value.isBlank()),
                        role.getPermissions().stream().map(Permission::getName)
                ))
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(user.getUsername(), user.getPasswordHash(), user.isEnabled(), true, true, true, authorities);
    }
}
