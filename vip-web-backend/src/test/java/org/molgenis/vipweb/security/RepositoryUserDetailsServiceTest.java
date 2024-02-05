package org.molgenis.vipweb.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.molgenis.vipweb.model.constants.Role.ROLE_ANONYMOUS;
import static org.molgenis.vipweb.model.constants.Role.ROLE_USER;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.Authority;
import org.molgenis.vipweb.model.User;
import org.molgenis.vipweb.model.constants.Role;
import org.molgenis.vipweb.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class RepositoryUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;
    private RepositoryUserDetailsService repositoryUserDetailsService;

    @BeforeEach
    void setUp() {
        repositoryUserDetailsService = new RepositoryUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername() {
        Integer id = 1;
        String username = "username";
        String password = "password";
        Role role = ROLE_USER;

        User user =
                User.builder()
                        .id(id)
                        .username(username)
                        .password(password)
                        .authorities(Set.of(Authority.builder().authority(role).build()))
                        .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserWithId userWithId =
                new UserWithId(
                        id,
                        username,
                        password,
                        true,
                        true,
                        true,
                        true,
                        List.of(new SimpleGrantedAuthority(role.name())));
        assertEquals(userWithId, repositoryUserDetailsService.loadUserByUsername(username));
    }

    @Test
    void loadUserByUsernameNotFound() {
        String username = "username";
        when(userRepository.findByUsername(username)).thenThrow(UsernameNotFoundException.class);
        assertThrows(
                UsernameNotFoundException.class,
                () -> repositoryUserDetailsService.loadUserByUsername(username));
    }

    @Test
    void createAnonymousUser() {
        UserDetails userDetails = RepositoryUserDetailsService.createAnonymousUser();
        assertEquals(
                List.of(new SimpleGrantedAuthority(ROLE_ANONYMOUS.name())),
                new ArrayList<>(userDetails.getAuthorities()));
    }
}
