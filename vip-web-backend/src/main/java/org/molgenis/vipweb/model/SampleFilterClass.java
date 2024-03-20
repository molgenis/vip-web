package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
@Table("SAMPLE_FILTER_CLASS")
public class SampleFilterClass {
    @Id
    Integer id;
    @NonNull AggregateReference<FilterTreeClass, Integer> filterTreeClass;
}
