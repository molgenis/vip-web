package org.molgenis.vipweb;

import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VcfCreator {
    private static final Map<String, Integer> CONTIGS;

    static {
        CONTIGS = new LinkedHashMap<>();
        CONTIGS.put("chr1", 248956422);
        CONTIGS.put("chr2", 242193529);
        CONTIGS.put("chr3", 198295559);
        CONTIGS.put("chr4", 190214555);
        CONTIGS.put("chr5", 181538259);
        CONTIGS.put("chr6", 170805979);
        CONTIGS.put("chr7", 159345973);
        CONTIGS.put("chr8", 145138636);
        CONTIGS.put("chr9", 138394717);
        CONTIGS.put("chr10", 133797422);
        CONTIGS.put("chr11", 135086622);
        CONTIGS.put("chr12", 133275309);
        CONTIGS.put("chr13", 114364328);
        CONTIGS.put("chr14", 107043718);
        CONTIGS.put("chr15", 101991189);
        CONTIGS.put("chr16", 90338345);
        CONTIGS.put("chr17", 83257441);
        CONTIGS.put("chr18", 80373285);
        CONTIGS.put("chr19", 58617616);
        CONTIGS.put("chr20", 64444167);
        CONTIGS.put("chr21", 46709983);
        CONTIGS.put("chr22", 50818468);
        CONTIGS.put("chrX", 156040895);
        CONTIGS.put("chrY", 57227415);
        CONTIGS.put("chrM", 16569);
    }

    private static List<Variant> parseVariants(String variantsStr) {
        String[] lines = variantsStr.split("[\\r\\n]+");
        return Arrays.stream(lines).map(VcfCreator::parseVariant).toList();
    }

    private static Variant parseVariant(String variantStr) {
        String[] tokens = variantStr.split("-", -1);
        if (tokens.length != 4) {
            throw new VcfParseException(
                    "variant '%s does not match format '<chromosome>-<position>-<reference base(s)>-<alternate base(s)>'"
                            .formatted(variantStr));
        }

        String chromosome = tokens[0];
        if (chromosome.isEmpty() || !CONTIGS.containsKey("chr" + chromosome)) {
            throw new VcfParseException(
                    "variant '%s' chromosome '%s' is invalid".formatted(variantStr, chromosome));
        }

        int position;
        String positionStr = tokens[1];
        try {
            position = Integer.parseInt(positionStr);
        } catch (NumberFormatException e) {
            throw new VcfParseException(
                    "variant '%s' position '%s' is invalid".formatted(variantStr, positionStr));
        }
        if (position <= 0) {
            throw new VcfParseException(
                    "variant '%s' position '%s' must be >= 0".formatted(variantStr, positionStr));
        }

        String ref = tokens[2];
        if (ref.isEmpty() || !ref.matches("[ACTG]+")) {
            throw new VcfParseException(
                    "variant '%s' reference base(s) '%s' are invalid".formatted(variantStr, ref));
        }

        String alt = tokens[3];
        if (alt.isEmpty() || !alt.matches("[ACTG]+")) {
            throw new VcfParseException(
                    "variant '%s' alternate base(s) '%s' are invalid".formatted(variantStr, alt));
        }

        return Variant.builder()
                .chromosome("chr" + chromosome)
                .position(position)
                .ref(ref)
                .alt(alt)
                .build();
    }

    public String create(String variantsStr) {
        // sort will result in e.g. chr10 appearing before chr2, but this is ok since it is valid vcf
        List<Variant> variants =
                parseVariants(variantsStr).stream()
                        .sorted(Comparator.comparing(Variant::chromosome).thenComparingInt(Variant::position))
                        .toList();

        StringBuilder strBuilder = new StringBuilder("##fileformat=VCFv4.2").append('\n');
        CONTIGS.forEach(
                (key, value) ->
                        strBuilder
                                .append("##contig=<ID=")
                                .append(key)
                                .append(",length=")
                                .append(value)
                                .append(">")
                                .append('\n'));
        strBuilder
                .append("##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">")
                .append('\n');
        strBuilder.append("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tSAMPLE").append('\n');
        variants.forEach(
                variant ->
                        strBuilder
                                .append(variant.chromosome())
                                .append('\t')
                                .append(variant.position())
                                .append('\t')
                                .append('.')
                                .append('\t')
                                .append(variant.ref())
                                .append('\t')
                                .append(variant.alt())
                                .append('\t')
                                .append('.') // qual
                                .append('\t')
                                .append('.') // filter
                                .append('\t')
                                .append('.') // info
                                .append('\t')
                                .append("GT") // format
                                .append('\t')
                                .append("1|1")
                                .append('\n'));

        return strBuilder.toString();
    }

    @Builder(toBuilder = true)
    private record Variant(String chromosome, int position, String ref, String alt) {
    }
}
