package org.molgenis.vipweb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.constants.Assembly;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class VcfAnalyzerTest {
    @Mock
    private VcfValidator vcfValidator;
    private VcfAnalyzer vcfAnalyzer;

    @BeforeEach
    void setUp() {
        vcfAnalyzer = new VcfAnalyzer(vcfValidator);
    }

    @Test
    void analyzeInvalid() {
        Path vcfPath = mock(Path.class);
        doThrow(VcfParseException.class).when(vcfValidator).validate(vcfPath);
        assertThrows(VcfParseException.class, () -> vcfAnalyzer.analyze(vcfPath));
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
}
