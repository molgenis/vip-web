package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.Role;
import org.springframework.data.relational.core.mapping.Table;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
@Table("AUTHORITY")
public class Authority {
    @NonNull Role authority;
}
