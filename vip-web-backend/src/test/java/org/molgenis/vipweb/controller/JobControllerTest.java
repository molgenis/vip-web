package org.molgenis.vipweb.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.BlobStore;
import org.molgenis.vipweb.model.constants.JobStatus;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.JobCreateDto;
import org.molgenis.vipweb.model.dto.JobDto;
import org.molgenis.vipweb.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@ExtendWith(MockitoExtension.class)
class JobControllerTest {
    @Mock
    private JobService jobService;
    @Mock
    private BlobStore blobStore;
    private JobController jobController;

    @BeforeEach
    void setUp() {
        jobController = new JobController(jobService, blobStore);
    }

    @Test
    void getJobs() {
        @SuppressWarnings("unchecked")
        Page<JobDto> jobDtos = mock(Page.class);
        when(jobService.getJobs(any())).thenReturn(jobDtos);
        assertEquals(jobDtos, jobController.getJobs());
    }

    @Test
    void createJob() {
        JobCreateDto jobCreateDto = mock(JobCreateDto.class);
        JobDto jobDto = mock(JobDto.class);
        when(jobService.createJob(jobCreateDto)).thenReturn(jobDto);
        assertEquals(jobDto, jobController.createJob(jobCreateDto));
    }

    @Test
    void getJobById() {
        Integer id = 1;
        JobDto jobDto = mock(JobDto.class);
        when(jobService.getJobById(id)).thenReturn(jobDto);
        assertEquals(jobDto, jobController.getJobById(id));
    }

    @Test
    void deleteJob() {
        Integer id = 1;
        jobController.deleteJob(id);
        verify(jobService).deleteJob(id);
    }

    @Test
    void getAndClaimJob() {
        Integer id = 1;
        when(jobService.getAndClaimJob()).thenReturn(Optional.of(id));
        assertEquals(id, jobController.getAndClaimJob());
    }

    @Test
    void getAndClaimJobNoJob() {
        when(jobService.getAndClaimJob()).thenReturn(Optional.empty());
        assertNull(jobController.getAndClaimJob());
    }

    @Test
    void downloadVcf() {
        Integer id = 1;
        String filename = "filename";
        long size = 123L;
        FileDto fileDto = mock(FileDto.class);
        when(fileDto.getFilename()).thenReturn(filename);
        when(fileDto.getSize()).thenReturn(size);
        when(jobService.getJobVcfFileById(id)).thenReturn(fileDto);
        ResponseEntity<StreamingResponseBody> responseEntity = jobController.downloadVcf(id);
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () ->
                        assertEquals(
                                "attachment; filename=\"%s\"".formatted(filename),
                                responseEntity.getHeaders().getContentDisposition().toString()),
                () -> assertEquals(size, responseEntity.getHeaders().getContentLength()));
    }

    @Test
    void downloadSampleSheet() {
        Integer id = 1;
        byte[] bytes = new byte[]{1, 2, 3};
        when(jobService.getJobSampleSheetBytesById(id)).thenReturn(bytes);
        ResponseEntity<StreamingResponseBody> responseEntity = jobController.downloadSampleSheet(id);
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () ->
                        assertEquals(
                                "attachment; filename=\"samplesheet.tsv\"",
                                responseEntity.getHeaders().getContentDisposition().toString()),
                () -> assertEquals(bytes.length, responseEntity.getHeaders().getContentLength()));
    }

    @Test
    void downloadVariantFilterTree() {
        Integer id = 1;
        long size = 123L;
        FileDto fileDto = mock(FileDto.class);
        when(fileDto.getSize()).thenReturn(size);
        when(jobService.getJobVariantFilterTreeFileById(id)).thenReturn(fileDto);
        ResponseEntity<StreamingResponseBody> responseEntity =
                jobController.downloadVariantFilterTree(id);
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () ->
                        assertEquals(
                                "attachment; filename=\"classification_tree_variants.json\"",
                                responseEntity.getHeaders().getContentDisposition().toString()),
                () -> assertEquals(size, responseEntity.getHeaders().getContentLength()));
    }

    @Test
    void downloadSampleFilterTree() {
        Integer id = 1;
        long size = 123L;
        FileDto fileDto = mock(FileDto.class);
        when(fileDto.getSize()).thenReturn(size);
        when(jobService.getJobSampleFilterTreeFileById(id)).thenReturn(fileDto);
        ResponseEntity<StreamingResponseBody> responseEntity =
                jobController.downloadSampleFilterTree(id);
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () ->
                        assertEquals(
                                "attachment; filename=\"classification_tree_samples.json\"",
                                responseEntity.getHeaders().getContentDisposition().toString()),
                () -> assertEquals(size, responseEntity.getHeaders().getContentLength()));
    }

    @Test
    void getVariantTreeFilterClasses() {
        Integer jobId = 1;
        when(jobService.getVariantTreeFilterClasses(jobId)).thenReturn(List.of("class1", "class2"));
        assertEquals("class1,class2", jobController.getVariantTreeFilterClasses(jobId));
    }

    @Test
    void getSampleTreeFilterClasses() {
        Integer jobId = 1;
        when(jobService.getSampleTreeFilterClasses(jobId)).thenReturn(List.of("class1", "class2"));
        assertEquals("class1,class2", jobController.getSampleTreeFilterClasses(jobId));
    }

    @Test
    void downloadJobReport() {
        Integer id = 1;
        String filename = "filename";
        long size = 123L;
        FileDto fileDto = mock(FileDto.class);
        when(fileDto.getFilename()).thenReturn(filename);
        when(fileDto.getSize()).thenReturn(size);
        when(jobService.getJobReportFileById(id)).thenReturn(fileDto);
        ResponseEntity<StreamingResponseBody> responseEntity = jobController.downloadJobReport(id);
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () ->
                        assertEquals(
                                "attachment; filename=\"%s\"".formatted(filename),
                                responseEntity.getHeaders().getContentDisposition().toString()),
                () -> assertEquals(size, responseEntity.getHeaders().getContentLength()));
    }

    @Test
    void updateJobStatus() {
        Integer id = 1;
        JobStatus jobStatus = JobStatus.COMPLETED;
        jobController.updateJobStatus(id, jobStatus);
        verify(jobService).updateJobStatus(id, jobStatus);
    }

    @Test
    void uploadReport() throws IOException {
        ServletInputStream inputStream = mock(ServletInputStream.class);
        HttpServletRequest request =
                when(mock(HttpServletRequest.class).getInputStream()).thenReturn(inputStream).getMock();
        Integer id = 1;
        String filename = "filename";
        jobController.uploadReport(request, id, filename);
        verify(jobService).uploadReport(eq(id), any());
    }
}
