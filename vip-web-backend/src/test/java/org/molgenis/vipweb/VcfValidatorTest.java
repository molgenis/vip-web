package org.molgenis.vipweb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

class VcfValidatorTest {
    private VcfValidator vcfValidator;

    @BeforeEach
    void setUp() {
        vcfValidator = new VcfValidator();
    }

    @Test
    void validateCorrupt() {
        Path vcfPath = Paths.get("src", "test", "resources", "corrupt.vcf");
        assertThrows(VcfParseException.class, () -> vcfValidator.validate(vcfPath));
    }

    @Test
    void validateMissingInfo() {
        Path vcfPath = Paths.get("src", "test", "resources", "unknown_info_field.vcf");
        assertThrows(VcfParseException.class, () -> vcfValidator.validate(vcfPath));
    }

    @Test
    void validateMissingFormat() {
        Path vcfPath = Paths.get("src", "test", "resources", "unknown_format_field.vcf");
        assertThrows(VcfParseException.class, () -> vcfValidator.validate(vcfPath));
    }

    @Test
    void validateMissingFilter() {
        Path vcfPath = Paths.get("src", "test", "resources", "unknown_filter_field.vcf");
        assertThrows(VcfParseException.class, () -> vcfValidator.validate(vcfPath));
    }

    // https://github.com/samtools/htsjdk/issues/628
    @ParameterizedTest
    @ValueSource(strings = {"bcf", "bcf.gz"})
    void analyzeBcfFileFormats(String fileType) {
        Path vcfPath = Paths.get("src", "test", "resources", "grch38_trio." + fileType);
        assertThrows(VcfParseException.class, () -> vcfValidator.validate(vcfPath));
    }
}