package org.molgenis.vipweb.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.dto.HpoTermDto;
import org.molgenis.vipweb.service.HpoService;
import org.springframework.data.domain.Page;

@ExtendWith(MockitoExtension.class)
class HpoControllerTest {
  @Mock private HpoService hpoService;
  private HpoController hpoController;

  @BeforeEach
  void setUp() {
    hpoController = new HpoController(hpoService);
  }

  @Test
  void getHpoTerms() {
    String q = "term";
    @SuppressWarnings("unchecked")
    Page<HpoTermDto> hpoTermDto = mock(Page.class);
    when(hpoService.getHpoTerms(q)).thenReturn(hpoTermDto);
    assertEquals(hpoTermDto, hpoController.getHpoTerms(q));
  }
}
