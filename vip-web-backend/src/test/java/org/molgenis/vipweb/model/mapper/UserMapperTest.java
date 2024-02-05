package org.molgenis.vipweb.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipweb.model.Authority;
import org.molgenis.vipweb.model.User;
import org.molgenis.vipweb.model.constants.Role;
import org.molgenis.vipweb.model.dto.UserCreateDto;
import org.molgenis.vipweb.model.dto.UserDto;

class UserMapperTest {
  private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    userMapper = new UserMapper();
  }

  @Test
  void mapUserToUserDto() {
    Integer id = 1;
    String username = "username";
    String password = "password";
    Authority authority =
        when(mock(Authority.class).getAuthority()).thenReturn(Role.ROLE_ADMIN).getMock();
    User user =
        User.builder()
            .id(id)
            .username(username)
            .password(password)
            .authorities(Set.of(authority))
            .build();
    UserDto userDto =
        UserDto.builder().id(id).username(username).authorities(List.of("ROLE_ADMIN")).build();
    assertEquals(userDto, userMapper.mapUserToUserDto(user));
  }

  @Test
  void mapUserCreateDtoToUser() {
    String username = "username";
    String password = "password";
    String encodedPassword = "encodedPassword";
    Role authority = Role.ROLE_ADMIN;
    UserCreateDto userCreateDto =
        UserCreateDto.builder()
            .username(username)
            .password(password)
            .authorities(List.of(authority))
            .build();
    User user =
        User.builder()
            .username(username)
            .password(encodedPassword)
            .authorities(Set.of(Authority.builder().authority(authority).build()))
            .build();
    assertEquals(user, userMapper.mapUserCreateDtoToUser(userCreateDto, encodedPassword));
  }
}
