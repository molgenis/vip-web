package org.molgenis.vipweb.model.mapper;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.File;
import org.molgenis.vipweb.model.dto.FileDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileMapper {
    public FileDto fileToFileDto(File file) {
        return FileDto.builder()
                .id(file.getId())
                .blobId(file.getBlobId())
                .filename(file.getFilename())
                .size(file.getSize())
                .build();
    }
}
