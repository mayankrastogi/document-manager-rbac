package com.mayankrastogi.cs587.documentmanager.services;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequestScope
public class SecurityService {

    @Getter
    private Set<String> allPermissions = allPermissions();

    public boolean hasPermission(String permissionId) {
        return allPermissions.contains(permissionId);
    }

    private Set<String> allPermissions() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toUnmodifiableSet());
    }
}
