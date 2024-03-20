package org.molgenis.vipweb.model.mapper;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.Vcf;
import org.molgenis.vipweb.model.VcfSample;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.VcfDto;
import org.molgenis.vipweb.model.dto.VcfSampleDto;
import org.molgenis.vipweb.service.FileService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class VcfMapper {
    private final FileService fileService;

    private static List<VcfSampleDto> vcfSampleToVcfSampleDto(List<VcfSample> vcfSamples) {
        return vcfSamples.stream().map(VcfMapper::vcfSampleToVcfSampleDto).toList();
    }

    private static VcfSampleDto vcfSampleToVcfSampleDto(VcfSample vcfSample) {
        return VcfSampleDto.builder().name(vcfSample.getName()).build();
    }

    public VcfDto vcfToVcfDto(Vcf vcf, boolean isOwner) {
        Integer fileId = Objects.requireNonNull(vcf.getFile().getId());
        FileDto fileDto = fileService.getFileById(fileId);

        return VcfDto.builder()
                .id(vcf.getId())
                .file(fileDto)
                .samples(vcfSampleToVcfSampleDto(vcf.getSamples()))
                .assembly(vcf.getAssembly())
                .isOwner(isOwner)
                .isPublic(vcf.isPublic())
                .build();
    }
}
