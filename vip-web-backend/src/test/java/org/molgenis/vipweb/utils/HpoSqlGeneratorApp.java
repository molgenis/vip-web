package org.molgenis.vipweb.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.molgenis.vipweb.model.HpoTerm;

/**
 * Create data.sql content for HPO_TERM table using <a
 * href="https://hpo.jax.org/app/data/annotations">phenotypes to genes resource</a>.
 */
public class HpoSqlGeneratorApp {
    public static void main(String[] args) throws IOException {
        Path inputPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);

        try (Stream<String> lines = Files.lines(inputPath, UTF_8);
             BufferedWriter bufferedWriter = Files.newBufferedWriter(outputPath, UTF_8)) {

            Set<HpoTerm> hpoTerms =
                    lines.skip(1).map(HpoSqlGeneratorApp::parseHpoTerm).collect(Collectors.toSet());

            hpoTerms.stream()
                    .sorted(Comparator.comparing(HpoTerm::getTerm))
                    .forEach(
                            hpoTerm -> {
                                try {
                                    String sql = toSql(hpoTerm);
                                    bufferedWriter.write(sql);
                                } catch (IOException e) {
                                    throw new UncheckedIOException(e);
                                }
                            });
            bufferedWriter.write(";\n");
        }
    }

    private static HpoTerm parseHpoTerm(String line) {
        String[] tokens = line.split("\t", -1);
        return HpoTerm.builder().term(tokens[0]).name(tokens[1]).build();
    }

    private static String toSql(HpoTerm hpoTerm) {
        return "INSERT INTO HPO_TERM (TERM, NAME)\nVALUES ('"
                + escape(hpoTerm.getTerm())
                + "', '"
                + escape(hpoTerm.getName())
                + "');\n";
    }

    private static String escape(String str) {
        return str.replace("'", "''");
    }
}
