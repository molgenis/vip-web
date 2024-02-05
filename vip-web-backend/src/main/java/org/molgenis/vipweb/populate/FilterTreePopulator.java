package org.molgenis.vipweb.populate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.model.constants.FilterTreeType;
import org.molgenis.vipweb.model.dto.FileCreateDto;
import org.molgenis.vipweb.model.dto.FilterTreeCreateDto;
import org.molgenis.vipweb.service.FilterTreeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilterTreePopulator {
    private final FilterTreeService filterTreeService;

    @Transactional
    public void populate(Path filterTreesJson) {
        if (Files.notExists(filterTreesJson)) {
            throw new IllegalArgumentException("'%s' does not exist".formatted(filterTreesJson));
        }

        try {
            FilterTrees filterTrees =
                    new ObjectMapper().readValue(filterTreesJson.toFile(), FilterTrees.class);
            if (filterTrees.variants.isEmpty()) {
                throw new IllegalArgumentException(
                        "'%s' must contain at least one variant tree".formatted(filterTreesJson));
            }
            if (filterTrees.samples.isEmpty()) {
                throw new IllegalArgumentException(
                        "'%s' must contain at least one sample tree".formatted(filterTreesJson));
            }

            filterTrees
                    .variants()
                    .forEach(treePath -> populate(FilterTreeType.VARIANT, Path.of(treePath)));
            filterTrees.samples().forEach(treePath -> populate(FilterTreeType.SAMPLE, Path.of(treePath)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void populate(FilterTreeType type, Path filterTreeJson) {
        if (Files.notExists(filterTreeJson)) {
            throw new IllegalArgumentException("'%s' does not exist".formatted(filterTreeJson));
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filterTreeJson.toFile(), "r")) {
            FilterTreeCreateDto filterTreeCreateDto =
                    FilterTreeCreateDto.builder()
                            .type(type)
                            .fileCreateDto(
                                    FileCreateDto.builder()
                                            .readableByteChannel(randomAccessFile.getChannel())
                                            .filename(filterTreeJson.getFileName().toString())
                                            .build())
                            .isPublic(true)
                            .build();
            filterTreeService.upload(filterTreeCreateDto);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private record FilterTrees(List<String> variants, List<String> samples) {
    }
}
