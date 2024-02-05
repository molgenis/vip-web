package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class UserDetailsDto {
    @NonNull String username;
    // do not expose password field
    @NonNull List<String> authorities;
}
