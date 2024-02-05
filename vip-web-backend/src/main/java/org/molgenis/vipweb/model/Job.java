package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.Assembly;
import org.molgenis.vipweb.model.constants.JobStatus;
import org.molgenis.vipweb.model.constants.SequencingMethod;
import org.springframework.data.annotation.*;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import java.util.Set;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
@Table("JOB")
public class Job {
    @Id
    Integer id;
    @NonNull String name;
    @NonNull AggregateReference<Vcf, Integer> vcf;
    @NonNull JobStatus status;
    @NonNull SequencingMethod sequencingMethod;
    @NonNull Assembly assembly;
    @NonNull List<Sample> samples;
    @NonNull AggregateReference<FilterTree, Integer> variantFilterTree;
    @NonNull Set<VariantFilterClass> variantFilterClasses;
    @NonNull AggregateReference<FilterTree, Integer> sampleFilterTree;
    @NonNull Set<SampleFilterClass> sampleFilterClasses;
    Report report;
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
