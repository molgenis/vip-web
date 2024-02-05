package org.molgenis.vipweb.model.mapper;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.HpoTerm;
import org.molgenis.vipweb.model.dto.HpoTermDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HpoTermMapper {
    public HpoTermDto hpoTermToHpoTermDto(HpoTerm hpoTerm) {
        return HpoTermDto.builder()
                .id(hpoTerm.getId())
                .term(hpoTerm.getTerm())
                .name(hpoTerm.getName())
                .build();
    }

    public List<HpoTermDto> hpoTermsToHpoTermDtos(List<HpoTerm> hpoTerms) {
        return hpoTerms.stream().map(this::hpoTermToHpoTermDto).toList();
    }
}
