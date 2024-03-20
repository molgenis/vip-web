package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.nio.channels.ReadableByteChannel;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class FileCreateDto {
    @NonNull ReadableByteChannel readableByteChannel;
    @NonNull String filename;
    Boolean isPublic;
}
