package org.molgenis.vipweb.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import java.util.Optional;

@RequiredArgsConstructor
class AuditorAwareImpl implements AuditorAware<AggregateReference<User, Integer>> {
    private final AuthenticationFacade authenticationFacade;

    @Override
    public @NonNull Optional<AggregateReference<User, Integer>> getCurrentAuditor() {
        Integer userId = authenticationFacade.getUserId();
        return Optional.of(AggregateReference.to(userId));
    }
}
