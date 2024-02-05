package org.molgenis.vipweb.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.AffectedStatus;
import org.molgenis.vipweb.model.constants.Sex;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
@Table("SAMPLE")
public class Sample {
    @Id
    Integer id;
    @NonNull String individualId;
    String paternalId;
    String maternalId;
    @NonNull Boolean proband;
    @NonNull Sex sex;
    @NonNull AffectedStatus affected;
    @NonNull List<SampleHpoTerm> hpoTerms;
}
