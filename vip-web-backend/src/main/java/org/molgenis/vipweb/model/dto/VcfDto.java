package org.molgenis.vipweb.model.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vipweb.model.constants.Assembly;

import java.util.List;

// class instead of record due to https://github.com/mockito/mockito/issues/3107
@Value
@Builder(toBuilder = true)
public class VcfDto {
    @NonNull Integer id;
    @NonNull FileDto file;
    @NonNull List<VcfSampleDto> samples;
    Assembly assembly;
    @NonNull Boolean isOwner;
    @NonNull Boolean isPublic;
}
