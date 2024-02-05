package org.molgenis.vipweb;

import java.util.concurrent.CompletableFuture;
import jakarta.servlet.http.HttpServletRequest;
import org.molgenis.vipweb.model.File;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface FilesService {

    /** Asynchronous file upload from HTTP request */
    @Async
    CompletableFuture<File> upload(HttpServletRequest httpServletRequest);

    /**
     * Asynchronous file download to HTTP response
     */
    ResponseEntity<StreamingResponseBody> download(String fileId);

    /**
     * Get file metadata
     */
    File getFile(String fileId);

    /**
     * Delete file and file metadata
     */
    void delete(String fileId);
}
