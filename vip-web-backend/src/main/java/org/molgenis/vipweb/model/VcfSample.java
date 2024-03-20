package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.relational.core.mapping.Table;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Table("VCF_SAMPLE")
@Builder(toBuilder = true)
public class VcfSample {
    @NonNull String name;
}
