package org.molgenis.vipweb.service;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.Blob;
import org.molgenis.vipweb.SampleSheetGenerator;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.Job;
import org.molgenis.vipweb.model.Report;
import org.molgenis.vipweb.model.constants.JobStatus;
import org.molgenis.vipweb.model.dto.*;
import org.molgenis.vipweb.model.mapper.JobMapper;
import org.molgenis.vipweb.repository.JobRepository;
import org.molgenis.vipweb.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobMapper jobMapper;
    private final JobRepository jobRepository;
    private final FileService fileService;
    private final VcfService vcfService;
    private final SampleSheetGenerator sampleSheetGenerator;
    private final AuthenticationFacade authenticationFacade;

    @Transactional
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public JobDto createJob(JobCreateDto jobCreateDto) {
        Job newJob = jobMapper.jobCreateDtoToJob(jobCreateDto);
        newJob = jobRepository.save(newJob);
        return jobMapper.jobToJobDto(newJob, true);
    }

    @Transactional(readOnly = true)
    public JobDto getJobById(Integer id) {
        return getJobDto(id);
    }

    @Transactional(readOnly = true)
    public Page<JobDto> getJobs(Pageable pageable) {
        Page<Job> jobs =
                (authenticationFacade.isAdmin() || authenticationFacade.isVipbot())
                        ? jobRepository.findAll(pageable)
                        : authenticationFacade.isAnonymousUser()
                        ? jobRepository.findAllByIsPublic(true, pageable)
                        : jobRepository.findAllByCreatedByOrIsPublic(
                        authenticationFacade.getUserId(), true, pageable);

        return jobs.map(this::jobToJobDto);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public void deleteJob(Integer id) {
        Job job = getJob(id);
        Integer createdById = Objects.requireNonNull(job.getCreatedBy().getId());
        if (!createdById.equals(authenticationFacade.getUserId()) && !authenticationFacade.isAdmin()) {
            throw new AccessDeniedException("Access Denied");
        }

        Report report = job.getReport();
        Integer fileId = null;
        FileDto fileDto = null;
        if (report != null) {
            fileId = job.getReport().getFile().getId();
            fileDto = fileService.getFileById(fileId);
        }

        if (report != null) fileService.deleteFileBytes(fileDto.getBlobId());
        jobRepository.delete(job);
        if (fileId != null) fileService.deleteFileById(fileId);

        Integer vcfId = Objects.requireNonNull(job.getVcf().getId());
        VcfDto vcfDto = vcfService.getVcfById(vcfId);
        if (authenticationFacade.isAdmin() || vcfDto.getIsOwner()) {
            long nrJobsWithVcf = jobRepository.countByVcf(vcfId);
            if (nrJobsWithVcf == 0) {
                vcfService.deleteVcf(vcfId);
            }
        }
    }

    @Transactional(readOnly = true)
    public FileDto getJobVcfFileById(Integer id) {
        JobDto jobDto = getJobDto(id);
        return jobDto.getVcf().getFile();
    }

    @Transactional(readOnly = true)
    public FileDto getJobVariantFilterTreeFileById(Integer id) {
        JobDto jobDto = getJobDto(id);
        return jobDto.getVariantFilterTree().getFile();
    }

    @Transactional(readOnly = true)
    public FileDto getJobSampleFilterTreeFileById(Integer id) {
        JobDto jobDto = getJobDto(id);
        return jobDto.getSampleFilterTree().getFile();
    }

    @Transactional(readOnly = true)
    public FileDto getJobReportFileById(Integer id) {
        JobDto jobDto = getJobDto(id);
        return jobDto.getReport().getFile();
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_VIPBOT') or hasRole('ROLE_ADMIN')")
    public synchronized Optional<Integer> getAndClaimJob() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("creationDate"));
        Page<Job> jobs = jobRepository.findAllByStatus(JobStatus.PENDING, pageable);

        Optional<Integer> optionalJobId;
        if (jobs.getNumberOfElements() == 0) {
            optionalJobId = Optional.empty();
        } else {
            Job job = jobs.stream().findAny().orElseThrow(IllegalArgumentException::new);
            Job newJob = job.toBuilder().status(JobStatus.RUNNING).build();
            newJob = jobRepository.save(newJob);
            optionalJobId = Optional.of(newJob.getId());
        }

        return optionalJobId;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_VIPBOT') or hasRole('ROLE_ADMIN')")
    public byte[] getJobSampleSheetBytesById(Integer jobId) {
        JobDto jobDto = getJobDto(jobId);
        String sampleSheetStr = sampleSheetGenerator.generate(jobDto);
        return sampleSheetStr.getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_VIPBOT') or hasRole('ROLE_ADMIN')")
    public List<String> getVariantTreeFilterClasses(Integer jobId) {
        JobDto jobDto = getJobDto(jobId);
        FilterTreeDto filterTree = jobDto.getVariantFilterTree();

        List<Integer> filterClassIds = jobDto.getVariantFilterClassIds();
        return filterTree.getClasses().stream()
                .filter(filterTreeClass -> filterClassIds.contains(filterTreeClass.getId()))
                .map(FilterTreeClassDto::getName)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_VIPBOT') or hasRole('ROLE_ADMIN')")
    public List<String> getSampleTreeFilterClasses(Integer jobId) {
        JobDto jobDto = getJobDto(jobId);
        FilterTreeDto filterTree = jobDto.getSampleFilterTree();

        List<Integer> filterClassIds = jobDto.getSampleFilterClassIds();
        return filterTree.getClasses().stream()
                .filter(filterTreeClass -> filterClassIds.contains(filterTreeClass.getId()))
                .map(FilterTreeClassDto::getName)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_VIPBOT') or hasRole('ROLE_ADMIN')")
    public void uploadReport(Integer jobId, FileCreateDto fileCreateDto) {
        Job job = jobRepository.findById(jobId).orElseThrow(UnknownEntityException::new);

        // ignore supplied filename and create a filename from the input vcf
        VcfDto vcfDto = vcfService.getVcfById(Objects.requireNonNull(job.getVcf().getId()));
        String filename = createReportFilename(vcfDto.getFile().getFilename(), job);
        fileCreateDto = fileCreateDto.toBuilder().filename(filename).build();

        // persist bytes and update job
        Blob blob = fileService.createFileBytes(fileCreateDto);

        try {
            FileDto fileDto = fileService.createFile(fileCreateDto, blob);

            Job newJob =
                    job.toBuilder()
                            .report(Report.builder().file(AggregateReference.to(fileDto.getId())).build())
                            .status(JobStatus.COMPLETED)
                            .build();
            jobRepository.save(newJob);
        } catch (RuntimeException e) {
            fileService.deleteFileBytes(blob.getId());
            throw e;
        }
    }

    private static String createReportFilename(String filename, Job job) {
        String reportFilename;
        if (filename.endsWith(".vcf.gz")) {
            reportFilename = filename.substring(0, filename.length() - ".vcf.gz".length());
        } else if (filename.endsWith(".vcf.bgz")) {
            reportFilename = filename.substring(0, filename.length() - ".vcf.bgz".length());
        } else if (filename.endsWith(".vcf")) {
            reportFilename = filename.substring(0, filename.length() - ".vcf".length());
        } else {
            reportFilename = filename;
        }
        reportFilename += "_" + job.getSequencingMethod().name() + "_" + job.getAssembly() + ".html";
        return reportFilename;
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_VIPBOT') or hasRole('ROLE_ADMIN')")
    public void updateJobStatus(Integer id, JobStatus status) {
        Job job = jobRepository.findById(id).orElseThrow(UnknownEntityException::new);
        Job newJob = job.toBuilder().status(status).build();
        jobRepository.save(newJob);
    }

    private JobDto getJobDto(Integer id) {
        Job job = getJob(id);
        return jobToJobDto(job);
    }

    private JobDto jobToJobDto(Job job) {
        boolean isOwner =
                !authenticationFacade.isAnonymousUser()
                        && authenticationFacade
                        .getUserId()
                        .equals(Objects.requireNonNull(job.getCreatedBy().getId()));
        return jobMapper.jobToJobDto(job, isOwner);
    }

    private Job getJob(Integer id) {
        Optional<Job> jobOptional;
        if (authenticationFacade.isAdmin() || authenticationFacade.isVipbot()) {
            jobOptional = jobRepository.findById(id);
        } else if (authenticationFacade.isAnonymousUser()) {
            jobOptional = jobRepository.findByIdAndIsPublic(id, true);
        } else {
            jobOptional =
                    jobRepository.findByIdAndCreatedByOrIdAndIsPublic(
                            id, authenticationFacade.getUserId(), id, true);
        }
        return jobOptional.orElseThrow(UnknownEntityException::new);
    }
}
