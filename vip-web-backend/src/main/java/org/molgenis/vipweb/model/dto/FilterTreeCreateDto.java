package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.FilterTreeType;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class FilterTreeCreateDto {
    @NonNull FilterTreeType type;
    @NonNull FileCreateDto fileCreateDto;
    Boolean isPublic;
}
