package org.molgenis.vipweb.repository;

import org.molgenis.vipweb.model.Job;
import org.molgenis.vipweb.model.constants.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository
        extends ListCrudRepository<Job, Integer>, ListPagingAndSortingRepository<Job, Integer> {

    Optional<Job> findByIdAndCreatedByOrIdAndIsPublic(
            Integer jobId, Integer createdById, Integer jobIdIsPublic, boolean isPublic);

    Page<Job> findAllByCreatedByOrIsPublic(Integer createdById, boolean isPublic, Pageable pageable);

    Page<Job> findAllByStatus(JobStatus status, Pageable pageable);

    Page<Job> findAllByIsPublic(boolean isPublic, Pageable pageable);

    Optional<Job> findByIdAndIsPublic(Integer jobId, boolean isPublic);

    long countByVcf(Integer vcfId);
}
