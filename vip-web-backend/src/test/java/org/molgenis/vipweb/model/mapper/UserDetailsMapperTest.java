package org.molgenis.vipweb.model.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipweb.model.dto.UserDetailsDto;
import org.molgenis.vipweb.security.UserWithId;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDetailsMapperTest {

  private UserDetailsMapper userDetailsMapper;

  @BeforeEach
  void setUp() {
    userDetailsMapper = new UserDetailsMapper();
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
    assertEquals(userDetailsDto, userDetailsMapper.mapUserDetails(userDetails));
  }
}
