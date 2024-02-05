package org.molgenis.vipweb.controller;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.Error;
import org.molgenis.vipweb.model.dto.UserDetailsDto;
import org.molgenis.vipweb.model.dto.UserDto;
import org.molgenis.vipweb.model.dto.UserSignupDto;
import org.molgenis.vipweb.security.AuthenticationService;
import org.molgenis.vipweb.security.TooLongUsernameException;
import org.molgenis.vipweb.security.WeakPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * {@link org.molgenis.vipweb.security.SecurityConfig} handles login via /api/auth/login and logout
 * via /api/auth/logout
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController implements ApiController {
    private final AuthenticationService authenticationService;

    @GetMapping("/me")
    public UserDetailsDto getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        return authenticationService.mapUserDetails(userDetails);
    }

    @PostMapping("/signup")
    public UserDto signup(@RequestBody UserSignupDto userSignupDto) {
        return authenticationService.signup(userSignupDto);
    }

    @ExceptionHandler(value = TooLongUsernameException.class)
    public ResponseEntity<Error> handleTooLongUsernameException() {
        return new ResponseEntity<>(
                Error.from("Username must be less than 255 characters"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = WeakPasswordException.class)
    public ResponseEntity<Error> handleWeakPasswordException() {
        return new ResponseEntity<>(
                Error.from("Password must be at least 8 characters long"), HttpStatus.BAD_REQUEST);
    }
}
