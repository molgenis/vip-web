package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.AffectedStatus;
import org.molgenis.vipweb.model.constants.Sex;

import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class SampleCreateDto {
    @NonNull String individualId;
    String paternalId;
    String maternalId;
    boolean proband;
    @NonNull Sex sex;
    @NonNull AffectedStatus affected;
    @NonNull List<Integer> hpoTermIds;
}
