package org.molgenis.vipweb;

import htsjdk.variant.vcf.VCFContigHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.constants.Assembly;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VcfAnalyzer {

    private final VcfValidator vcfValidator;

    public AnalyzedVcf analyze(Path vcfPath) {
        vcfValidator.validate(vcfPath);

        AnalyzedVcf analyzedVcf;
        try (VCFIterator vcfIterator = new VCFIteratorBuilder().open(vcfPath)) {
            VCFHeader vcfHeader = vcfIterator.getHeader();
            analyzedVcf = analyzeHeader(vcfHeader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return analyzedVcf;
    }

    private AnalyzedVcf analyzeHeader(VCFHeader vcfHeader) {
        List<String> sampleNames =
                vcfHeader.getSampleNameToOffset().entrySet().stream()
                        .sorted(Comparator.comparingInt(Map.Entry::getValue))
                        .map(Map.Entry::getKey)
                        .toList();

        Assembly predictedAssembly = null;
        for (VCFContigHeaderLine contigLine : vcfHeader.getContigLines()) {

            if (contigLine.getID().equals("chr1")) {
                Map<String, String> genericFields = contigLine.getGenericFields();
                if (genericFields.containsKey("length")) {
                    String length = genericFields.get("length");
                    if (length.equals("248956422")) {
                        predictedAssembly = Assembly.GRCh38;
                        break;
                    } else if (length.equals("248387328")) {
                        predictedAssembly = Assembly.T2T;
                    }
                }
            }

            // probably GRCh37
            if (contigLine.getID().equals("1")) {
                Map<String, String> genericFields = contigLine.getGenericFields();
                if (genericFields.containsKey("length")) {
                    String length = genericFields.get("length");
                    if (length.equals("249250621")) {
                        predictedAssembly = Assembly.GRCh37;
                        break;
                    }
                }
            }
        }

        return AnalyzedVcf.builder()
                .sampleNames(sampleNames)
                .predictedAssembly(predictedAssembly)
                .build();
    }
}
