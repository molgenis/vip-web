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
import org.molgenis.vipweb.FilterTreeParser;
import org.molgenis.vipweb.model.FilterTree;
import org.molgenis.vipweb.model.constants.FilterTreeType;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.FilterTreeDto;
import org.molgenis.vipweb.model.mapper.FilterTreeMapper;
import org.molgenis.vipweb.repository.FilterTreeRepository;
import org.molgenis.vipweb.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class FilterTreeServiceTest {
    @Mock
    private FilterTreeRepository filterTreeRepository;
    @Mock
    private FilterTreeMapper filterTreeMapper;
    @Mock
    private FilterTreeParser filterTreeParser;
    @Mock
    private FileService fileService;
    @Mock
    private AuthenticationFacade authenticationFacade;
    private FilterTreeService filterTreeService;

    @BeforeEach
    void setUp() {
        filterTreeService =
                new FilterTreeService(
                        filterTreeRepository,
                        filterTreeMapper,
                        filterTreeParser,
                        fileService,
                        authenticationFacade);
    }

    @Test
    void getFilterTreeByIdAsUser() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer id = 2;
        FilterTree filterTree = mock(FilterTree.class);
        when(filterTreeRepository.findByIdAndCreatedByOrIdAndIsPublic(2, 1, 2, true))
                .thenReturn(Optional.of(filterTree));
        FilterTreeDto filterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeMapper.filterTreeToFilterTreeDto(filterTree)).thenReturn(filterTreeDto);
        assertEquals(filterTreeDto, filterTreeService.getFilterTreeById(id));
    }

    @Test
    void getFilterTreeByIdAsAdmin() {
        when(authenticationFacade.isAdmin()).thenReturn(true);

        Integer id = 2;
        FilterTree filterTree = mock(FilterTree.class);
        when(filterTreeRepository.findById(id)).thenReturn(Optional.of(filterTree));
        FilterTreeDto filterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeMapper.filterTreeToFilterTreeDto(filterTree)).thenReturn(filterTreeDto);
        assertEquals(filterTreeDto, filterTreeService.getFilterTreeById(id));
    }

    @Test
    void getFilterTreeByIdAsVipbot() {
        when(authenticationFacade.isVipbot()).thenReturn(true);

        Integer id = 2;
        FilterTree filterTree = mock(FilterTree.class);
        when(filterTreeRepository.findById(id)).thenReturn(Optional.of(filterTree));
        FilterTreeDto filterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeMapper.filterTreeToFilterTreeDto(filterTree)).thenReturn(filterTreeDto);
        assertEquals(filterTreeDto, filterTreeService.getFilterTreeById(id));
    }

    @Test
    void getFilterTreeByIdAsAnonymous() {
        when(authenticationFacade.isAnonymousUser()).thenReturn(true);

        Integer id = 2;
        FilterTree filterTree = mock(FilterTree.class);
        when(filterTreeRepository.findByIdAndIsPublic(id, true)).thenReturn(Optional.of(filterTree));
        FilterTreeDto filterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeMapper.filterTreeToFilterTreeDto(filterTree)).thenReturn(filterTreeDto);
        assertEquals(filterTreeDto, filterTreeService.getFilterTreeById(id));
    }

    @Test
    void getFilterTreesAsUser() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        FilterTreeType filterTreeType = FilterTreeType.VARIANT;
        Pageable pageable = mock(Pageable.class);

        FilterTree filterTree = mock(FilterTree.class);
        Page<FilterTree> filterTreePage = new PageImpl<>(List.of(filterTree));
        when(filterTreeRepository.findAllByTypeAndCreatedByOrTypeAndIsPublic(
                filterTreeType, userId, filterTreeType, true, pageable))
                .thenReturn(filterTreePage);

        FilterTreeDto filterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeMapper.filterTreeToFilterTreeDto(filterTree)).thenReturn(filterTreeDto);
        assertEquals(
                new PageImpl<>(List.of(filterTreeDto)),
                filterTreeService.getFilterTrees(filterTreeType, pageable));
    }

    @Test
    void getFilterTreesAsAdmin() {
        when(authenticationFacade.isAdmin()).thenReturn(true);

        FilterTreeType filterTreeType = FilterTreeType.VARIANT;
        Pageable pageable = mock(Pageable.class);

        FilterTree filterTree = mock(FilterTree.class);
        Page<FilterTree> filterTreePage = new PageImpl<>(List.of(filterTree));
        when(filterTreeRepository.findAllByType(filterTreeType, pageable)).thenReturn(filterTreePage);

        FilterTreeDto filterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeMapper.filterTreeToFilterTreeDto(filterTree)).thenReturn(filterTreeDto);
        assertEquals(
                new PageImpl<>(List.of(filterTreeDto)),
                filterTreeService.getFilterTrees(filterTreeType, pageable));
    }

    @Test
    void getFilterTreesAsVipbot() {
        when(authenticationFacade.isVipbot()).thenReturn(true);

        FilterTreeType filterTreeType = FilterTreeType.VARIANT;
        Pageable pageable = mock(Pageable.class);

        FilterTree filterTree = mock(FilterTree.class);
        Page<FilterTree> filterTreePage = new PageImpl<>(List.of(filterTree));
        when(filterTreeRepository.findAllByType(filterTreeType, pageable)).thenReturn(filterTreePage);

        FilterTreeDto filterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeMapper.filterTreeToFilterTreeDto(filterTree)).thenReturn(filterTreeDto);
        assertEquals(
                new PageImpl<>(List.of(filterTreeDto)),
                filterTreeService.getFilterTrees(filterTreeType, pageable));
    }

    @Test
    void getFilterTreesAsAnonymous() {
        when(authenticationFacade.isAnonymousUser()).thenReturn(true);

        FilterTreeType filterTreeType = FilterTreeType.SAMPLE;
        Pageable pageable = mock(Pageable.class);

        FilterTree filterTree = mock(FilterTree.class);
        Page<FilterTree> filterTreePage = new PageImpl<>(List.of(filterTree));
        when(filterTreeRepository.findAllByTypeAndIsPublic(filterTreeType, true, pageable))
                .thenReturn(filterTreePage);

        FilterTreeDto filterTreeDto = mock(FilterTreeDto.class);
        when(filterTreeMapper.filterTreeToFilterTreeDto(filterTree)).thenReturn(filterTreeDto);
        assertEquals(
                new PageImpl<>(List.of(filterTreeDto)),
                filterTreeService.getFilterTrees(filterTreeType, pageable));
    }

    @Test
    void deleteFilterTree() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer filterTreeId = 2;
        Integer fileId = 3;
        FilterTree filterTree = mock(FilterTree.class);
        when(filterTree.getCreatedBy()).thenReturn(AggregateReference.to(userId));
        when(filterTree.getFile()).thenReturn(AggregateReference.to(fileId));

        when(filterTreeRepository.findByIdAndCreatedByOrIdAndIsPublic(
                filterTreeId, userId, filterTreeId, true))
                .thenReturn(Optional.of(filterTree));
        String blobId = "blobId";
        FileDto fileDto = when(mock(FileDto.class).getBlobId()).thenReturn(blobId).getMock();
        when(fileService.getFileById(fileId)).thenReturn(fileDto);

        filterTreeService.deleteFilterTree(filterTreeId);
        assertAll(
                () -> verify(filterTreeRepository).delete(filterTree),
                () -> verify(fileService).deleteFileBytes(blobId),
                () -> verify(fileService).deleteFileById(fileId));
    }

    @Test
    void deleteFilterTreeFromOtherUserAsUser() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);

        Integer filterTreeId = 2;
        Integer otherUserId = 3;
        FilterTree filterTree =
                when(mock(FilterTree.class).getCreatedBy())
                        .thenReturn(AggregateReference.to(otherUserId))
                        .getMock();

        when(filterTreeRepository.findByIdAndCreatedByOrIdAndIsPublic(
                filterTreeId, userId, filterTreeId, true))
                .thenReturn(Optional.of(filterTree));
        assertThrows(
                AccessDeniedException.class, () -> filterTreeService.deleteFilterTree(filterTreeId));
    }

    @Test
    void deleteFilterTreeFromOtherUserAsAdmin() {
        Integer userId = 1;
        when(authenticationFacade.getUserId()).thenReturn(userId);
        when(authenticationFacade.isAdmin()).thenReturn(true);

        Integer filterTreeId = 2;
        Integer otherUserId = 3;
        Integer fileId = 3;
        FilterTree filterTree = mock(FilterTree.class);
        when(filterTree.getCreatedBy()).thenReturn(AggregateReference.to(otherUserId)).getMock();
        when(filterTree.getFile()).thenReturn(AggregateReference.to(fileId));

        when(filterTreeRepository.findById(filterTreeId)).thenReturn(Optional.of(filterTree));
        String blobId = "blobId";
        FileDto fileDto = when(mock(FileDto.class).getBlobId()).thenReturn(blobId).getMock();
        when(fileService.getFileById(fileId)).thenReturn(fileDto);

        filterTreeService.deleteFilterTree(filterTreeId);
        assertAll(
                () -> verify(filterTreeRepository).delete(filterTree),
                () -> verify(fileService).deleteFileBytes(blobId),
                () -> verify(fileService).deleteFileById(fileId));
    }
}
