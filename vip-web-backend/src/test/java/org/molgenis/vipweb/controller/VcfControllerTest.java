package org.molgenis.vipweb.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.model.dto.FileCreateDto;
import org.molgenis.vipweb.model.dto.VcfCreateDto;
import org.molgenis.vipweb.model.dto.VcfDto;
import org.molgenis.vipweb.service.VcfService;
import org.springframework.data.domain.Page;

@ExtendWith(MockitoExtension.class)
class VcfControllerTest {
    @Mock
    private VcfService vcfService;
    private VcfController vcfController;

    @BeforeEach
    void setUp() {
        vcfController = new VcfController(vcfService);
    }

    @Test
    void uploadVcf() throws IOException {
        ServletInputStream inputStream = mock(ServletInputStream.class);
        HttpServletRequest request =
                when(mock(HttpServletRequest.class).getInputStream()).thenReturn(inputStream).getMock();
        String filename = "filename";
        boolean isPublic = true;
        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfService.upload(any(FileCreateDto.class))).thenReturn(vcfDto);
        assertEquals(vcfDto, vcfController.uploadVcf(request, filename, isPublic));
    }

    @Test
    void createVcf() {
        VcfCreateDto vcfCreateDto = VcfCreateDto.builder().variants("variants").isPublic(true).build();
        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfService.create(vcfCreateDto)).thenReturn(vcfDto);
        assertEquals(vcfDto, vcfController.createVcf(vcfCreateDto));
    }

    @Test
    void getVcfById() {
        Integer id = 1;
        VcfDto vcfDto = mock(VcfDto.class);
        when(vcfService.getVcfById(id)).thenReturn(vcfDto);
        assertEquals(vcfDto, vcfController.getVcfById(id));
    }

    @Test
    void deleteVcf() {
        Integer id = 1;
        vcfController.deleteVcf(id);
        verify(vcfService).deleteVcf(id);
    }

    @Test
    void getVcfs() {
        @SuppressWarnings("unchecked")
        Page<VcfDto> vcfDtos = mock(Page.class);
        when(vcfService.getVcfs((any()))).thenReturn(vcfDtos);
        assertEquals(vcfDtos, vcfController.getVcfs());
    }
}
