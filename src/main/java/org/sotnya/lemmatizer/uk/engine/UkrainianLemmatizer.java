package org.sotnya.lemmatizer.uk.engine;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class to serve purposes of term-to-lemma substitution.
 * Handles mapping retrieval, terms normalisation and lookup for proper lemmas in mapping.
 */
public class UkrainianLemmatizer {
    private static final Map<String, String> dictionary;

    static {
        // load mapping from file (must be changed to faster and memory-efficient type)
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

    /**
     * @param termAtt Token (word) we should compare with existing in our dictionary
     *                Before that we replace ukrainian apostrophes with to english
     *                single quote is being used in mapping.
     * @return Optional value which is defined in case if we have related lemma in dict.
     */
    public Optional<CharSequence> lemmatize(CharTermAttribute termAtt) {
        final String term = termAtt.toString().replace('’', '\'').replace('ʼ', '\'');

        return dictionary.containsKey(term) ? Optional.of(dictionary.get(term)) : Optional.empty();
    }
}
