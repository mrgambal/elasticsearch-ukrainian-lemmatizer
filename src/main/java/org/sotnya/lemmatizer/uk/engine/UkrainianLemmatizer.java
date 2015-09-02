package org.sotnya.lemmatizer.uk.engine;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UkrainianLemmatizer {
    private static final Map<String, String> dictionary;

    static {
        final InputStream is = UkrainianLemmatizer.class.getClassLoader().getResourceAsStream("mapping_sorted.csv");
        final String separator = ",";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            dictionary = reader.lines()
                    .map(line -> line.split(separator))
                    .collect(Collectors.toMap(p -> p[0], p -> p[1]));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<CharSequence> lemmatize(CharTermAttribute termAtt) {
        // replace ukrainian apostrophe to english single quote is being used in mapping
        final String term = termAtt.toString().replace('’', '\'');

        if (dictionary.containsKey(term)) {
            return Optional.of(dictionary.get(term));
        } else
            return Optional.empty();
    }
}
