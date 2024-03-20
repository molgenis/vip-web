package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class Error {
    @NonNull ErrorDetails error;

    public static Error from(String message) {
        return Error.builder().error(ErrorDetails.builder().message(message).build()).build();
    }

    @Value
    @Builder(toBuilder = true)
    public static class ErrorDetails {
        @NonNull String message;
    }
}
