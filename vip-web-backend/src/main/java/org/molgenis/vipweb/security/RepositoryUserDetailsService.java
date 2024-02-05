package org.molgenis.vipweb.security;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.User;
import org.molgenis.vipweb.model.constants.Role;
import org.molgenis.vipweb.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryUserDetailsService implements UserDetailsService {
    // depending on UserService would introduce a circular dependency issue
    private final UserRepository userRepository;

    public static UserDetails createAnonymousUser() {
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        List<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(Role.ROLE_ANONYMOUS.name()));

        return new org.springframework.security.core.userdetails.User(
                "anonymous",
                "anonymous",
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("User '%s' not found".formatted(username)));

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        List<? extends GrantedAuthority> authorities =
                user.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority().name()))
                        .toList();

        return new UserWithId(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities);
    }
}
