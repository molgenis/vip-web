package org.molgenis.vipweb.model.mapper;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.Sample;
import org.molgenis.vipweb.model.SampleHpoTerm;
import org.molgenis.vipweb.model.dto.HpoTermDto;
import org.molgenis.vipweb.model.dto.SampleCreateDto;
import org.molgenis.vipweb.model.dto.SampleDto;
import org.molgenis.vipweb.service.HpoService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SampleMapper {
    private final HpoService hpoService;

    public List<SampleDto> samplesToSampleDtos(List<Sample> samples) {
        return samples.stream().map(this::sampleToSampleDto).toList();
    }

    private SampleDto sampleToSampleDto(Sample sample) {
        List<Integer> hpoTermIds =
                sample.getHpoTerms().stream().map(SampleHpoTerm::getHpoTerm).toList();
        List<HpoTermDto> hpoTermDtos = hpoService.getHpoTermsByIds(hpoTermIds);

        return SampleDto.builder()
                .individualId(sample.getIndividualId())
                .paternalId(sample.getPaternalId())
                .maternalId(sample.getMaternalId())
                .proband(sample.getProband())
                .sex(sample.getSex())
                .affected(sample.getAffected())
                .hpoTerms(hpoTermDtos)
                .build();
    }

    public List<Sample> sampleCreateDtosToSamples(List<SampleCreateDto> samples) {
        return samples.stream().map(this::sampleCreateDtoToSample).toList();
    }

    private Sample sampleCreateDtoToSample(SampleCreateDto sample) {
        return Sample.builder()
                .individualId(sample.getIndividualId())
                .paternalId(sample.getPaternalId())
                .maternalId(sample.getMaternalId())
                .proband(sample.isProband())
                .sex(sample.getSex())
                .affected(sample.getAffected())
                .hpoTerms(hpoTermIdsToSampleHpoTerms(sample.getHpoTermIds()))
                .build();
    }

    private List<SampleHpoTerm> hpoTermIdsToSampleHpoTerms(List<Integer> hpoTermIds) {
        List<SampleHpoTerm> sampleHpoTerms;
        if (!hpoTermIds.isEmpty()) {
            List<HpoTermDto> hpoTermDtos = hpoService.getHpoTermsByIds(hpoTermIds);
            return hpoTermDtosToSampleHpoTerms(hpoTermDtos);
        } else {
            sampleHpoTerms = List.of();
        }
        return sampleHpoTerms;
    }

    private List<SampleHpoTerm> hpoTermDtosToSampleHpoTerms(List<HpoTermDto> hpoTermDtos) {
        return hpoTermDtos.stream()
                .map(hpoTermDto -> SampleHpoTerm.builder().hpoTerm(hpoTermDto.getId()).build())
                .toList();
    }
}
