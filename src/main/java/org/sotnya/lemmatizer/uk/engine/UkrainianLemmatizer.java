package org.sotnya.lemmatizer.uk.engine;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class to serve purposes of term-to-lemma substitution.
 * Handles mapping retrieval, terms normalisation and lookup for proper lemmas in mapping.
 */
public class UkrainianLemmatizer {
    private static final ESLogger logger = ESLoggerFactory.getLogger(UkrainianLemmatizer.class.getSimpleName());
    private static final Map<String, String> dictionary;
    /**
     * Before the lookup we replace some symbols to their alternatives are being used in mapping.
     * Right now there is the single substitution case: ukrainian apostrophes with to english single quote.
     */
    private static final Map<Character, Character> replaceItems = new HashMap<Character, Character>(2) {{
        put('’', '\'');
        put('ʼ', '\'');
    }};

    static {
        // load mapping from file (must be changed to faster and memory-efficient type)
        final String fileName = "mapping_sorted.csv";
        final String separator = ",";
        final ClassLoader loader = UkrainianLemmatizer.class.getClassLoader();

        logger.debug("Started loading dictionary");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(fileName), "UTF8"))) {
            dictionary = reader.lines()
                    .map(line -> line.split(separator))
                    .collect(Collectors.toMap(p -> p[0], p -> p[1]));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        logger.debug(String.format("Finished loading dictionary. %d entities delivered gently.", dictionary.size()));
        // Let's wait some additional second but we'll keep our heap clean from
        // lots of short-lived objects created during the loading.
        System.gc();
    }

    /**
     * @param termAtt Token (word) we should compare with existing in our dictionary.
     *
     * @return Optional value which is defined in case if we have related lemma in dict.
     */
    public Optional<CharSequence> lemmatize(CharTermAttribute termAtt) {
        String term = termAtt.toString();

        for (Map.Entry<Character, Character> e : replaceItems.entrySet()) {
            term = term.replace(e.getKey(), e.getValue());
        }

        return dictionary.containsKey(term) ? Optional.of(dictionary.get(term)) : Optional.empty();
    }
}