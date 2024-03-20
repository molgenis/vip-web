package org.molgenis.vipweb.model.mapper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.*;
import org.molgenis.vipweb.model.constants.JobStatus;
import org.molgenis.vipweb.model.dto.*;
import org.molgenis.vipweb.service.FileService;
import org.molgenis.vipweb.service.FilterTreeService;
import org.molgenis.vipweb.service.VcfService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobMapper {
    private final VcfService vcfService;
    private final FilterTreeService filterTreeService;
    private final FileService fileService;
    private final SampleMapper sampleMapper;

    private static Set<VariantFilterClass> createJobVariantFilterClasses(
            List<Integer> filterClassIds, FilterTreeDto filterTree) {
        return filterClassIds.stream()
                .map(
                        filterClassId ->
                                filterTree.getClasses().stream()
                                        .filter(filterClass -> filterClass.getId().equals(filterClassId))
                                        .findFirst()
                                        .orElseThrow(UnknownEntityException::new))
                .map(
                        filterTreeClass ->
                                VariantFilterClass.builder()
                                        .filterTreeClass(AggregateReference.to(filterTreeClass.getId()))
                                        .build())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Set<SampleFilterClass> createJobSampleFilterClasses(
            List<Integer> filterClassIds, FilterTreeDto filterTree) {
        return filterClassIds.stream()
                .map(
                        filterClassId ->
                                filterTree.getClasses().stream()
                                        .filter(filterClass -> filterClass.getId().equals(filterClassId))
                                        .findFirst()
                                        .orElseThrow(UnknownEntityException::new))
                .map(
                        filterTreeClass ->
                                SampleFilterClass.builder()
                                        .filterTreeClass(AggregateReference.to(filterTreeClass.getId()))
                                        .build())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Job jobCreateDtoToJob(JobCreateDto jobCreateDto) {
        VcfDto vcf = vcfService.getVcfById(jobCreateDto.getVcfId());

        FilterTreeDto variantFilterTree =
                filterTreeService.getFilterTreeById(jobCreateDto.getVariantFilterTreeId());
        FilterTreeDto sampleFilterTree =
                filterTreeService.getFilterTreeById(jobCreateDto.getSampleFilterTreeId());

        // validate that filter classes belong to corresponding tree
        Set<VariantFilterClass> variantFilterClasses =
                createJobVariantFilterClasses(jobCreateDto.getVariantFilterClassIds(), variantFilterTree);
        Set<SampleFilterClass> sampleFilterClasses =
                createJobSampleFilterClasses(jobCreateDto.getSampleFilterClassIds(), sampleFilterTree);

        List<Sample> samples = sampleMapper.sampleCreateDtosToSamples(jobCreateDto.getSamples());
        boolean isPublic = jobCreateDto.getIsPublic() != null && jobCreateDto.getIsPublic();

        return Job.builder()
                .name(jobCreateDto.getName())
                .vcf(AggregateReference.to(vcf.getId()))
                .status(JobStatus.PENDING)
                .sequencingMethod(jobCreateDto.getSequencingMethod())
                .assembly(jobCreateDto.getAssembly())
                .samples(samples)
                .variantFilterTree(AggregateReference.to(variantFilterTree.getId()))
                .variantFilterClasses(variantFilterClasses)
                .sampleFilterTree(AggregateReference.to(sampleFilterTree.getId()))
                .sampleFilterClasses(sampleFilterClasses)
                .isPublic(isPublic)
                .build();
    }

    public JobDto jobToJobDto(Job job, boolean isOwner) {
        Report report = job.getReport();

        return JobDto.builder()
                .id(job.getId())
                .name(job.getName())
                .submitted(job.getCreationDate().toEpochMilli())
                .vcf(vcfService.getVcfById(job.getVcf().getId()))
                .status(job.getStatus())
                .sequencingMethod(job.getSequencingMethod())
                .assembly(job.getAssembly())
                .samples(sampleMapper.samplesToSampleDtos(job.getSamples()))
                .variantFilterTree(filterTreeService.getFilterTreeById(job.getVariantFilterTree().getId()))
                .variantFilterClassIds(
                        job.getVariantFilterClasses().stream()
                                .map(filterClass -> filterClass.getFilterTreeClass().getId())
                                .collect(Collectors.toList()))
                .sampleFilterTree(filterTreeService.getFilterTreeById(job.getSampleFilterTree().getId()))
                .sampleFilterClassIds(
                        job.getSampleFilterClasses().stream()
                                .map(filterClass -> filterClass.getFilterTreeClass().getId())
                                .collect(Collectors.toList()))
                .report(report != null ? mapReport(report) : null)
                .isOwner(isOwner)
                .isPublic(job.isPublic())
                .build();
    }

    private ReportDto mapReport(Report report) {
        Integer fileId = Objects.requireNonNull(report.getFile().getId());
        FileDto fileDto = fileService.getFileById(fileId);
        return ReportDto.builder().file(fileDto).build();
    }
}
