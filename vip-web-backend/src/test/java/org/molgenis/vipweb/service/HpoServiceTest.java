package org.molgenis.vipweb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.HpoTerm;
import org.molgenis.vipweb.model.dto.HpoTermDto;
import org.molgenis.vipweb.model.mapper.HpoTermMapper;
import org.molgenis.vipweb.repository.HpoRepository;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HpoServiceTest {
    @Mock
    private HpoRepository hpoRepository;
    @Mock
    private HpoTermMapper hpoTermMapper;
    private HpoService hpoService;

    @BeforeEach
    void setUp() {
        hpoService = new HpoService(hpoRepository, hpoTermMapper);
    }

    @Test
    void findByTerm() {
        String term = "term";

        HpoTerm hpoTerm = mock(HpoTerm.class);
        when(hpoRepository.findByTerm(term)).thenReturn(Optional.of(hpoTerm));
        HpoTermDto hpoTermDto = mock(HpoTermDto.class);
        when(hpoTermMapper.hpoTermToHpoTermDto(hpoTerm)).thenReturn(hpoTermDto);

        assertEquals(hpoTermDto, hpoService.findByTerm(term));
    }

    @Test
    void findByTermUnknownEntityException() {
        String term = "term";
        when(hpoRepository.findByTerm(term)).thenReturn(Optional.empty());
        assertThrows(UnknownEntityException.class, () -> hpoService.findByTerm(term));
    }

    @Test
    void getHpoTerms() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("term"));
        String query = "query";
        HpoTerm hpoTerm = mock(HpoTerm.class);
        Page<HpoTerm> hpoTerms = new PageImpl<>(List.of(hpoTerm));
        HpoTermDto hpoTermDto = mock(HpoTermDto.class);
        Page<HpoTermDto> hpoTermDtos = new PageImpl<>(List.of(hpoTermDto));
        when(hpoTermMapper.hpoTermToHpoTermDto(hpoTerm)).thenReturn(hpoTermDto);
        when(hpoRepository.findByTermContainingIgnoreCaseOrNameContainingIgnoreCase(
                query, query, pageable))
                .thenReturn(hpoTerms);
        assertEquals(hpoTermDtos, hpoService.getHpoTerms(query));
    }
}
