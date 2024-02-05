package org.molgenis.vipweb.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.constants.FilterTreeType;
import org.molgenis.vipweb.model.dto.FilterTreeDto;
import org.molgenis.vipweb.service.FilterTreeService;
import org.springframework.data.domain.Page;

@ExtendWith(MockitoExtension.class)
class FilterTreeControllerTest {
  @Mock private FilterTreeService filterTreeService;
  private FilterTreeController filterTreeController;

  @BeforeEach
  void setUp() {
    filterTreeController = new FilterTreeController(filterTreeService);
  }

  @Test
  void getFilterTrees() {
    FilterTreeType type = FilterTreeType.VARIANT;
    @SuppressWarnings("unchecked")
    Page<FilterTreeDto> filterTreeDtos = mock(Page.class);
    when(filterTreeService.getFilterTrees(eq(type), any())).thenReturn(filterTreeDtos);
    assertEquals(filterTreeDtos, filterTreeController.getFilterTrees(type));
  }

  @Test
  void getDefaultFilterTree() {
    FilterTreeType type = FilterTreeType.SAMPLE;
    FilterTreeDto filterTreeDto = mock(FilterTreeDto.class);
    when(filterTreeService.getDefaultFilterTree(type)).thenReturn(filterTreeDto);
    assertEquals(filterTreeDto, filterTreeController.getDefaultFilterTree(type));
  }
}
