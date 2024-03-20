package org.molgenis.vipweb.repository;

import org.molgenis.vipweb.model.HpoTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.Optional;

public interface HpoRepository
        extends ListPagingAndSortingRepository<HpoTerm, Integer>, ListCrudRepository<HpoTerm, Integer> {
    Optional<HpoTerm> findByTerm(String term);

    Page<HpoTerm> findByTermContainingIgnoreCaseOrNameContainingIgnoreCase(
            String term, String name, Pageable pageable);
}
