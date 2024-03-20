package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class FilterTreeDto {
    @NonNull Integer id;
    @NonNull String name;
    String description;
    @NonNull List<FilterTreeClassDto> classes;
    @NonNull FileDto file;
}
