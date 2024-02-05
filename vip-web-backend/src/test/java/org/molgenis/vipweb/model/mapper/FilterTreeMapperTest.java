package org.molgenis.vipweb.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.File;
import org.molgenis.vipweb.model.FilterTree;
import org.molgenis.vipweb.model.FilterTreeClass;
import org.molgenis.vipweb.model.User;
import org.molgenis.vipweb.model.constants.FilterTreeType;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.dto.FilterTreeClassDto;
import org.molgenis.vipweb.model.dto.FilterTreeDto;
import org.molgenis.vipweb.service.FileService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

@ExtendWith(MockitoExtension.class)
class FilterTreeMapperTest {
    @Mock
    private FileService fileService;
    private FilterTreeMapper filterTreeMapper;

    @BeforeEach
    void setUp() {
        filterTreeMapper = new FilterTreeMapper(fileService);
    }

    @Test
    void filterTreeToFilterTreeDto() {
        int fileId = 2;
        FileDto fileDto = mock(FileDto.class);
        when(fileService.getFileById(fileId)).thenReturn(fileDto);

        int filterClassId = 6;
        String filterClassName = "class";
        String filterClassDescription = "class description";
        boolean filterClassIsDefaultFilter = true;

        int id = 1;
        String name = "filter_tree";
        String description = "filter_tree description";
        AggregateReference<File, Integer> fileRef = AggregateReference.to(fileId);
        FilterTreeType type = FilterTreeType.VARIANT;
        FilterTreeClass filterTreeClass =
                FilterTreeClass.builder()
                        .id(filterClassId)
                        .name(filterClassName)
                        .description(filterClassDescription)
                        .isDefaultFilter(filterClassIsDefaultFilter)
                        .build();
        AggregateReference<User, Integer> createdByRef = AggregateReference.to(4);
        Instant creationDate = Instant.ofEpochSecond(1);
        AggregateReference<User, Integer> lastModifiedByRef = AggregateReference.to(5);
        Instant lastModifiedDate = Instant.ofEpochSecond(2);
        FilterTree filterTree =
                FilterTree.builder()
                        .id(id)
                        .name(name)
                        .description(description)
                        .file(fileRef)
                        .type(type)
                        .classes(List.of(filterTreeClass))
                        .isPublic(true)
                        .createdBy(createdByRef)
                        .creationDate(creationDate)
                        .lastModifiedBy(lastModifiedByRef)
                        .lastModifiedDate(lastModifiedDate)
                        .build();

        FilterTreeClassDto filterTreeClassDto =
                FilterTreeClassDto.builder()
                        .id(filterClassId)
                        .name(filterClassName)
                        .description(filterClassDescription)
                        .isDefaultFilter(filterClassIsDefaultFilter)
                        .build();

        FilterTreeDto filterTreeDto =
                FilterTreeDto.builder()
                        .id(id)
                        .name(name)
                        .description(description)
                        .classes(List.of(filterTreeClassDto))
                        .file(fileDto)
                        .build();

        assertEquals(filterTreeDto, filterTreeMapper.filterTreeToFilterTreeDto(filterTree));
    }
}
