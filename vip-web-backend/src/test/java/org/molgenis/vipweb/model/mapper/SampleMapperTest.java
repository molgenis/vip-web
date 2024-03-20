package org.molgenis.vipweb.model.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.Sample;
import org.molgenis.vipweb.model.SampleHpoTerm;
import org.molgenis.vipweb.model.constants.AffectedStatus;
import org.molgenis.vipweb.model.constants.Sex;
import org.molgenis.vipweb.model.dto.HpoTermDto;
import org.molgenis.vipweb.model.dto.SampleCreateDto;
import org.molgenis.vipweb.model.dto.SampleDto;
import org.molgenis.vipweb.service.HpoService;

@ExtendWith(MockitoExtension.class)
class SampleMapperTest {
  @Mock private HpoService hpoService;
  private SampleMapper sampleMapper;

  @BeforeEach
  void setUp() {
    sampleMapper = new SampleMapper(hpoService);
  }

  @Test
  void samplesToSampleDtos() {
    Integer hpoTermId = 2;
    SampleHpoTerm sampleHpoTerm = SampleHpoTerm.builder().hpoTerm(hpoTermId).build();
    String hpoTermTerm = "term";
    String hpoTermName = "termName";
    HpoTermDto hpoTermDto =
        HpoTermDto.builder().id(hpoTermId).term(hpoTermTerm).name(hpoTermName).build();
    when(hpoService.getHpoTermsByIds(List.of(hpoTermId))).thenReturn(List.of(hpoTermDto));

    Integer id = 1;
    String individualId = "sample";
    String paternalId = "father";
    String maternalId = "mother";
    boolean proband = true;
    Sex sex = Sex.MALE;
    AffectedStatus affected = AffectedStatus.TRUE;

    Sample sample =
        Sample.builder()
            .id(id)
            .individualId(individualId)
            .paternalId(paternalId)
            .maternalId(maternalId)
            .proband(proband)
            .sex(sex)
            .affected(affected)
            .hpoTerms(List.of(sampleHpoTerm))
            .build();

    SampleDto sampleDto =
        SampleDto.builder()
            .individualId(individualId)
            .paternalId(paternalId)
            .maternalId(maternalId)
            .proband(proband)
            .sex(sex)
            .affected(affected)
            .hpoTerms(List.of(hpoTermDto))
            .build();
    assertEquals(List.of(sampleDto), sampleMapper.samplesToSampleDtos(List.of(sample)));
  }

  @Test
  void sampleCreateDtosToSamples() {
    // minimal sample with only non-null fields and empty hpo term list
    String sample1IndividualId = "individualId1";
    Sex sample1Sex = Sex.MALE;
    AffectedStatus sample1AffectedStatus = AffectedStatus.TRUE;

    SampleCreateDto sampleCreateDto1 =
        SampleCreateDto.builder()
            .individualId(sample1IndividualId)
            .sex(sample1Sex)
            .affected(sample1AffectedStatus)
            .hpoTermIds(List.of())
            .build();

    // complete sample
    String sample2IndividualId = "individualId2";
    String sample2PaternalId = "paternalId2";
    String sample2MaternalId = "maternalId2";
    boolean sample2Proband = true;
    Sex sample2Sex = Sex.FEMALE;
    AffectedStatus sample2AffectedStatus = AffectedStatus.FALSE;
    Integer sample2HpoTerm1 = 1;
    Integer sample2HpoTerm2 = 2;
    List<Integer> sample2HpoTermIds = List.of(sample2HpoTerm1, sample2HpoTerm2);

    SampleCreateDto sampleCreateDto2 =
        SampleCreateDto.builder()
            .individualId(sample2IndividualId)
            .paternalId(sample2PaternalId)
            .maternalId(sample2MaternalId)
            .proband(sample2Proband)
            .sex(sample2Sex)
            .affected(sample2AffectedStatus)
            .hpoTermIds(sample2HpoTermIds)
            .build();

    List<HpoTermDto> sample2SampleHpoTerms =
        List.of(
            when(mock(HpoTermDto.class).getId()).thenReturn(sample2HpoTerm1).getMock(),
            when(mock(HpoTermDto.class).getId()).thenReturn(sample2HpoTerm2).getMock());
    when(hpoService.getHpoTermsByIds(sample2HpoTermIds)).thenReturn(sample2SampleHpoTerms);

    List<Sample> samples =
        List.of(
            Sample.builder()
                .individualId(sample1IndividualId)
                .proband(false)
                .sex(sample1Sex)
                .affected(sample1AffectedStatus)
                .hpoTerms(List.of())
                .build(),
            Sample.builder()
                .individualId(sample2IndividualId)
                .paternalId(sample2PaternalId)
                .maternalId(sample2MaternalId)
                .proband(sample2Proband)
                .sex(sample2Sex)
                .affected(sample2AffectedStatus)
                .hpoTerms(
                    List.of(
                        SampleHpoTerm.builder().hpoTerm(sample2HpoTerm1).build(),
                        SampleHpoTerm.builder().hpoTerm(sample2HpoTerm2).build()))
                .build());
    assertEquals(
        samples,
        sampleMapper.sampleCreateDtosToSamples(List.of(sampleCreateDto1, sampleCreateDto2)));
  }
}
