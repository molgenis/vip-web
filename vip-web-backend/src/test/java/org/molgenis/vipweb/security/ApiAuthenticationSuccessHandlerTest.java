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
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiAuthenticationSuccessHandlerTest {
  @Mock private UserDetailsMapper userDetailsMapper;
  @Mock private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
  private ApiAuthenticationSuccessHandler apiAuthenticationSuccessHandler;

  @BeforeEach
  void setUp() {
    apiAuthenticationSuccessHandler =
        new ApiAuthenticationSuccessHandler(userDetailsMapper, mappingJackson2HttpMessageConverter);
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
            verify(mappingJackson2HttpMessageConverter)
                .write(eq(userDetailsDto), eq(MediaType.APPLICATION_JSON), any()));
  }
}
