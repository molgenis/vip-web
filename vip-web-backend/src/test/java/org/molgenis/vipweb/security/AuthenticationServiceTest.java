package org.molgenis.vipweb.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vipweb.model.constants.Role.ROLE_USER;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.dto.UserCreateDto;
import org.molgenis.vipweb.model.dto.UserDetailsDto;
import org.molgenis.vipweb.model.dto.UserDto;
import org.molgenis.vipweb.model.dto.UserSignupDto;
import org.molgenis.vipweb.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserService userService;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(userService);
    }

    @Test
    void mapUserDetails() {
        String username = "username";
        String authority = "authority";
        UserDetails userDetails =
                new UserWithId(
                        1,
                        username,
                        "password",
                        true,
                        true,
                        true,
                        true,
                        List.of(new SimpleGrantedAuthority(authority)));
        UserDetailsDto userDetailsDto =
                UserDetailsDto.builder().username(username).authorities(List.of(authority)).build();
        assertEquals(userDetailsDto, authenticationService.mapUserDetails(userDetails));
    }

    @Test
    void signup() {
        String username = "username";
        String password = "x$@#4faA";

        UserCreateDto userCreateDto =
                UserCreateDto.builder()
                        .username(username)
                        .password(password)
                        .authorities(List.of(ROLE_USER))
                        .build();
        UserDto userDto = mock(UserDto.class);
        when(userService.createUser(userCreateDto)).thenReturn(userDto);

        UserSignupDto userSignupDto =
                UserSignupDto.builder().username(username).password(password).build();
        assertEquals(userDto, authenticationService.signup(userSignupDto));
    }

    @Test
    void signupWeakPassword() {
        String username = "username";
        String password = "x";
        UserSignupDto userSignupDto =
                UserSignupDto.builder().username(username).password(password).build();
        assertThrows(WeakPasswordException.class, () -> authenticationService.signup(userSignupDto));
    }
}
