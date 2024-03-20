package org.molgenis.vipweb.populate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.constants.*;
import org.molgenis.vipweb.model.dto.*;
import org.molgenis.vipweb.service.FilterTreeService;
import org.molgenis.vipweb.service.HpoService;
import org.molgenis.vipweb.service.JobService;
import org.molgenis.vipweb.service.VcfService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JobPopulator {
    private final VcfService vcfService;
    private final FilterTreeService filterTreeService;
    private final JobService jobService;
    private final HpoService hpoService;

    @Transactional
    public void populate(Path jobsJson) {
        if (Files.notExists(jobsJson)) {
            throw new IllegalArgumentException("'%s' does not exist".formatted(jobsJson));
        }

        try {
            Jobs jobs = new ObjectMapper().readValue(jobsJson.toFile(), Jobs.class);
            jobs.jobs().forEach(this::populate);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void populate(Job job) {
        Path vcf = Path.of(job.vcf());

        VcfDto vcfDto;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(vcf.toFile(), "r")) {
            FileCreateDto fileCreateDto =
                    FileCreateDto.builder()
                            .readableByteChannel(randomAccessFile.getChannel())
                            .filename(vcf.getFileName().toString())
                            .isPublic(true)
                            .build();
            vcfDto = vcfService.upload(fileCreateDto);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        FilterTreeDto variantFilterTree =
                filterTreeService.getDefaultFilterTree(FilterTreeType.VARIANT);
        FilterTreeDto sampleFilterTree = filterTreeService.getDefaultFilterTree(FilterTreeType.SAMPLE);

        JobCreateDto jobCreateDto =
                JobCreateDto.builder()
                        .name(vcf.getFileName().toString())
                        .vcfId(vcfDto.getId())
                        .sequencingMethod(job.sequencingMethod())
                        .assembly(job.assembly())
                        .samples(
                                job.samples().stream()
                                        .map(
                                                sample ->
                                                        SampleCreateDto.builder()
                                                                .individualId(sample.individualId())
                                                                .paternalId(sample.paternalId())
                                                                .maternalId(sample.maternalId())
                                                                .proband(sample.proband())
                                                                .sex(sample.sex())
                                                                .affected(sample.affected())
                                                                .hpoTermIds(
                                                                        sample.hpoTerms().stream()
                                                                                .map(hpoTerm -> hpoService.findByTerm(hpoTerm).getId())
                                                                                .toList())
                                                                .build())
                                        .toList())
                        .variantFilterTreeId(variantFilterTree.getId())
                        .variantFilterClassIds(
                                job.variantFilterClasses != null
                                        ? job.variantFilterClasses.stream()
                                        .map(
                                                variantFilterClass ->
                                                        variantFilterTree.getClasses().stream()
                                                                .filter(
                                                                        filterTreeClass ->
                                                                                filterTreeClass.getName().equals(variantFilterClass))
                                                                .findAny()
                                                                .orElseThrow(UnknownEntityException::new)
                                                                .getId())
                                        .toList()
                                        : variantFilterTree.getClasses().stream()
                                        .filter(FilterTreeClassDto::isDefaultFilter)
                                        .map(FilterTreeClassDto::getId)
                                        .toList())
                        .sampleFilterTreeId(sampleFilterTree.getId())
                        .sampleFilterClassIds(
                                job.sampleFilterClasses != null
                                        ? job.sampleFilterClasses.stream()
                                        .map(
                                                sampleFilterClass ->
                                                        sampleFilterTree.getClasses().stream()
                                                                .filter(
                                                                        filterTreeClass ->
                                                                                filterTreeClass.getName().equals(sampleFilterClass))
                                                                .findAny()
                                                                .orElseThrow(UnknownEntityException::new)
                                                                .getId())
                                        .toList()
                                        : sampleFilterTree.getClasses().stream()
                                        .filter(FilterTreeClassDto::isDefaultFilter)
                                        .map(FilterTreeClassDto::getId)
                                        .toList())
                        .isPublic(true)
                        .build();
        jobService.createJob(jobCreateDto);
    }

    private record Jobs(List<Job> jobs) {
    }

    private record Job(
            @NonNull String vcf,
            @NonNull SequencingMethod sequencingMethod,
            @NonNull Assembly assembly,
            List<String> variantFilterClasses,
            List<String> sampleFilterClasses,
            @NonNull List<Sample> samples) {
    }

    private record Sample(
            @NonNull String individualId,
            String paternalId,
            String maternalId,
            boolean proband,
            @NonNull Sex sex,
            @NonNull AffectedStatus affected,
            @NonNull List<String> hpoTerms) {
    }
}
