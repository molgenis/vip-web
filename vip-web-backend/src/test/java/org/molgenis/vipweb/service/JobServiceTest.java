package org.molgenis.vipweb.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.SampleSheetGenerator;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.Job;
import org.molgenis.vipweb.model.Report;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.JobCreateDto;
import org.molgenis.vipweb.model.dto.JobDto;
import org.molgenis.vipweb.model.dto.VcfDto;
import org.molgenis.vipweb.model.mapper.JobMapper;
import org.molgenis.vipweb.repository.JobRepository;
import org.molgenis.vipweb.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    @Mock
    private JobMapper jobMapper;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private FileService fileService;
    @Mock
    private VcfService vcfService;
    @Mock
    private SampleSheetGenerator sampleSheetGenerator;
    @Mock
    private AuthenticationFacade authenticationFacade;
    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobService =
                new JobService(
                        jobMapper,
                        jobRepository,
                        fileService,
                        vcfService,
                        sampleSheetGenerator,
                        authenticationFacade);
    }

    @Test
    void createJob() {
        JobCreateDto jobCreateDto = mock(JobCreateDto.class);
        Job job = mock(Job.class);
        when(jobMapper.jobCreateDtoToJob(jobCreateDto)).thenReturn(job);

        Job persistedJob = mock(Job.class);
        when(jobRepository.save(job)).thenReturn(persistedJob);

        JobDto jobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(persistedJob, true)).thenReturn(jobDto);

        assertEquals(jobDto, jobService.createJob(jobCreateDto));
    }

    @Test
    void getJobByIdAsUser() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer id = 2;
        Job job =
                when(mock(Job.class).getCreatedBy()).thenReturn(AggregateReference.to(userId)).getMock();
        when(jobRepository.findByIdAndCreatedByOrIdAndIsPublic(2, 1, 2, true))
                .thenReturn(Optional.of(job));
        JobDto JobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(job, true)).thenReturn(JobDto);
        assertEquals(JobDto, jobService.getJobById(id));
    }

    @Test
    void getJobByIdAsAdmin() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isAdmin()).thenReturn(true);

        Integer id = 1;
        Integer otherUserId = 2;
        Job job = when(mock(Job.class).getCreatedBy())
                .thenReturn(AggregateReference.to(otherUserId))
                .getMock();
        when(jobRepository.findById(id)).thenReturn(Optional.of(job));
        JobDto JobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(job, false)).thenReturn(JobDto);
        assertEquals(JobDto, jobService.getJobById(id));
    }

    @Test
    void getJobByIdAsVipbot() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isVipbot()).thenReturn(true);

        Integer id = 1;
        Job job = when(mock(Job.class).getCreatedBy())
                .thenReturn(AggregateReference.to(userId))
                .getMock();
        when(jobRepository.findById(id)).thenReturn(Optional.of(job));
        JobDto JobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(job, true)).thenReturn(JobDto);
        assertEquals(JobDto, jobService.getJobById(id));
    }

    @Test
    void getJobByIdAsAnonymous() {
        when(authenticationFacade.isAnonymousUser()).thenReturn(true);

        Integer id = 1;
        Job job = mock(Job.class);
        when(jobRepository.findByIdAndIsPublic(id, true)).thenReturn(Optional.of(job));
        JobDto JobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(job, false)).thenReturn(JobDto);
        assertEquals(JobDto, jobService.getJobById(id));
    }

    @Test
    void getJobByIdUnknownEntityException() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer id = 2;
        when(jobRepository.findByIdAndCreatedByOrIdAndIsPublic(2, 1, 2, true))
                .thenReturn(Optional.empty());
        assertThrows(UnknownEntityException.class, () -> jobService.getJobById(id));
    }

    @Test
    void getJobsAsUser() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Pageable pageable = mock(Pageable.class);

        Integer otherUserId = 2;
        Job job =
                when(mock(Job.class).getCreatedBy())
                        .thenReturn(AggregateReference.to(otherUserId))
                        .getMock();
        Page<Job> jobPage = new PageImpl<>(List.of(job));
        when(jobRepository.findAllByCreatedByOrIsPublic(userId, true, pageable)).thenReturn(jobPage);

        JobDto jobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(job, false)).thenReturn(jobDto);
        assertEquals(new PageImpl<>(List.of(jobDto)), jobService.getJobs(pageable));
    }

    @Test
    void getJobsAsAdmin() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isAdmin()).thenReturn(true);

        Pageable pageable = mock(Pageable.class);

        Job job = when(mock(Job.class).getCreatedBy())
                .thenReturn(AggregateReference.to(userId))
                .getMock();
        Page<Job> jobPage = new PageImpl<>(List.of(job));
        when(jobRepository.findAll(pageable)).thenReturn(jobPage);

        JobDto jobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(job, true)).thenReturn(jobDto);
        assertEquals(new PageImpl<>(List.of(jobDto)), jobService.getJobs(pageable));
    }

    @Test
    void getJobsAsVipbot() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isVipbot()).thenReturn(true);

        Pageable pageable = mock(Pageable.class);

        Integer otherUserId = 2;
        Job job = when(mock(Job.class).getCreatedBy())
                .thenReturn(AggregateReference.to(otherUserId))
                .getMock();
        Page<Job> jobPage = new PageImpl<>(List.of(job));
        when(jobRepository.findAll(pageable)).thenReturn(jobPage);

        JobDto jobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(job, false)).thenReturn(jobDto);
        assertEquals(new PageImpl<>(List.of(jobDto)), jobService.getJobs(pageable));
    }

    @Test
    void getJobsAsAnonymous() {
        when(authenticationFacade.isAnonymousUser()).thenReturn(true);

        Pageable pageable = mock(Pageable.class);

        Job job = mock(Job.class);
        Page<Job> jobPage = new PageImpl<>(List.of(job));
        when(jobRepository.findAllByIsPublic(true, pageable)).thenReturn(jobPage);

        JobDto jobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(job, false)).thenReturn(jobDto);
        assertEquals(new PageImpl<>(List.of(jobDto)), jobService.getJobs(pageable));
    }

    @Test
    void deleteJob() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer jobId = 2;

        Job job = mock(Job.class);
        when(job.getCreatedBy()).thenReturn(AggregateReference.to(userId));
        Integer vcfId = 3;
        when(job.getVcf()).thenReturn(AggregateReference.to(vcfId));
        VcfDto vcfDto = when(mock(VcfDto.class).getIsOwner()).thenReturn(true).getMock();
        when(vcfService.getVcfById(vcfId)).thenReturn(vcfDto);
        when(jobRepository.findByIdAndCreatedByOrIdAndIsPublic(jobId, userId, jobId, true))
                .thenReturn(Optional.of(job));
        when(jobRepository.countByVcf(vcfId)).thenReturn(0L);
        jobService.deleteJob(jobId);
        assertAll(() -> verify(jobRepository).delete(job), () -> verify(vcfService).deleteVcf(vcfId));
    }

    @Test
    void deleteJobKeepVcf() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer jobId = 2;

        Job job = mock(Job.class);
        when(job.getCreatedBy()).thenReturn(AggregateReference.to(userId));
        Integer vcfId = 3;
        when(job.getVcf()).thenReturn(AggregateReference.to(vcfId));
        VcfDto vcfDto = when(mock(VcfDto.class).getIsOwner()).thenReturn(true).getMock();
        when(vcfService.getVcfById(vcfId)).thenReturn(vcfDto);
        when(jobRepository.findByIdAndCreatedByOrIdAndIsPublic(jobId, userId, jobId, true))
                .thenReturn(Optional.of(job));
        when(jobRepository.countByVcf(vcfId)).thenReturn(1L);
        jobService.deleteJob(jobId);
        assertAll(() -> verify(jobRepository).delete(job), () -> verifyNoMoreInteractions(vcfService));
    }

    @Test
    void deleteJobFromOtherUserAsUser() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer jobId = 2;
        Integer otherUserId = 3;
        Job job =
                when(mock(Job.class).getCreatedBy())
                        .thenReturn(AggregateReference.to(otherUserId))
                        .getMock();
        when(jobRepository.findByIdAndCreatedByOrIdAndIsPublic(jobId, userId, jobId, true))
                .thenReturn(Optional.of(job));
        assertThrows(AccessDeniedException.class, () -> jobService.deleteJob(jobId));
    }

    @Test
    void deleteJobFromOtherUserAsAdmin() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isAdmin()).thenReturn(true);

        Integer jobId = 2;
        Integer otherUserId = 3;
        Integer fileId = 4;
        Report report =
                when(mock(Report.class).getFile()).thenReturn(AggregateReference.to(fileId)).getMock();
        Job job = mock(Job.class);
        when(job.getCreatedBy()).thenReturn(AggregateReference.to(otherUserId));
        when(job.getReport()).thenReturn(report);

        Integer vcfId = 5;
        when(job.getVcf()).thenReturn(AggregateReference.to(vcfId));
        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfService.getVcfById(vcfId)).thenReturn(vcfDto);
        when(jobRepository.countByVcf(vcfId)).thenReturn(0L);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        String blobId = "blobId";
        FileDto fileDto = when(mock(FileDto.class).getBlobId()).thenReturn(blobId).getMock();
        when(fileService.getFileById(fileId)).thenReturn(fileDto);
        jobService.deleteJob(jobId);
        assertAll(
                () -> verify(jobRepository).delete(job),
                () -> verify(fileService).deleteFileBytes(blobId),
                () -> verify(fileService).deleteFileById(fileId),
                () -> verify(vcfService).deleteVcf(vcfId));
    }

    @Test
    void getJobSampleSheetBytesById() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isAdmin()).thenReturn(true);

        Integer id = 1;
        Job job = when(mock(Job.class).getCreatedBy())
                .thenReturn(AggregateReference.to(userId))
                .getMock();
        when(jobRepository.findById(id)).thenReturn(Optional.of(job));
        JobDto jobDto = mock(JobDto.class);
        when(jobMapper.jobToJobDto(job, true)).thenReturn(jobDto);
        String sampleSheetStr = "str";
        when(sampleSheetGenerator.generate(jobDto)).thenReturn(sampleSheetStr);
        assertArrayEquals(
                sampleSheetStr.getBytes(StandardCharsets.UTF_8), jobService.getJobSampleSheetBytesById(id));
    }
}
