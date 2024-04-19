package org.molgenis.vipweb;

import htsjdk.tribble.TribbleException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class VcfValidator {

    public void validate(Path vcfPath) {
        try (VCFIterator vcfIterator = new VCFIteratorBuilder().open(vcfPath)) {
            VCFHeader vcfHeader = vcfIterator.getHeader();
            while (vcfIterator.hasNext()) {
                VariantContext variantContext = vcfIterator.next();
                validateVariantContext(vcfHeader, variantContext);
            }
        } catch (TribbleException e) {
            e.setSource(null); // do not leak source file location
            throw new VcfParseException(e.getMessage());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void validateVariantContext(VCFHeader vcfHeader, VariantContext variantContext) {
        validateInfo(vcfHeader, variantContext);
        validateFormat(vcfHeader, variantContext);
        validateFilter(vcfHeader, variantContext);
    }

    private void validateInfo(VCFHeader vcfHeader, VariantContext variantContext) {
        for (String infoFieldId : variantContext.getAttributes().keySet()) {
            if (!vcfHeader.hasInfoLine(infoFieldId)) {
                throw new VcfParseException("missing ##INFO header line for field '%s'".formatted(infoFieldId));
            }
        }
    }

    private void validateFormat(VCFHeader vcfHeader, VariantContext variantContext) {
        for (String formatFieldId : variantContext.calcVCFGenotypeKeys(vcfHeader)) {
            if (!vcfHeader.hasFormatLine(formatFieldId)) {
                throw new VcfParseException("missing ##FORMAT header line for field '%s'".formatted(formatFieldId));
            }
        }
    }

    private void validateFilter(VCFHeader vcfHeader, VariantContext variantContext) {
        for (String filter : variantContext.getFilters()) {
            if (!vcfHeader.hasFilterLine(filter)) {
                throw new VcfParseException("missing ##FILTER header line for field '%s'".formatted(filter));
            }
        }
    }
}
