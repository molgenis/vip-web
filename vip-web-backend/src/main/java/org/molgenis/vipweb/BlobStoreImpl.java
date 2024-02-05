package org.molgenis.vipweb;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Component
public class BlobStoreImpl implements BlobStore {

    private final IdGenerator idGenerator;

    BlobStoreImpl(IdGenerator idGenerator) {
        this.idGenerator = requireNonNull(idGenerator);
    }
    @Override
    public Blob store(ReadableByteChannel fromChannel) {
        String id = idGenerator.generateUniqueId();

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

    @Override
    public void delete(String blobId) {
        try {
            Files.delete(getPath(blobId));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public ReadableByteChannel newChannel(String blobId) {
        try {
            return Channels.newChannel(Files.newInputStream(getPath(blobId)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Path getPath(String blobId) {
        return Paths.get(System.getProperty("user.home"), blobId);
    }
}
