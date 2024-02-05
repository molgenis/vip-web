package org.molgenis.vipweb;

import jakarta.servlet.http.HttpServletRequest;
import org.molgenis.vipweb.model.File;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.CompletableFuture;

import static java.nio.channels.Channels.newChannel;
import static java.util.Objects.requireNonNull;

@Component
public class FilesServiceImpl implements FilesService {
    private final BlobStore blobStore;

    FilesServiceImpl(BlobStore blobStore) {
        this.blobStore = requireNonNull(blobStore);
    }

    @Override
    public CompletableFuture<File> upload(HttpServletRequest httpServletRequest) {
        Blob blob;
        try (ReadableByteChannel fromChannel = newChannel(httpServletRequest.getInputStream())) {
            blob = blobStore.store(fromChannel);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        String uriString =
                ServletUriComponentsBuilder.fromRequestUri(httpServletRequest)
                        .scheme(null)
                        .host(null)
                        .port(null)
                        .userInfo(null)
                        .pathSegment(blob.getId())
                        .queryParam("alt", "media")
                        .build()
                        .toUriString();

        String filename = httpServletRequest.getHeader("x-vip-filename");
        String contentType = httpServletRequest.getContentType();
        File file = File.builder().id("foobar").filename(filename).size(blob.getSize()).contentType(contentType).url(uriString).build();
        // FIXME
        // dataService.add(FILE_META, fileMeta);
        return CompletableFuture.completedFuture(file);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> download(String fileId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFile(String fileId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String fileId) {
        throw new UnsupportedOperationException();
    }
}
