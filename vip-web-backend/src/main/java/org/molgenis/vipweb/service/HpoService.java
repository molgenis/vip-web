package org.molgenis.vipweb.service;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.HpoTerm;
import org.molgenis.vipweb.model.dto.HpoTermDto;
import org.molgenis.vipweb.model.mapper.HpoTermMapper;
import org.molgenis.vipweb.repository.HpoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HpoService {
    private final HpoRepository hpoRepository;
    private final HpoTermMapper hpoTermMapper;

    @Transactional(readOnly = true)
    public List<HpoTermDto> getHpoTermsByIds(List<Integer> hpoTermIds) {
        List<HpoTerm> hpoTerms = hpoRepository.findAllById(hpoTermIds);
        return hpoTermMapper.hpoTermsToHpoTermDtos(hpoTerms);
    }

    @Transactional(readOnly = true)
    public HpoTermDto findByTerm(String term) {
        HpoTerm hpoTerm = hpoRepository.findByTerm(term).orElseThrow(UnknownEntityException::new);
        return hpoTermMapper.hpoTermToHpoTermDto(hpoTerm);
    }

    @Transactional(readOnly = true)
    public Page<HpoTermDto> getHpoTerms(String query) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("term"));
        Page<HpoTerm> hpoTerms =
                hpoRepository.findByTermContainingIgnoreCaseOrNameContainingIgnoreCase(
                        query, query, pageable);
        return hpoTerms.map(hpoTermMapper::hpoTermToHpoTermDto);
    }
}
