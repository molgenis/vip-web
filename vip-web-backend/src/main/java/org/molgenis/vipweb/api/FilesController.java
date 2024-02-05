package org.molgenis.vipweb.api;

import jakarta.servlet.http.HttpServletRequest;
import org.molgenis.vipweb.FilesService;
import org.molgenis.vipweb.model.File;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/files")
public class FilesController {
    private final FilesService filesService;

    FilesController(FilesService filesService) {
        this.filesService = requireNonNull(filesService);
    }
    @PostMapping
    @ResponseStatus(CREATED)
    public CompletableFuture<ResponseEntity<File>> createFile(
            HttpServletRequest httpServletRequest) {
        return filesService
                .upload(httpServletRequest)
                .thenApply(file -> {URI uri =
                        ServletUriComponentsBuilder.fromRequestUri(httpServletRequest)
                                .pathSegment(file.getId())
                                .queryParam("alt", "media")
                                .build()
                                .toUri();

                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(uri);

                    return new ResponseEntity<>(file, headers, HttpStatus.CREATED);});
    }

    @GetMapping
    @ResponseStatus(OK)
    public String get() {
        return "success";
    }
}
