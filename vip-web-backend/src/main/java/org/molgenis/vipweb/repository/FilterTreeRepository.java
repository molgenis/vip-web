package org.molgenis.vipweb.repository;

import org.molgenis.vipweb.model.FilterTree;
import org.molgenis.vipweb.model.constants.FilterTreeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilterTreeRepository
        extends ListPagingAndSortingRepository<FilterTree, Integer>,
        ListCrudRepository<FilterTree, Integer> {
    Page<FilterTree> findAllByType(FilterTreeType type, Pageable pageable);

    Page<FilterTree> findAllByTypeAndIsPublic(
            FilterTreeType type, boolean isPublic, Pageable pageable);

    Page<FilterTree> findAllByTypeAndCreatedByOrTypeAndIsPublic(
            FilterTreeType typeCreatedBy,
            Integer createdById,
            FilterTreeType typeIsPublic,
            boolean isPublic,
            Pageable pageable);

    Optional<FilterTree> findByIdAndIsPublic(Integer vcfId, boolean isPublic);

    Optional<FilterTree> findByIdAndCreatedByOrIdAndIsPublic(
            Integer filterTreeIdCreatedBy,
            Integer createdById,
            Integer filterTreeIdIsPublic,
            boolean isPublic);
}
