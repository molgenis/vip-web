package org.molgenis.vipweb.log;

import org.molgenis.vipweb.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserListener extends AbstractRelationalEventListener<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserListener.class);

    @Override
    protected void onAfterSave(AfterSaveEvent<User> event) {
        User user = Objects.requireNonNull(event.getEntity());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("event.user.create:user=%d".formatted(user.getId()));
        }
    }
}
