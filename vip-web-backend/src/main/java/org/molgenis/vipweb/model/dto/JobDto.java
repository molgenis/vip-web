package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.Assembly;
import org.molgenis.vipweb.model.constants.JobStatus;
import org.molgenis.vipweb.model.constants.SequencingMethod;

import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class JobDto {
    @NonNull Integer id;
    @NonNull String name;
    @NonNull VcfDto vcf;
    @NonNull Long submitted;
    @NonNull JobStatus status;
    @NonNull SequencingMethod sequencingMethod;
    @NonNull Assembly assembly;
    @NonNull List<SampleDto> samples;
    @NonNull FilterTreeDto variantFilterTree;
    @NonNull List<Integer> variantFilterClassIds;
    @NonNull FilterTreeDto sampleFilterTree;
    @NonNull List<Integer> sampleFilterClassIds;
    ReportDto report;
    @NonNull Boolean isOwner;
    @NonNull Boolean isPublic;
}
