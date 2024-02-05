package org.molgenis.vipweb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.constants.Assembly;

@ExtendWith(MockitoExtension.class)
class VcfAnalyzerTest {

    private VcfAnalyzer vcfAnalyzer;

    @BeforeEach
    void setUp() {
        vcfAnalyzer = new VcfAnalyzer();
    }

    @Test
    void analyzeGRCh37Single() {
        Path vcfPath = Paths.get("src", "test", "resources", "grch37_single.vcf");
        assertEquals(
                AnalyzedVcf.builder()
                        .sampleNames(List.of("SAMPLE0"))
                        .predictedAssembly(Assembly.GRCh37)
                        .build(),
                vcfAnalyzer.analyze(vcfPath));
    }

    @Test
    void analyzeGRCh38Cohort() {
        Path vcfPath = Paths.get("src", "test", "resources", "grch38_cohort.vcf");
        assertEquals(
                AnalyzedVcf.builder()
                        .sampleNames(
                                List.of(
                                        "PATIENT0",
                                        "PATIENT1",
                                        "PATIENT2",
                                        "PATIENT3",
                                        "PATIENT4",
                                        "PATIENT5",
                                        "PATIENT6",
                                        "PATIENT7",
                                        "PATIENT8",
                                        "PATIENT9"))
                        .predictedAssembly(Assembly.GRCh38)
                        .build(),
                vcfAnalyzer.analyze(vcfPath));
    }

    @Test
    void analyzeT2TTrio() {
        Path vcfPath = Paths.get("src", "test", "resources", "t2t_trio.vcf");
        assertEquals(
                AnalyzedVcf.builder()
                        .sampleNames(List.of("CHILD", "FATHER", "MOTHER"))
                        .predictedAssembly(Assembly.T2T)
                        .build(),
                vcfAnalyzer.analyze(vcfPath));
    }

    @Test
    void analyzeUnknownContig() {
        Path vcfPath = Paths.get("src", "test", "resources", "unknown_contig.vcf");
        assertEquals(
                AnalyzedVcf.builder().sampleNames(List.of("PATIENT")).predictedAssembly(null).build(),
                vcfAnalyzer.analyze(vcfPath));
    }

    @Test
    void analyzeUnknownContigLength() {
        Path vcfPath = Paths.get("src", "test", "resources", "unknown_contig_length.vcf");
        assertEquals(
                AnalyzedVcf.builder().sampleNames(List.of("PATIENT")).predictedAssembly(null).build(),
                vcfAnalyzer.analyze(vcfPath));
    }

    @Test
    void analyzeCorrupt() {
        Path vcfPath = Paths.get("src", "test", "resources", "corrupt.vcf");
        assertThrows(VcfParseException.class, () -> vcfAnalyzer.analyze(vcfPath));
    }

    @ParameterizedTest
    @ValueSource(strings = {"vcf", "vcf.gz"})
    void analyzeVcfFileFormats(String fileType) {
        Path vcfPath = Paths.get("src", "test", "resources", "grch38_trio." + fileType);
        assertEquals(
                AnalyzedVcf.builder()
                        .sampleNames(List.of("PATIENT", "FATHER", "MOTHER"))
                        .predictedAssembly(Assembly.GRCh38)
                        .build(),
                vcfAnalyzer.analyze(vcfPath));
    }

    // https://github.com/samtools/htsjdk/issues/628
    @ParameterizedTest
    @ValueSource(strings = {"bcf", "bcf.gz"})
    void analyzeBcfFileFormats(String fileType) {
        Path vcfPath = Paths.get("src", "test", "resources", "grch38_trio." + fileType);
        assertThrows(VcfParseException.class, () -> vcfAnalyzer.analyze(vcfPath));
    }
}
