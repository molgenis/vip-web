package org.molgenis.vipweb.model.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.*;
import org.molgenis.vipweb.model.constants.Assembly;
import org.molgenis.vipweb.model.constants.JobStatus;
import org.molgenis.vipweb.model.constants.SequencingMethod;
import org.molgenis.vipweb.model.dto.*;
import org.molgenis.vipweb.service.FileService;
import org.molgenis.vipweb.service.FilterTreeService;
import org.molgenis.vipweb.service.VcfService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobMapperTest {
    @Mock
    private VcfService vcfService;
    @Mock
    private FilterTreeService filterTreeService;
    @Mock
    private FileService fileService;
    @Mock
    private SampleMapper sampleMapper;
    private JobMapper jobMapper;

    @BeforeEach
    void setUp() {
        jobMapper = new JobMapper(vcfService, filterTreeService, fileService, sampleMapper);
    }

    @Test
    void jobCreateDtoToJob() {
        String name = "name";
        Integer vcfId = 1;
        SequencingMethod sequencingMethod = SequencingMethod.WES;
        Assembly assembly = Assembly.GRCh38;
        List<SampleCreateDto> sampleCreateDtos =
                List.of(mock(SampleCreateDto.class), mock(SampleCreateDto.class));
        Integer variantFilterTreeId = 2;
        Integer variantFilterClassId0 = 3;
        Integer variantFilterClassId1 = 4;
        Integer sampleFilterTreeId = 5;
        Integer sampleFilterClassId0 = 6;
        Integer sampleFilterClassId1 = 7;
        boolean isPublic = true;

        VcfDto vcfDto = when(mock(VcfDto.class).getId()).thenReturn(vcfId).getMock();
        when(vcfService.getVcfById(vcfId)).thenReturn(vcfDto);
        List<Sample> samples = List.of(mock(Sample.class), mock(Sample.class));
        when(sampleMapper.sampleCreateDtosToSamples(sampleCreateDtos)).thenReturn(samples);
        FilterTreeDto variantFilterTree =
                when(mock(FilterTreeDto.class).getId()).thenReturn(variantFilterTreeId).getMock();
        FilterTreeClassDto variantFilterTreeClass0 =
                when(mock(FilterTreeClassDto.class).getId()).thenReturn(8).getMock();
        FilterTreeClassDto variantFilterTreeClass1 =
                when(mock(FilterTreeClassDto.class).getId()).thenReturn(variantFilterClassId0).getMock();
        FilterTreeClassDto variantFilterTreeClass2 =
                when(mock(FilterTreeClassDto.class).getId()).thenReturn(variantFilterClassId1).getMock();
        when(variantFilterTree.getClasses())
                .thenReturn(
                        List.of(variantFilterTreeClass0, variantFilterTreeClass1, variantFilterTreeClass2));
        when(filterTreeService.getFilterTreeById(variantFilterTreeId)).thenReturn(variantFilterTree);
        FilterTreeDto sampleFilterTree =
                when(mock(FilterTreeDto.class).getId()).thenReturn(sampleFilterTreeId).getMock();
        FilterTreeClassDto sampleFilterTreeClass0 =
                when(mock(FilterTreeClassDto.class).getId()).thenReturn(sampleFilterClassId1).getMock();
        FilterTreeClassDto sampleFilterTreeClass1 =
                when(mock(FilterTreeClassDto.class).getId()).thenReturn(sampleFilterClassId0).getMock();
        FilterTreeClassDto sampleFilterTreeClass2 = mock(FilterTreeClassDto.class);
        when(sampleFilterTree.getClasses())
                .thenReturn(
                        List.of(sampleFilterTreeClass0, sampleFilterTreeClass1, sampleFilterTreeClass2));
        when(filterTreeService.getFilterTreeById(sampleFilterTreeId)).thenReturn(sampleFilterTree);

        JobCreateDto jobCreateDto =
                JobCreateDto.builder()
                        .name(name)
                        .vcfId(vcfId)
                        .sequencingMethod(sequencingMethod)
                        .assembly(assembly)
                        .samples(sampleCreateDtos)
                        .variantFilterTreeId(variantFilterTreeId)
                        .variantFilterClassIds(List.of(variantFilterClassId0, variantFilterClassId1))
                        .sampleFilterTreeId(sampleFilterTreeId)
                        .sampleFilterClassIds(List.of(sampleFilterClassId0, sampleFilterClassId1))
                        .isPublic(isPublic)
                        .build();

        Job job = jobMapper.jobCreateDtoToJob(jobCreateDto);
        // can't use assertEquals, filter tree class order might differ
        assertAll(
                () -> assertEquals(name, job.getName()),
                () -> assertEquals(AggregateReference.to(vcfId), job.getVcf()),
                () -> assertEquals(JobStatus.PENDING, job.getStatus()),
                () -> assertEquals(sequencingMethod, job.getSequencingMethod()),
                () -> assertEquals(assembly, job.getAssembly()),
                () -> assertEquals(samples, job.getSamples()),
                () -> assertEquals(AggregateReference.to(variantFilterTreeId), job.getVariantFilterTree()),
                () -> assertEquals(AggregateReference.to(sampleFilterTreeId), job.getSampleFilterTree()),
                () -> assertEquals(isPublic, job.isPublic()));
    }

    @Test
    void jobToJobDto() {
        Integer id = 1;
        String name = "name";
        Integer vcfId = 2;
        Instant creationDate = Instant.now();
        JobStatus status = JobStatus.COMPLETED;
        SequencingMethod sequencingMethod = SequencingMethod.WES;
        Assembly assembly = Assembly.GRCh38;
        List<Sample> samples = List.of(mock(Sample.class), mock(Sample.class));
        Integer variantFilterTreeId = 3;
        Integer variantFilterClass0 = 4;
        Integer variantFilterClass1 = 5;
        Set<VariantFilterClass> variantFilterClasses =
                Set.of(
                        VariantFilterClass.builder()
                                .filterTreeClass(AggregateReference.to(variantFilterClass0))
                                .build(),
                        VariantFilterClass.builder()
                                .filterTreeClass(AggregateReference.to(variantFilterClass1))
                                .build());
        Integer sampleFilterTreeId = 6;
        Integer sampleFilterClass0 = 7;
        Set<SampleFilterClass> sampleFilterClasses =
                Set.of(
                        SampleFilterClass.builder()
                                .filterTreeClass(AggregateReference.to(sampleFilterClass0))
                                .build());
        Integer fileId = 8;
        Report report = Report.builder().file(AggregateReference.to(fileId)).build();
        boolean isPublic = true;

        Job job =
                Job.builder()
                        .id(id)
                        .name(name)
                        .vcf(AggregateReference.to(vcfId))
                        .creationDate(creationDate)
                        .status(status)
                        .sequencingMethod(sequencingMethod)
                        .assembly(assembly)
                        .samples(samples)
                        .variantFilterTree(AggregateReference.to(variantFilterTreeId))
                        .variantFilterClasses(variantFilterClasses)
                        .sampleFilterTree(AggregateReference.to(sampleFilterTreeId))
                        .sampleFilterClasses(sampleFilterClasses)
                        .report(report)
                        .isPublic(isPublic)
                        .build();

        List<SampleDto> sampleDtos = List.of(mock(SampleDto.class), mock(SampleDto.class));
        when(sampleMapper.samplesToSampleDtos(samples)).thenReturn(sampleDtos);
        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfService.getVcfById(vcfId)).thenReturn(vcfDto);
        FilterTreeDto variantFilterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeService.getFilterTreeById(variantFilterTreeId)).thenReturn(variantFilterTreeDto);
        FilterTreeDto sampleFilterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeService.getFilterTreeById(sampleFilterTreeId)).thenReturn(sampleFilterTreeDto);
        FileDto fileDto = mock(FileDto.class);
        when(fileService.getFileById(fileId)).thenReturn(fileDto);

        JobDto jobDto = jobMapper.jobToJobDto(job, true);
        // can't use assertEquals, filter tree class order might differ
        assertAll(
                () -> assertEquals(id, jobDto.getId()),
                () -> assertEquals(name, jobDto.getName()),
                () -> assertEquals(vcfDto, jobDto.getVcf()),
                () -> assertEquals(creationDate.toEpochMilli(), jobDto.getSubmitted()),
                () -> assertEquals(status, jobDto.getStatus()),
                () -> assertEquals(sequencingMethod, jobDto.getSequencingMethod()),
                () -> assertEquals(assembly, jobDto.getAssembly()),
                () -> assertEquals(sampleDtos, jobDto.getSamples()),
                () -> assertEquals(variantFilterTreeDto, jobDto.getVariantFilterTree()),
                () ->
                        assertEquals(
                                Set.of(variantFilterClass0, variantFilterClass1),
                                new HashSet<>(jobDto.getVariantFilterClassIds())),
                () -> assertEquals(sampleFilterTreeDto, jobDto.getSampleFilterTree()),
                () ->
                        assertEquals(
                                Set.of(sampleFilterClass0), new HashSet<>(jobDto.getSampleFilterClassIds())),
                () -> assertEquals(ReportDto.builder().file(fileDto).build(), jobDto.getReport()),
                () -> assertTrue(jobDto.getIsOwner()),
                () -> assertTrue(jobDto.getIsPublic()));
    }
}
