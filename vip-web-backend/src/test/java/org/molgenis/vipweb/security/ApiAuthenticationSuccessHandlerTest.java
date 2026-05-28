package org.molgenis.vipweb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.dto.UserDetailsDto;
import org.molgenis.vipweb.model.mapper.UserDetailsMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import tools.jackson.databind.ObjectMapper;

import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiAuthenticationSuccessHandlerTest {
    @Mock
    private UserDetailsMapper userDetailsMapper;
    @Mock
    private ObjectMapper objectMapper;
    private ApiAuthenticationSuccessHandler apiAuthenticationSuccessHandler;

    @BeforeEach
    void setUp() {
        apiAuthenticationSuccessHandler =
                new ApiAuthenticationSuccessHandler(userDetailsMapper, objectMapper);
    }

    @Test
    void onAuthenticationSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication =
                when(mock(Authentication.class).getPrincipal()).thenReturn(userDetails).getMock();

        UserDetailsDto userDetailsDto = mock(UserDetailsDto.class);
        when(userDetailsMapper.mapUserDetails(userDetails)).thenReturn(userDetailsDto);

        apiAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertAll(
                () -> verify(response).setStatus(HttpStatus.OK.value()),
                () ->
                        verify(objectMapper)
                                .writeValue((OutputStream) any(), eq(userDetailsDto)));
    }
}
