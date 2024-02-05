package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
@Table("USER")
public class User {
    @Id
    Integer id;
    @NonNull String username;
    @NonNull String password;
    @NonNull Set<Authority> authorities;
}
