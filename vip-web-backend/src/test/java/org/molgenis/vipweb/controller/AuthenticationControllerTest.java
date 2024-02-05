package org.molgenis.vipweb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.dto.UserDetailsDto;
import org.molgenis.vipweb.model.dto.UserDto;
import org.molgenis.vipweb.model.dto.UserSignupDto;
import org.molgenis.vipweb.security.AuthenticationService;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock
    private AuthenticationService authenticationService;
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        authenticationController = new AuthenticationController(authenticationService);
    }

    @Test
    void getUserDetails() {
        UserDetails userDetails = mock(UserDetails.class);
        UserDetailsDto userDetailsDto = mock(UserDetailsDto.class);
        when(authenticationService.mapUserDetails(userDetails)).thenReturn(userDetailsDto);
        assertEquals(userDetailsDto, authenticationController.getUserDetails(userDetails));
    }

    @Test
    void signup() {
        UserSignupDto userSignupDto = mock(UserSignupDto.class);
        UserDto userDto = mock(UserDto.class);
        when(authenticationService.signup(userSignupDto)).thenReturn(userDto);
        assertEquals(userDto, authenticationController.signup(userSignupDto));
    }
}
