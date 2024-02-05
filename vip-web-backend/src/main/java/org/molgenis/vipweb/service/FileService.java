package org.molgenis.vipweb.service;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.Blob;
import org.molgenis.vipweb.BlobStore;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.File;
import org.molgenis.vipweb.model.dto.FileCreateDto;
import org.molgenis.vipweb.model.dto.FileDto;
import org.molgenis.vipweb.model.mapper.FileMapper;
import org.molgenis.vipweb.repository.FileRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.channels.ReadableByteChannel;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    private final BlobStore blobStore;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_VIPBOT')")
    public Blob createFileBytes(FileCreateDto fileCreateDto) {
        return blobStore.store(fileCreateDto.getReadableByteChannel());
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_VIPBOT')")
    public FileDto createFile(FileCreateDto fileCreateDto, Blob blob) {
        File file =
                File.builder()
                        .blobId(blob.getId())
                        .filename(fileCreateDto.getFilename())
                        .size(blob.getSize())
                        .build();

        file = fileRepository.save(file);
        return fileMapper.fileToFileDto(file);
    }

    @Transactional(readOnly = true)
    public FileDto getFileById(Integer id) {
        File file = fileRepository.findById(id).orElseThrow(UnknownEntityException::new);
        return fileMapper.fileToFileDto(file);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public void deleteFileBytes(String blobId) {
        blobStore.delete(blobId);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public void deleteFileById(Integer fileId) {
        fileRepository.deleteById(fileId);
    }

    public ReadableByteChannel newChannel(Blob blob) {
        return blobStore.newChannel(blob.getId());
    }
}
