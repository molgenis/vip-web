package org.molgenis.vipweb.populate;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.VipWebProperties;
import org.molgenis.vipweb.model.constants.Role;
import org.molgenis.vipweb.model.dto.UserCreateDto;
import org.molgenis.vipweb.model.dto.UserDto;
import org.molgenis.vipweb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserPopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPopulator.class);

    private final UserService userService;

    @Transactional
    public UserDto populate(VipWebProperties.Initializer.Users users) {
        VipWebProperties.Initializer.Users.Admin admin = users.admin();
        UserDto adminUser = populate("admin", admin.username(), admin.password(), Role.ROLE_ADMIN);

        VipWebProperties.Initializer.Users.Vipbot vipbot = users.vipbot();
        populate("vipbot", vipbot.username(), vipbot.password(), Role.ROLE_VIPBOT);

        return adminUser;
    }

    public UserDto populate(String key, String username, String password, Role role) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException(
                    "missing required value for property 'vipweb.initializer.users.%s.username'"
                            .formatted(key));
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "missing required value for property 'vipweb.initializer.users.%s.password'"
                            .formatted(key));
        }

        UserCreateDto user =
                UserCreateDto.builder()
                        .username(username)
                        .password(password)
                        .authorities(List.of(role))
                        .build();
        LOGGER.info("creating %s user".formatted(key));
        return userService.createUser(user);
    }
}
