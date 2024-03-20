package org.molgenis.vipweb;

import lombok.Builder;
import lombok.Value;
import org.molgenis.vipweb.model.constants.Assembly;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class AnalyzedVcf {
    List<String> sampleNames;
    Assembly predictedAssembly;
}
