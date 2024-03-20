package org.molgenis.vipweb.security;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.dto.UserCreateDto;
import org.molgenis.vipweb.model.dto.UserDetailsDto;
import org.molgenis.vipweb.model.dto.UserDto;
import org.molgenis.vipweb.model.dto.UserSignupDto;
import org.molgenis.vipweb.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.molgenis.vipweb.model.constants.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;

    public UserDetailsDto mapUserDetails(UserDetails userDetails) {
        return UserDetailsDto.builder()
                .username(userDetails.getUsername())
                .authorities(
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ANONYMOUS')")
    public UserDto signup(UserSignupDto userSignupDto) {
        String username = userSignupDto.getUsername();
        if (username.length() > 254) {
            throw new TooLongUsernameException();
        }
        String password = userSignupDto.getPassword();
        if (password.length() < 8) {
            throw new WeakPasswordException();
        }

        UserCreateDto userCreateDto =
                UserCreateDto.builder()
                        .username(userSignupDto.getUsername())
                        .password(password)
                        .authorities(List.of(ROLE_USER))
                        .build();
        return userService.createUser(userCreateDto);
    }
}
