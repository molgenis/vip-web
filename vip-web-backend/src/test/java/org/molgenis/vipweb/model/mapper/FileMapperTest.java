package org.molgenis.vipweb.model.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipweb.model.File;
import org.molgenis.vipweb.model.dto.FileDto;

class FileMapperTest {
  private FileMapper fileMapper;

  @BeforeEach
  void setUp() {
    fileMapper = new FileMapper();
  }

  @Test
  void fileToFileDto() {
    int id = 1;
    String blobId = "blob1";
    String filename = "file.txt";
    long size = 123L;
    File file = File.builder().id(id).blobId(blobId).filename(filename).size(size).build();
    FileDto fileDto = FileDto.builder().id(id).blobId(blobId).filename(filename).size(size).build();
    assertEquals(fileDto, fileMapper.fileToFileDto(file));
  }
}
