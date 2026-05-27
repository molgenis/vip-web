package org.molgenis.vipweb;

import lombok.Builder;
import lombok.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Map;

@Component
public class FilterTreeParser {

    private static FilterTreeRaw readRaw(ReadableByteChannel readableByteChannel) {
        return new ObjectMapper()
                .readValue(Channels.newInputStream(readableByteChannel), FilterTreeRaw.class);
    }

    @SuppressWarnings("unchecked")
    private static FilterTree parse(FilterTreeRaw filterTreeRaw) {
        Map<String, Object> nodes = (Map<String, Object>) filterTreeRaw.tree().get("nodes");
        List<FilterTreeClass> filterTreeClasses =
                nodes.values().stream()
                        .filter(node -> ((Map<String, Object>) node).get("type").equals("LEAF"))
                        .map(
                                node ->
                                        FilterTreeClass.builder()
                                                .name((String) ((Map<String, Object>) node).get("class"))
                                                .description((String) ((Map<String, Object>) node).get("description"))
                                                .build())
                        .toList();

        byte[] treeBytes =
                new ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsBytes(filterTreeRaw.tree());

        return FilterTree.builder()
                .name(filterTreeRaw.name())
                .defaultFilterClasses(filterTreeRaw.defaultFilterClasses())
                .classes(filterTreeClasses)
                .innerTree(treeBytes)
                .build();
    }

    public FilterTree parse(ReadableByteChannel readableByteChannel) {
        FilterTreeRaw filterTreeRaw = readRaw(readableByteChannel);
        return parse(filterTreeRaw);
    }

    @Value
    @Builder(toBuilder = true)
    public static class FilterTree {
        String name;
        String description;
        List<String> defaultFilterClasses;
        List<FilterTreeClass> classes;
        byte[] innerTree;
    }

    @Value
    @Builder(toBuilder = true)
    public static class FilterTreeClass {
        String name;
        String description;
    }

    @Builder(toBuilder = true)
    private record FilterTreeRaw(
            String name,
            String description,
            List<String> defaultFilterClasses,
            Map<String, Object> tree) {
    }
}
