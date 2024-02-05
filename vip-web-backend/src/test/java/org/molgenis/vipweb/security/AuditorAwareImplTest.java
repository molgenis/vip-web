package org.molgenis.vipweb.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditorAwareImplTest {
    @Mock
    AuthenticationFacade authenticationFacade;
    private AuditorAwareImpl auditorAwareImpl;

    @BeforeEach
    void setUp() {
        auditorAwareImpl = new AuditorAwareImpl(authenticationFacade);
    }

    @Test
    void getCurrentAuditor() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        assertEquals(Optional.of(AggregateReference.to(userId)), auditorAwareImpl.getCurrentAuditor());
    }
}
