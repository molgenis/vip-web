package org.molgenis.vipweb;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class BlobStore {

    private final String storePath;

    BlobStore(VipWebProperties vipWebProperties) {
        String storePath = vipWebProperties.fsPath();
        if (storePath == null) {
            throw new IllegalArgumentException("missing required value for property 'vipweb.fs.path'");
        }
        this.storePath = storePath;
    }

    public Blob store(ReadableByteChannel fromChannel) {
        String id = UUID.randomUUID().toString();

        long size;
        InputStream inputStream = Channels.newInputStream(fromChannel);
        Path path = getPath(id);
        try {
            size = Files.copy(inputStream, path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Blob.builder().id(id).size(size).build();
    }

    public void delete(String blobId) {
        try {
            Files.delete(getPath(blobId));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ReadableByteChannel newChannel(String blobId) {
        try {
            return Channels.newChannel(Files.newInputStream(getPath(blobId)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Path getPath(String blobId) {
        Path path = Paths.get(storePath);
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return Path.of(path.toString(), blobId);
    }
}
