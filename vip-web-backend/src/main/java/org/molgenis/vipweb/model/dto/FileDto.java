package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class FileDto {
    @NonNull Integer id;
    @NonNull String blobId;
    @NonNull String filename;
    @NonNull Long size;
}
