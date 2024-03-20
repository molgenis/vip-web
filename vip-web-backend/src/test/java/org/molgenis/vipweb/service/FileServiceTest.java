package org.molgenis.vipweb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipweb.Blob;
import org.molgenis.vipweb.BlobStore;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.File;
import org.molgenis.vipweb.model.dto.FileCreateDto;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.mapper.FileMapper;
import org.molgenis.vipweb.repository.FileRepository;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @Mock
    private FileRepository fileRepository;
    @Mock
    private FileMapper fileMapper;
    @Mock
    private BlobStore blobStore;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService(fileRepository, fileMapper, blobStore);
    }

    @Test
    void createFileBytes() {
        ReadableByteChannel readableByteChannel = mock(ReadableByteChannel.class);
        FileCreateDto fileCreateDto =
                FileCreateDto.builder()
                        .readableByteChannel(readableByteChannel)
                        .filename("filename")
                        .build();

        Blob blob = mock(Blob.class);
        when(blobStore.store(readableByteChannel)).thenReturn(blob);

        assertEquals(blob, fileService.createFileBytes(fileCreateDto));
    }

    @Test
    void createFile() {
        ReadableByteChannel readableByteChannel = mock(ReadableByteChannel.class);
        String filename = "filename";
        FileCreateDto fileCreateDto =
                FileCreateDto.builder().readableByteChannel(readableByteChannel).filename(filename).build();

        String blobId = "blob1";
        long size = 123L;
        Blob blob = Blob.builder().id(blobId).size(size).build();

        File file = File.builder().blobId(blobId).filename(filename).size(size).build();

        File persistedFile = mock(File.class);
        when(fileRepository.save(file)).thenReturn(persistedFile);
        FileDto fileDto = mock(FileDto.class);
        when(fileMapper.fileToFileDto(persistedFile)).thenReturn(fileDto);

        assertEquals(fileDto, fileService.createFile(fileCreateDto, blob));
    }

    @Test
    void getFileById() {
        Integer id = 1;
        File file = mock(File.class);
        when(fileRepository.findById(id)).thenReturn(Optional.of(file));
        FileDto fileDto = mock(FileDto.class);
        when(fileMapper.fileToFileDto(file)).thenReturn(fileDto);
        assertEquals(fileDto, fileService.getFileById(1));
    }

    @Test
    void getFileByIdUnknownEntityException() {
        when(fileRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(UnknownEntityException.class, () -> fileService.getFileById(1));
    }

    @Test
    void deleteFileBytes() {
        String blobId = "blob1";
        fileService.deleteFileBytes(blobId);
        verify(blobStore).delete(blobId);
    }

    @Test
    void deleteFileById() {
        Integer id = 1;
        fileService.deleteFileById(id);
        verify(fileRepository).deleteById(id);
    }

    @Test
    void newChannel() {
        String blobId = "blob1";
        Blob blob = Blob.builder().id(blobId).size(123L).build();
        ReadableByteChannel readableByteChannel = mock(ReadableByteChannel.class);
        when(blobStore.newChannel(blobId)).thenReturn(readableByteChannel);
        assertEquals(readableByteChannel, fileService.newChannel(blob));
    }
}
