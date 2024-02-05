package org.molgenis.vipweb.service;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.Blob;
import org.molgenis.vipweb.FilterTreeParser;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.FilterTree;
import org.molgenis.vipweb.model.FilterTreeClass;
import org.molgenis.vipweb.model.constants.FilterTreeType;
import org.molgenis.vipweb.model.dto.FileCreateDto;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.FilterTreeCreateDto;
import org.molgenis.vipweb.model.dto.FilterTreeDto;
import org.molgenis.vipweb.model.mapper.FilterTreeMapper;
import org.molgenis.vipweb.repository.FilterTreeRepository;
import org.molgenis.vipweb.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilterTreeService {
    private final FilterTreeRepository filterTreeRepository;
    private final FilterTreeMapper filterTreeMapper;
    private final FilterTreeParser filterTreeParser;
    private final FileService fileService;
    private final AuthenticationFacade authenticationFacade;

    @Transactional(readOnly = true)
    public FilterTreeDto getFilterTreeById(Integer id) {
        return getFilterTreeDto(id);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_VIPBOT')")
    public void upload(FilterTreeCreateDto filterTreeCreateDto) {
        // parse tree
        FilterTreeParser.FilterTree parsedFilterTree;
        try (ReadableByteChannel readableByteChannel =
                     filterTreeCreateDto.getFileCreateDto().getReadableByteChannel()) {
            parsedFilterTree = filterTreeParser.parse(readableByteChannel);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // persist inner tree file
        FileCreateDto fileCreateDto =
                filterTreeCreateDto.getFileCreateDto().toBuilder()
                        .readableByteChannel(
                                Channels.newChannel(new ByteArrayInputStream(parsedFilterTree.getInnerTree())))
                        .build();

        Blob blob = fileService.createFileBytes(fileCreateDto);
        FileDto fileDto;
        try {
            fileDto = fileService.createFile(fileCreateDto, blob);
        } catch (RuntimeException e) {
            fileService.deleteFileBytes(blob.getId());
            throw e;
        }

        // persist tree entity
        try {
            FilterTree filterTree =
                    FilterTree.builder()
                            .name(fileDto.getFilename())
                            .description(parsedFilterTree.getDescription())
                            .file(AggregateReference.to(fileDto.getId()))
                            .type(filterTreeCreateDto.getType())
                            .classes(
                                    parsedFilterTree.getClasses().stream()
                                            .map(
                                                    parsedFilterClass ->
                                                            FilterTreeClass.builder()
                                                                    .name(parsedFilterClass.getName())
                                                                    .description(parsedFilterClass.getDescription())
                                                                    .isDefaultFilter(
                                                                            parsedFilterTree
                                                                                    .getDefaultFilterClasses()
                                                                                    .contains(parsedFilterClass.getName()))
                                                                    .build())
                                            .toList())
                            .isPublic(
                                    filterTreeCreateDto.getIsPublic() != null && filterTreeCreateDto.getIsPublic())
                            .build();
            filterTreeRepository.save(filterTree);
        } catch (RuntimeException e) {
            fileService.deleteFileBytes(blob.getId());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public FilterTreeDto getDefaultFilterTree(FilterTreeType filterTreeType) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id"));

        Page<FilterTree> filterTrees =
                (authenticationFacade.isAdmin() || authenticationFacade.isVipbot())
                        ? filterTreeRepository.findAllByType(filterTreeType, pageable)
                        : filterTreeRepository.findAllByTypeAndCreatedByOrTypeAndIsPublic(
                        filterTreeType, authenticationFacade.getUserId(), filterTreeType, true, pageable);

        FilterTree filterTree =
                filterTrees.stream().findFirst().orElseThrow(IllegalArgumentException::new);
        return filterTreeMapper.filterTreeToFilterTreeDto(filterTree);
    }

    @Transactional(readOnly = true)
    public Page<FilterTreeDto> getFilterTrees(FilterTreeType filterTreeType, Pageable pageable) {
        Page<FilterTree> filterTrees =
                (authenticationFacade.isAdmin() || authenticationFacade.isVipbot())
                        ? filterTreeRepository.findAllByType(filterTreeType, pageable)
                        : authenticationFacade.isAnonymousUser()
                        ? filterTreeRepository.findAllByTypeAndIsPublic(filterTreeType, true, pageable)
                        : filterTreeRepository.findAllByTypeAndCreatedByOrTypeAndIsPublic(
                        filterTreeType,
                        authenticationFacade.getUserId(),
                        filterTreeType,
                        true,
                        pageable);

        return filterTrees.map(filterTreeMapper::filterTreeToFilterTreeDto);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public void deleteFilterTree(Integer id) {
        FilterTree filterTree = getFilterTree(id);
        Integer createdById = Objects.requireNonNull(filterTree.getCreatedBy().getId());
        if (!createdById.equals(authenticationFacade.getUserId()) && !authenticationFacade.isAdmin()) {
            throw new AccessDeniedException("Access Denied");
        }

        Integer fileId = Objects.requireNonNull(filterTree.getFile().getId());
        FileDto fileDto = fileService.getFileById(fileId);

        fileService.deleteFileBytes(fileDto.getBlobId());
        filterTreeRepository.delete(filterTree);
        fileService.deleteFileById(fileId);
    }

    private FilterTreeDto getFilterTreeDto(Integer id) {
        FilterTree filterTree = getFilterTree(id);
        return filterTreeMapper.filterTreeToFilterTreeDto(filterTree);
    }

    private FilterTree getFilterTree(Integer id) {
        Optional<FilterTree> filterTreeOptional;
        if (authenticationFacade.isAdmin() || authenticationFacade.isVipbot()) {
            filterTreeOptional = filterTreeRepository.findById(id);
        } else if (authenticationFacade.isAnonymousUser()) {
            filterTreeOptional = filterTreeRepository.findByIdAndIsPublic(id, true);
        } else {
            filterTreeOptional =
                    filterTreeRepository.findByIdAndCreatedByOrIdAndIsPublic(
                            id, authenticationFacade.getUserId(), id, true);
        }
        return filterTreeOptional.orElseThrow(UnknownEntityException::new);
    }
}
