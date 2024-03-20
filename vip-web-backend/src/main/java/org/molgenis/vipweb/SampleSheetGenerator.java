package org.molgenis.vipweb;

import org.molgenis.vipweb.model.constants.AffectedStatus;
import org.molgenis.vipweb.model.constants.Assembly;
import org.molgenis.vipweb.model.constants.SequencingMethod;
import org.molgenis.vipweb.model.constants.Sex;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.HpoTermDto;
import org.molgenis.vipweb.model.dto.JobDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.join;

@Component
public class SampleSheetGenerator {
    private static String mapAssembly(Assembly assembly) {
        return switch (assembly) {
            case GRCh37 -> "GRCh37";
            case GRCh38 -> "GRCh38";
            case T2T -> "T2T";
        };
    }

    private static String mapSequencingMethod(SequencingMethod sequencingMethod) {
        return switch (sequencingMethod) {
            case WES -> "WES";
            case WGS -> "WGS";
        };
    }

    private static String mapHpoTerms(List<HpoTermDto> hpoTerms) {
        return hpoTerms.stream().map(HpoTermDto::getTerm).collect(Collectors.joining(","));
    }

    private static String mapAffected(AffectedStatus affected) {
        return switch (affected) {
            case TRUE -> "true";
            case FALSE -> "false";
            case UNKNOWN -> "";
        };
    }

    private static String mapSex(Sex sex) {
        return switch (sex) {
            case MALE -> "male";
            case FEMALE -> "female";
            case UNKNOWN -> "";
        };
    }

    public String generate(JobDto jobDto) {
        FileDto file = jobDto.getVcf().getFile();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(
                        join(
                                "\t",
                                List.of(
                                        "project_id",
                                        "family_id",
                                        "individual_id",
                                        "paternal_id",
                                        "maternal_id",
                                        "sex",
                                        "affected",
                                        "proband",
                                        "hpo_ids",
                                        "sequencing_method",
                                        "assembly",
                                        "vcf")))
                .append("\n");
        jobDto
                .getSamples()
                .forEach(
                        sample ->
                                stringBuilder
                                        .append(
                                                join(
                                                        "\t",
                                                        List.of(
                                                                "vip",
                                                                "fam",
                                                                sample.getIndividualId(),
                                                                sample.getPaternalId() != null ? sample.getPaternalId() : "",
                                                                sample.getMaternalId() != null ? sample.getMaternalId() : "",
                                                                mapSex(sample.getSex()),
                                                                mapAffected(sample.getAffected()),
                                                                sample.isProband() ? "true" : "",
                                                                mapHpoTerms(sample.getHpoTerms()),
                                                                mapSequencingMethod(jobDto.getSequencingMethod()),
                                                                mapAssembly(jobDto.getAssembly()),
                                                                "data/" + file.getFilename())))
                                        .append("\n"));

        return stringBuilder.toString();
    }
}
