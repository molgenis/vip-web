package org.molgenis.vipweb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Blob {
    String id;
    long size;
}
