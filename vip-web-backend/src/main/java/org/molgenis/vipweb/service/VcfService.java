package org.molgenis.vipweb.service;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.*;
import org.molgenis.vipweb.model.Vcf;
import org.molgenis.vipweb.model.VcfSample;
import org.molgenis.vipweb.model.dto.FileCreateDto;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.VcfCreateDto;
import org.molgenis.vipweb.model.dto.VcfDto;
import org.molgenis.vipweb.model.mapper.VcfMapper;
import org.molgenis.vipweb.repository.VcfRepository;
import org.molgenis.vipweb.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VcfService {
    private final VcfRepository vcfRepository;
    private final VcfMapper vcfMapper;
    private final VcfAnalyzer vcfAnalyzer;
    private final VcfCreator vcfCreator;
    private final FileService fileService;
    private final BlobStore blobStore;
    private final AuthenticationFacade authenticationFacade;

    private static List<VcfSample> createSamples(List<String> sampleNames) {
        return sampleNames.stream()
                .map(sampleName -> VcfSample.builder().name(sampleName).build())
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public VcfDto upload(FileCreateDto fileCreateDto) {
        return uploadVcf(fileCreateDto);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public VcfDto create(VcfCreateDto vcf) {
        String vcfStr = vcfCreator.create(vcf.getVariants());

        FileCreateDto fileCreateDto =
                FileCreateDto.builder()
                        .filename("variants.vcf")
                        .readableByteChannel(
                                Channels.newChannel(
                                        new ByteArrayInputStream(vcfStr.getBytes(StandardCharsets.UTF_8))))
                        .build();
        return uploadVcf(fileCreateDto);
    }

    private VcfDto uploadVcf(FileCreateDto fileCreateDto) {
        Blob blob = fileService.createFileBytes(fileCreateDto);

        try {
            FileDto fileDto = fileService.createFile(fileCreateDto, blob);
            Path vcfPath = blobStore.getPath(fileDto.getBlobId());
            AnalyzedVcf analyzedVcf = vcfAnalyzer.analyze(vcfPath);

            Vcf vcf =
                    Vcf.builder()
                            .file(AggregateReference.to(fileDto.getId()))
                            .samples(createSamples(analyzedVcf.getSampleNames()))
                            .assembly(analyzedVcf.getPredictedAssembly())
                            .isPublic(fileCreateDto.getIsPublic() != null && fileCreateDto.getIsPublic())
                            .build();
            vcf = vcfRepository.save(vcf);

            return vcfToVcfDto(vcf);
        } catch (RuntimeException e) {
            fileService.deleteFileBytes(blob.getId());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public VcfDto getVcfById(Integer id) {
        return getVcfDto(id);
    }

    @Transactional(readOnly = true)
    public Page<VcfDto> getVcfs(Pageable pageable) {
        Page<Vcf> vcfs =
                (authenticationFacade.isAdmin() || authenticationFacade.isVipbot())
                        ? vcfRepository.findAll(pageable)
                        : authenticationFacade.isAnonymousUser()
                        ? vcfRepository.findAllByIsPublic(true, pageable)
                        : vcfRepository.findAllByCreatedByOrIsPublic(
                        authenticationFacade.getUserId(), true, pageable);

        return vcfs.map(this::vcfToVcfDto);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public void deleteVcf(Integer id) {
        Vcf vcf = getVcf(id);
        if (!authenticationFacade.isAdmin()
                && !authenticationFacade
                .getUserId()
                .equals(Objects.requireNonNull(vcf.getCreatedBy().getId()))) {
            throw new AccessDeniedException("Access Denied");
        }

        Integer fileId = Objects.requireNonNull(vcf.getFile().getId());
        FileDto fileDto = fileService.getFileById(fileId);

        fileService.deleteFileBytes(fileDto.getBlobId());
        vcfRepository.delete(vcf);
        fileService.deleteFileById(fileId);
    }

    private VcfDto getVcfDto(Integer id) {
        Vcf vcf = getVcf(id);
        return vcfToVcfDto(vcf);
    }

    private VcfDto vcfToVcfDto(Vcf vcf) {
        boolean isOwner =
                !authenticationFacade.isAnonymousUser()
                        && authenticationFacade
                        .getUserId()
                        .equals(Objects.requireNonNull(vcf.getCreatedBy().getId()));
        return vcfMapper.vcfToVcfDto(vcf, isOwner);
    }

    private Vcf getVcf(Integer id) {
        Optional<Vcf> optionalVcf;
        if (authenticationFacade.isAdmin() || authenticationFacade.isVipbot()) {
            optionalVcf = vcfRepository.findById(id);
        } else if (authenticationFacade.isAnonymousUser()) {
            optionalVcf = vcfRepository.findByIdAndIsPublic(id, true);
        } else {
            optionalVcf =
                    vcfRepository.findByIdAndCreatedByOrIdAndIsPublic(
                            id, authenticationFacade.getUserId(), id, true);
        }
        return optionalVcf.orElseThrow(UnknownEntityException::new);
    }
}
