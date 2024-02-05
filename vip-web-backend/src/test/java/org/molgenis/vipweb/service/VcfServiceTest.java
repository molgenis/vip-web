package org.molgenis.vipweb.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.BlobStore;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.VcfAnalyzer;
import org.molgenis.vipweb.VcfCreator;
import org.molgenis.vipweb.model.Vcf;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.VcfDto;
import org.molgenis.vipweb.model.mapper.VcfMapper;
import org.molgenis.vipweb.repository.VcfRepository;
import org.molgenis.vipweb.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class VcfServiceTest {
    @Mock
    private VcfRepository vcfRepository;
    @Mock
    private VcfMapper vcfMapper;
    @Mock
    private VcfAnalyzer vcfAnalyzer;
    @Mock
    private VcfCreator vcfCreator;
    @Mock
    private FileService fileService;
    @Mock
    private BlobStore blobStore;
    @Mock
    private AuthenticationFacade authenticationFacade;
    private VcfService vcfService;

    @BeforeEach
    void setUp() {
        vcfService =
                new VcfService(
                        vcfRepository,
                        vcfMapper,
                        vcfAnalyzer,
                        vcfCreator,
                        fileService,
                        blobStore,
                        authenticationFacade);
    }

    @Test
    void getVcfByIdAsUser() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer id = 2;
        Vcf vcf =
                when(mock(Vcf.class).getCreatedBy()).thenReturn(AggregateReference.to(userId)).getMock();
        when(vcfRepository.findByIdAndCreatedByOrIdAndIsPublic(2, userId, 2, true))
                .thenReturn(Optional.of(vcf));
        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfMapper.vcfToVcfDto(vcf, true)).thenReturn(vcfDto);
        assertEquals(vcfDto, vcfService.getVcfById(id));
    }

    @Test
    void getVcfByIdAsAdmin() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isAdmin()).thenReturn(true);

        Integer id = 2;
        Integer otherUserId = 3;
        Vcf vcf =
                when(mock(Vcf.class).getCreatedBy())
                        .thenReturn(AggregateReference.to(otherUserId))
                        .getMock();
        when(vcfRepository.findById(id)).thenReturn(Optional.of(vcf));
        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfMapper.vcfToVcfDto(vcf, false)).thenReturn(vcfDto);
        assertEquals(vcfDto, vcfService.getVcfById(id));
    }

    @Test
    void getVcfByIdAsVipbot() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isVipbot()).thenReturn(true);

        Integer id = 2;
        Vcf vcf =
                when(mock(Vcf.class).getCreatedBy()).thenReturn(AggregateReference.to(userId)).getMock();
        when(vcfRepository.findById(id)).thenReturn(Optional.of(vcf));
        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfMapper.vcfToVcfDto(vcf, true)).thenReturn(vcfDto);
        assertEquals(vcfDto, vcfService.getVcfById(id));
    }

    @Test
    void getVcfByIdAsAnonymous() {
        when(authenticationFacade.isAnonymousUser()).thenReturn(true);

        Integer id = 1;
        Vcf vcf = mock(Vcf.class);
        when(vcfRepository.findByIdAndIsPublic(id, true)).thenReturn(Optional.of(vcf));
        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfMapper.vcfToVcfDto(vcf, false)).thenReturn(vcfDto);
        assertEquals(vcfDto, vcfService.getVcfById(id));
    }

    @Test
    void getVcfByIdUnknownEntityException() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer id = 2;
        when(vcfRepository.findByIdAndCreatedByOrIdAndIsPublic(2, 1, 2, true))
                .thenReturn(Optional.empty());
        assertThrows(UnknownEntityException.class, () -> vcfService.getVcfById(id));
    }

    @Test
    void getVcfsAsUser() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Pageable pageable = mock(Pageable.class);

        Vcf vcf =
                when(mock(Vcf.class).getCreatedBy()).thenReturn(AggregateReference.to(userId)).getMock();
        Page<Vcf> vcfPage = new PageImpl<>(List.of(vcf));
        when(vcfRepository.findAllByCreatedByOrIsPublic(userId, true, pageable)).thenReturn(vcfPage);

        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfMapper.vcfToVcfDto(vcf, true)).thenReturn(vcfDto);
        assertEquals(new PageImpl<>(List.of(vcfDto)), vcfService.getVcfs(pageable));
    }

    @Test
    void getVcfsAsAdmin() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isAdmin()).thenReturn(true);

        Pageable pageable = mock(Pageable.class);

        Integer otherUserId = 2;
        Vcf vcf =
                when(mock(Vcf.class).getCreatedBy())
                        .thenReturn(AggregateReference.to(otherUserId))
                        .getMock();
        Page<Vcf> vcfPage = new PageImpl<>(List.of(vcf));
        when(vcfRepository.findAll(pageable)).thenReturn(vcfPage);

        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfMapper.vcfToVcfDto(vcf, false)).thenReturn(vcfDto);
        assertEquals(new PageImpl<>(List.of(vcfDto)), vcfService.getVcfs(pageable));
    }

    @Test
    void getVcfsAsVipbot() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isVipbot()).thenReturn(true);

        Pageable pageable = mock(Pageable.class);

        Vcf vcf =
                when(mock(Vcf.class).getCreatedBy()).thenReturn(AggregateReference.to(userId)).getMock();
        Page<Vcf> vcfPage = new PageImpl<>(List.of(vcf));
        when(vcfRepository.findAll(pageable)).thenReturn(vcfPage);

        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfMapper.vcfToVcfDto(vcf, true)).thenReturn(vcfDto);
        assertEquals(new PageImpl<>(List.of(vcfDto)), vcfService.getVcfs(pageable));
    }

    @Test
    void getVcfsAsAnonymous() {
        when(authenticationFacade.isAnonymousUser()).thenReturn(true);

        Pageable pageable = mock(Pageable.class);

        Vcf vcf = mock(Vcf.class);
        Page<Vcf> vcfPage = new PageImpl<>(List.of(vcf));
        when(vcfRepository.findAllByIsPublic(true, pageable)).thenReturn(vcfPage);

        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfMapper.vcfToVcfDto(vcf, false)).thenReturn(vcfDto);
        assertEquals(new PageImpl<>(List.of(vcfDto)), vcfService.getVcfs(pageable));
    }

    @Test
    void deleteVcf() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer vcfId = 2;
        Integer fileId = 3;
        Vcf vcf = mock(Vcf.class);
        when(vcf.getCreatedBy()).thenReturn(AggregateReference.to(userId));
        when(vcf.getFile()).thenReturn(AggregateReference.to(fileId));

        when(vcfRepository.findByIdAndCreatedByOrIdAndIsPublic(vcfId, userId, vcfId, true))
                .thenReturn(Optional.of(vcf));
        String blobId = "blobId";
        FileDto fileDto = when(mock(FileDto.class).getBlobId()).thenReturn(blobId).getMock();
        when(fileService.getFileById(fileId)).thenReturn(fileDto);

        vcfService.deleteVcf(vcfId);
        assertAll(
                () -> verify(vcfRepository).delete(vcf),
                () -> verify(fileService).deleteFileBytes(blobId),
                () -> verify(fileService).deleteFileById(fileId));
    }

    @Test
    void deleteVcfFromOtherUserAsUser() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer vcfId = 2;
        Integer otherUserId = 3;
        Vcf vcf =
                when(mock(Vcf.class).getCreatedBy())
                        .thenReturn(AggregateReference.to(otherUserId))
                        .getMock();

        when(vcfRepository.findByIdAndCreatedByOrIdAndIsPublic(vcfId, userId, vcfId, true))
                .thenReturn(Optional.of(vcf));
        assertThrows(AccessDeniedException.class, () -> vcfService.deleteVcf(vcfId));
    }

    @Test
    void deleteVcfFromOtherUserAsAdmin() {
        when(authenticationFacade.isAdmin()).thenReturn(true);

        Integer vcfId = 2;
        Integer fileId = 3;
        Vcf vcf = mock(Vcf.class);
        when(vcf.getFile()).thenReturn(AggregateReference.to(fileId));

        when(vcfRepository.findById(vcfId)).thenReturn(Optional.of(vcf));
        String blobId = "blobId";
        FileDto fileDto = when(mock(FileDto.class).getBlobId()).thenReturn(blobId).getMock();
        when(fileService.getFileById(fileId)).thenReturn(fileDto);

        vcfService.deleteVcf(vcfId);
        assertAll(
                () -> verify(vcfRepository).delete(vcf),
                () -> verify(fileService).deleteFileBytes(blobId),
                () -> verify(fileService).deleteFileById(fileId));
    }
}
