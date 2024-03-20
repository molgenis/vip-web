package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.Value;
import org.molgenis.vipweb.model.constants.AffectedStatus;
import org.molgenis.vipweb.model.constants.Sex;

import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class SampleDto {
    String individualId;
    String paternalId;
    String maternalId;
    boolean proband;
    Sex sex;
    AffectedStatus affected;
    List<HpoTermDto> hpoTerms;
}
