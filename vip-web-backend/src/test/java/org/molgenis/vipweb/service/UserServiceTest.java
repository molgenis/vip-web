package org.molgenis.vipweb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.User;
import org.molgenis.vipweb.model.constants.Role;
import org.molgenis.vipweb.model.dto.UserCreateDto;
import org.molgenis.vipweb.model.dto.UserDto;
import org.molgenis.vipweb.model.mapper.UserMapper;
import org.molgenis.vipweb.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void createUser() {
        String username = "username";
        String password = "password";
        Role role = Role.ROLE_USER;
        UserCreateDto userCreateDto =
                UserCreateDto.builder()
                        .username(username)
                        .password(password)
                        .authorities(List.of(role))
                        .build();

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        User user = mock(User.class);
        when(userMapper.mapUserCreateDtoToUser(userCreateDto, encodedPassword)).thenReturn(user);
        User persistedUser = mock(User.class);
        when(userRepository.save(user)).thenReturn(persistedUser);
        UserDto userDto = mock(UserDto.class);
        when(userMapper.mapUserToUserDto(persistedUser)).thenReturn(userDto);

        assertEquals(userDto, userService.createUser(userCreateDto));
    }
}
