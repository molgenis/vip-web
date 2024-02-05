package org.molgenis.vipweb.service;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.User;
import org.molgenis.vipweb.model.dto.UserCreateDto;
import org.molgenis.vipweb.model.dto.UserDto;
import org.molgenis.vipweb.model.mapper.UserMapper;
import org.molgenis.vipweb.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(UserCreateDto userCreateDto) {
        String encodedPassword = passwordEncoder.encode(userCreateDto.getPassword());
        User user = userMapper.mapUserCreateDtoToUser(userCreateDto, encodedPassword);
        user = userRepository.save(user);
        return userMapper.mapUserToUserDto(user);
    }
}
