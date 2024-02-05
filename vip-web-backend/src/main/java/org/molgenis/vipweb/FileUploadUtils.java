package org.molgenis.vipweb;

import jakarta.servlet.http.HttpServletRequest;
import org.molgenis.vipweb.model.dto.FileCreateDto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;

public class FileUploadUtils {
    private FileUploadUtils() {
    }

    public static FileCreateDto fromRequest(HttpServletRequest httpServletRequest, String filename) {
        return fromRequest(httpServletRequest, filename, null);
    }

    public static FileCreateDto fromRequest(
            HttpServletRequest httpServletRequest, String filename, Boolean isPublic) {
        try {
            return FileCreateDto.builder()
                    .readableByteChannel(Channels.newChannel(httpServletRequest.getInputStream()))
                    .filename(filename)
                    .isPublic(isPublic)
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
