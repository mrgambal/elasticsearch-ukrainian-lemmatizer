package org.sotnya.lemmatizer.uk.engine;

import morfologik.stemming.Dictionary;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.morfologik.MorfologikFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianAnalyzer;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Class to serve purposes of term-to-lemma substitution.
 * Handles mapping retrieval, terms normalisation and lookup for proper lemmas in mapping.
 */
public class UkrainianLemmatizerResources {
    /**
     * File containing default Ukrainian stopwords.
     */
    private static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private static final String DICT_FILE_PATH = "ua/net/nlp/ukrainian.dict";

    private static final ESLogger LOGGER = ESLoggerFactory.getLogger(UkrainianLemmatizerResources.class.getSimpleName());

    /**
     * Returns an unmodifiable instance of the default stop words set.
     *
     * @return default stop words set.
     */
    public static CharArraySet getDefaultStopSet() {
        return UkrainianLemmatizerResources.DefaultSetHolder.DEFAULT_STOP_SET;
    }

    /**
     * Builds new filter using the dictionary built in morfologik-ukrainian-search package and appends it to the
     * stream transformation pipeline.
     *
     * @param input Stream to be altered.
     *
     * @return Base stream with added ukrainian Morfoligik filter.
     */
    public static TokenStream getUkrainianLemmatizerTokenFilter(TokenStream input) {
        return new MorfologikFilter(input, getDictionary());
    }

    /**
     * Atomically loads the DEFAULT_STOP_SET in a lazy fashion once the outer class
     * accesses the static final set the first time.;
     */
    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        static {
            try {
                final Reader decodingReader = IOUtils.getDecodingReader(
                        UkrainianLemmatizerResources.class.getClassLoader().getResourceAsStream(DEFAULT_STOPWORD_FILE),
                        StandardCharsets.UTF_8);

                DEFAULT_STOP_SET = WordlistLoader.getSnowballWordSet(decodingReader);


                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("The stop-set has been loaded.");
            } catch (IOException | NullPointerException ex) {
                // default set should always be present as it is part of the
                // distribution (JAR)
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }

    /**
     * Reads a .dict file produced by morfologik-ukrainian-search and constructs a new dictionary from it.
     * In case of file's absence must fail with {@link IOException}.
     *
     * @return New instance of morfologik {@link Dictionary} to be used in token filters.
     */
    private static Dictionary getDictionary() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Started loading the ukrainian dictionary.");

        try {
            Dictionary dict = Dictionary.read(UkrainianAnalyzer.class.getClassLoader().getResource(DICT_FILE_PATH));

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("The ukrainian dictionary has been loaded successfully.");

            return dict;
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }
}
