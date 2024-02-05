package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.FilterTreeType;
import org.springframework.data.annotation.*;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
@Table("FILTER_TREE")
public class FilterTree {
    @Id
    Integer id;
    @NonNull String name;
    String description;
    @NonNull AggregateReference<File, Integer> file;
    @NonNull FilterTreeType type;
    @NonNull List<FilterTreeClass> classes;
    boolean isPublic;
    // workaround for https://github.com/spring-projects/spring-data-relational/issues/1694
    @CreatedBy
    AggregateReference<User, Integer> createdBy;
    @CreatedDate
    Instant creationDate;
    @LastModifiedBy
    AggregateReference<User, Integer> lastModifiedBy;
    @LastModifiedDate
    Instant lastModifiedDate;
}
