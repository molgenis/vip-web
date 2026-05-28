package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
@Jacksonized
public class ReportDto {
    @NonNull
    FileDto file;
}
