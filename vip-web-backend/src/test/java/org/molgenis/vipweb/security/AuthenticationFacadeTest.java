package org.molgenis.vipweb.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.molgenis.vipweb.model.constants.Role.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.molgenis.vipweb.model.constants.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AuthenticationFacadeTest {
    private AuthenticationFacade authenticationFacade;

    @BeforeEach
    void setUp() {
        authenticationFacade = new AuthenticationFacade();
    }

    @Test
    @WithUserDetails
    void getUserId() {
        assertEquals(1, authenticationFacade.getUserId());
    }

    @Test
    @WithUserDetails(value = "admin")
    void isAdmin() {
        assertAll(
                () -> assertTrue(authenticationFacade.isAdmin()),
                () -> assertFalse(authenticationFacade.isVipbot()),
                () -> assertFalse(authenticationFacade.isAnonymousUser()));
    }

    @Test
    @WithUserDetails(value = "vipbot")
    void isVipbot() {
        assertAll(
                () -> assertFalse(authenticationFacade.isAdmin()),
                () -> assertTrue(authenticationFacade.isVipbot()),
                () -> assertFalse(authenticationFacade.isAnonymousUser()));
    }

    @Test
    @WithUserDetails(value = "anonymous")
    void isAnonymousUser() {
        assertAll(
                () -> assertFalse(authenticationFacade.isAdmin()),
                () -> assertFalse(authenticationFacade.isVipbot()),
                () -> assertTrue(authenticationFacade.isAnonymousUser()));
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        public UserDetailsService userDetailsService() {
            return username -> {
                Role role =
                        switch (username) {
                            case "admin" -> ROLE_ADMIN;
                            case "vipbot" -> ROLE_VIPBOT;
                            case "anonymous" -> ROLE_ANONYMOUS;
                            default -> ROLE_USER;
                        };
                return new UserWithId(
                        1,
                        username,
                        "password",
                        true,
                        true,
                        true,
                        true,
                        List.of(new SimpleGrantedAuthority(role.name())));
            };
        }
    }
}
