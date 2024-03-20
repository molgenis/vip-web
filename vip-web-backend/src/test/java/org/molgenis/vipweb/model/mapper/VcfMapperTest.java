package org.molgenis.vipweb.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.Vcf;
import org.molgenis.vipweb.model.VcfSample;
import org.molgenis.vipweb.model.constants.Assembly;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.VcfDto;
import org.molgenis.vipweb.model.dto.VcfSampleDto;
import org.molgenis.vipweb.service.FileService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

@ExtendWith(MockitoExtension.class)
class VcfMapperTest {
  @Mock private FileService fileService;
  private VcfMapper vcfMapper;

  @BeforeEach
  void setUp() {
    vcfMapper = new VcfMapper(fileService);
  }

  @Test
  void vcfSampleToVcfSampleDto() {
    Integer vcfId = 1;
    Integer fileId = 2;
    String vcfSample0Name = "vcfSample0";
    String vcfSample1Name = "vcfSample1";
    VcfSample vcfSample0 = VcfSample.builder().name(vcfSample0Name).build();
    VcfSample vcfSample1 = VcfSample.builder().name(vcfSample1Name).build();
    Assembly assembly = Assembly.GRCh38;
    boolean isPublic = true;

    Vcf vcf =
        Vcf.builder()
            .id(vcfId)
            .file(AggregateReference.to(fileId))
            .samples(List.of(vcfSample0, vcfSample1))
            .assembly(assembly)
            .isPublic(isPublic)
            .build();

    FileDto fileDto = mock(FileDto.class);
    when(fileService.getFileById(fileId)).thenReturn(fileDto);

    boolean isOwner = true;
    VcfDto vcfDto =
        VcfDto.builder()
            .id(vcfId)
            .file(fileDto)
            .samples(
                List.of(
                    VcfSampleDto.builder().name(vcfSample0Name).build(),
                    VcfSampleDto.builder().name(vcfSample1Name).build()))
            .assembly(assembly)
            .isOwner(isOwner)
            .isPublic(isPublic)
            .build();
    assertEquals(vcfDto, vcfMapper.vcfToVcfDto(vcf, isOwner));
  }
}
