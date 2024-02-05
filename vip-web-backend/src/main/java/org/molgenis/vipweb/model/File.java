package org.molgenis.vipweb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Value
public class File {
    String id;
    String filename;
    String contentType;
    long size;
    String url;
}
