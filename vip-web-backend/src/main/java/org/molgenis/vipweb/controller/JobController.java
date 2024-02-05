package org.molgenis.vipweb.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.BlobStore;
import org.molgenis.vipweb.FileUploadUtils;
import org.molgenis.vipweb.model.constants.JobStatus;
import org.molgenis.vipweb.model.dto.FileCreateDto;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.JobCreateDto;
import org.molgenis.vipweb.model.dto.JobDto;
import org.molgenis.vipweb.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import static java.nio.channels.Channels.newChannel;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController implements ApiController {
    private final JobService jobService;
    private final BlobStore blobStore;

    @GetMapping
    public Page<JobDto> getJobs() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("creationDate").descending());
        return jobService.getJobs(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobDto createJob(@RequestBody JobCreateDto job) {
        return jobService.createJob(job);
    }

    @GetMapping("/{id}")
    public JobDto getJobById(@PathVariable("id") Integer id) {
        return jobService.getJobById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@PathVariable("id") Integer id) {
        jobService.deleteJob(id);
    }

    @PostMapping("/claim")
    public Integer getAndClaimJob() {
        return jobService.getAndClaimJob().orElse(null);
    }

    @GetMapping("/{id}/vcf")
    public ResponseEntity<StreamingResponseBody> downloadVcf(@PathVariable("id") Integer jobId) {
        FileDto fileDto = jobService.getJobVcfFileById(jobId);
        return createFileDownloadResponse(fileDto);
    }

    @GetMapping("/{id}/samplesheet")
    public ResponseEntity<StreamingResponseBody> downloadSampleSheet(
            @PathVariable("id") Integer jobId) {
        byte[] bytes = jobService.getJobSampleSheetBytesById(jobId);

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        builder.header(
                CONTENT_TYPE, "text/tab-separated-values"); // see http://www.rfc-editor.org/rfc/rfc4180.txt
        builder.header(CONTENT_DISPOSITION, "attachment; filename=\"samplesheet.tsv\"");
        builder.contentLength(bytes.length);

        return builder.body(outputStream -> outputStream.write(bytes));
    }

    @GetMapping("/{id}/filtertree/variant")
    public ResponseEntity<StreamingResponseBody> downloadVariantFilterTree(
            @PathVariable("id") Integer jobId) {
        FileDto fileDto = jobService.getJobVariantFilterTreeFileById(jobId);
        return createFileDownloadResponse(fileDto, "classification_tree_variants.json");
    }

    @GetMapping("/{id}/filtertree/sample")
    public ResponseEntity<StreamingResponseBody> downloadSampleFilterTree(
            @PathVariable("id") Integer jobId) {
        FileDto fileDto = jobService.getJobSampleFilterTreeFileById(jobId);
        return createFileDownloadResponse(fileDto, "classification_tree_samples.json");
    }

    @GetMapping("/{id}/filterclasses/variant")
    public String getVariantTreeFilterClasses(@PathVariable("id") Integer jobId) {
        List<String> variantTreeFilterClasses = jobService.getVariantTreeFilterClasses(jobId);
        return String.join(",", variantTreeFilterClasses);
    }

    @GetMapping("/{id}/filterclasses/sample")
    public String getSampleTreeFilterClasses(@PathVariable("id") Integer jobId) {
        List<String> sampleTreeFilterClasses = jobService.getSampleTreeFilterClasses(jobId);
        return String.join(",", sampleTreeFilterClasses);
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<StreamingResponseBody> downloadJobReport(
            @PathVariable("id") Integer jobId) {
        FileDto fileDto = jobService.getJobReportFileById(jobId);
        return createFileDownloadResponse(fileDto);
    }

    @PatchMapping("/{id}/status/{status}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateJobStatus(
            @PathVariable("id") Integer id, @PathVariable("status") JobStatus status) {
        jobService.updateJobStatus(id, status);
    }

    /**
     * curl friendly endpoint to upload reports, e.g.
     *
     * <pre>curl --upload-file report.html <host>/api/job/${job_id}/report/</pre>
     */
    @PutMapping("/{id}/report/{filename}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadReport(
            HttpServletRequest httpServletRequest,
            @PathVariable("id") Integer jobId,
            @PathVariable("filename") String filename) {
        FileCreateDto fileCreateDto = FileUploadUtils.fromRequest(httpServletRequest, filename);
        jobService.uploadReport(jobId, fileCreateDto);
    }

    private ResponseEntity<StreamingResponseBody> createFileDownloadResponse(FileDto file) {
        return createFileDownloadResponse(file, file.getFilename());
    }

    private ResponseEntity<StreamingResponseBody> createFileDownloadResponse(
            FileDto file, String filename) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        builder.header(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(filename));
        builder.contentLength(file.getSize());

        return builder.body(
                outputStream -> {
                    WritableByteChannel outChannel = newChannel(outputStream);
                    try (ReadableByteChannel fromChannel = blobStore.newChannel(file.getBlobId())) {
                        ByteBuffer buf = ByteBuffer.wrap(new byte[8192]);
                        while (fromChannel.read(buf) != -1) {
                            buf.flip();
                            while (buf.hasRemaining()) {
                                outChannel.write(buf);
                            }
                            buf.clear();
                        }
                    }
                });
    }
}
