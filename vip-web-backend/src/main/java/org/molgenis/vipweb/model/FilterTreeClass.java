package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
@Table("FILTER_TREE_CLASS")
public class FilterTreeClass {
    @Id
    Integer id;
    @NonNull String name;
    String description;
    boolean isDefaultFilter;
}
