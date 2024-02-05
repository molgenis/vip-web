package org.molgenis.vipweb;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder(toBuilder = true)
@Value
public class Blob {
    @NonNull String id;
    @NonNull Long size;
}
