package org.molgenis.vipweb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.dto.UserDetailsDto;
import org.molgenis.vipweb.model.mapper.UserDetailsMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserDetailsMapper userDetailsMapper;
    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try (ServletServerHttpResponse servletServerHttpResponse =
                     new ServletServerHttpResponse(response)) {
            UserDetailsDto userDetailsDto = userDetailsMapper.mapUserDetails((UserDetails) authentication.getPrincipal());
            servletServerHttpResponse.setStatusCode(HttpStatus.OK);
            mappingJackson2HttpMessageConverter.write(
                    userDetailsDto, MediaType.APPLICATION_JSON, servletServerHttpResponse);
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new UncheckedIOException(e);
        }
    }
}
