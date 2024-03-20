package org.molgenis.vipweb.model.mapper;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.FilterTree;
import org.molgenis.vipweb.model.FilterTreeClass;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.FilterTreeClassDto;
import org.molgenis.vipweb.model.dto.FilterTreeDto;
import org.molgenis.vipweb.service.FileService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilterTreeMapper {
    private final FileService fileService;

    private static FilterTreeClassDto filterTreeClassToFilterTreeClassDto(
            FilterTreeClass filterTreeClass) {
        return FilterTreeClassDto.builder()
                .id(filterTreeClass.getId())
                .name(filterTreeClass.getName())
                .description(filterTreeClass.getDescription())
                .isDefaultFilter(filterTreeClass.isDefaultFilter())
                .build();
    }

    public FilterTreeDto filterTreeToFilterTreeDto(FilterTree filterTree) {
        Integer fileId = Objects.requireNonNull(filterTree.getFile().getId());
        FileDto fileDto = fileService.getFileById(fileId);

        return FilterTreeDto.builder()
                .id(filterTree.getId())
                .name(filterTree.getName())
                .description(filterTree.getDescription())
                .classes(
                        filterTree.getClasses().stream()
                                .map(FilterTreeMapper::filterTreeClassToFilterTreeClassDto)
                                .toList())
                .file(fileDto)
                .build();
    }
}
