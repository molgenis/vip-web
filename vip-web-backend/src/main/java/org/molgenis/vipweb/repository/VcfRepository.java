package org.molgenis.vipweb.repository;

import org.molgenis.vipweb.model.Vcf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VcfRepository extends ListCrudRepository<Vcf, Integer> {
    Optional<Vcf> findByIdAndIsPublic(Integer vcfId, boolean isPublic);

    Optional<Vcf> findByIdAndCreatedByOrIdAndIsPublic(
            Integer vcfIdCreatedBy, Integer createdById, Integer vcfIdIsPublic, boolean isPublic);

    Page<Vcf> findAll(Pageable pageable);

    Page<Vcf> findAllByIsPublic(boolean isPublic, Pageable pageable);

    Page<Vcf> findAllByCreatedByOrIsPublic(Integer userId, boolean isPublic, Pageable pageable);
}
