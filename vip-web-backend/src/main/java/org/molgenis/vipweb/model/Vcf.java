package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.Assembly;
import org.springframework.data.annotation.*;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
@Table("VCF")
public class Vcf {
    @Id
    Integer id;
    @NonNull AggregateReference<File, Integer> file;
    @NonNull List<VcfSample> samples;
    Assembly assembly;
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
