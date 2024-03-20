package org.molgenis.vipweb.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipweb.model.HpoTerm;
import org.molgenis.vipweb.model.dto.HpoTermDto;

class HpoTermMapperTest {
    private HpoTermMapper hpoTermMapper;

    @BeforeEach
    void setUp() {
        hpoTermMapper = new HpoTermMapper();
    }

    @Test
    void hpoTermToHpoTermDto() {
        int hpoTermId = 1;
        String hpoTermTerm = "term";
        String hpoTermName = "termName";
        HpoTerm hpoTerm = HpoTerm.builder().id(hpoTermId).term(hpoTermTerm).name(hpoTermName).build();

        HpoTermDto hpoTermDto =
                HpoTermDto.builder().id(hpoTermId).term(hpoTermTerm).name(hpoTermName).build();
        assertEquals(hpoTermDto, hpoTermMapper.hpoTermToHpoTermDto(hpoTerm));
    }

    @Test
    void hpoTermsToHpoTermDtos() {
        // 1
        int hpoTerm1Id = 1;
        String hpoTerm1Term = "term";
        String hpoTerm1Name = "termName";
        HpoTerm hpoTerm1 =
                HpoTerm.builder().id(hpoTerm1Id).term(hpoTerm1Term).name(hpoTerm1Name).build();

        HpoTermDto hpoTermDto1 =
                HpoTermDto.builder().id(hpoTerm1Id).term(hpoTerm1Term).name(hpoTerm1Name).build();

        // 2
        int hpoTerm2Id = 2;
        String hpoTerm2Term = "term";
        String hpoTerm2Name = "termName";
        HpoTerm hpoTerm2 =
                HpoTerm.builder().id(hpoTerm2Id).term(hpoTerm2Term).name(hpoTerm2Name).build();

        HpoTermDto hpoTermDto2 =
                HpoTermDto.builder().id(hpoTerm2Id).term(hpoTerm2Term).name(hpoTerm2Name).build();

        assertEquals(
                List.of(hpoTermDto1, hpoTermDto2),
                hpoTermMapper.hpoTermsToHpoTermDtos(List.of(hpoTerm1, hpoTerm2)));
    }
}
