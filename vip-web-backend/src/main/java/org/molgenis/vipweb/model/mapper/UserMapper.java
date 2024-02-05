package org.molgenis.vipweb.model.mapper;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.Authority;
import org.molgenis.vipweb.model.User;
import org.molgenis.vipweb.model.constants.Role;
import org.molgenis.vipweb.model.dto.UserCreateDto;
import org.molgenis.vipweb.model.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private static Set<Authority> rolesToAuthorities(List<Role> roles) {
        return roles.stream()
                .map(role -> Authority.builder().authority(role).build())
                .collect(Collectors.toSet());
    }

    public UserDto mapUserToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .authorities(
                        user.getAuthorities().stream()
                                .map(authority -> authority.getAuthority().toString())
                                .toList())
                .build();
    }

    public User mapUserCreateDtoToUser(UserCreateDto userCreateDto, String encodedPassword) {
        return User.builder()
                .username(userCreateDto.getUsername())
                .password(encodedPassword)
                .authorities(rolesToAuthorities(userCreateDto.getAuthorities()))
                .build();
    }
}
