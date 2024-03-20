package org.molgenis.vipweb.security;

import org.molgenis.vipweb.model.constants.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public Integer getUserId() {
        Object principal = getAuthentication().getPrincipal();
        if (!(principal instanceof UserWithId)) {
            throw new UnsupportedOperationException(
                    "principal is of type %s instead of %s"
                            .formatted(principal.getClass().getName(), UserWithId.class.getName()));
        }
        return ((UserWithId) principal).getId();
    }

    public boolean isAdmin() {
        return hasRole(Role.ROLE_ADMIN);
    }

    public boolean isVipbot() {
        return hasRole(Role.ROLE_VIPBOT);
    }

    public boolean isAnonymousUser() {
        return hasRole(Role.ROLE_ANONYMOUS);
    }

    private boolean hasRole(Role role) {
        Authentication authentication = getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role.name()));
    }
}
