package org.molgenis.vipweb;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGeneratorImpl implements IdGenerator {
    @Override
    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
