package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.Assembly;
import org.molgenis.vipweb.model.constants.SequencingMethod;

import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class JobCreateDto {
    @NonNull String name;
    @NonNull Integer vcfId;
    @NonNull SequencingMethod sequencingMethod;
    @NonNull Assembly assembly;
    @NonNull List<SampleCreateDto> samples;
    @NonNull Integer variantFilterTreeId;
    @NonNull List<Integer> variantFilterClassIds;
    @NonNull Integer sampleFilterTreeId;
    @NonNull List<Integer> sampleFilterClassIds;
    Boolean isPublic;
}
