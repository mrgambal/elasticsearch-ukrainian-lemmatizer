package org.sotnya.lemmatizer.uk.engine;

import morfologik.stemming.Dictionary;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Serves purposes of safe loading of static resources and providing them to other parts of the application.
 */
public final class UkrainianLemmatizerResources {
    /**
     * File containing default Ukrainian stopwords.
     */
    private static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    /**
     * Path to the file that contains the actual mapping.
     * The file stored within the `morfologik-ukrainian-search` package.
     */
    private static final String DICTIONARY_FILE_PATH = "ua/net/nlp/ukrainian.dict";

    private static final Logger LOGGER = ESLoggerFactory.getLogger(UkrainianLemmatizerResources.class.getSimpleName());

    public static final NormalizeCharMap NORMALIZE_MAP = new NormalizeCharMap.Builder() {{
        // different apostrophes
        add("\u2019", "'");
        add("\u2018", "'");
        add("\u02BC", "'");
        add("`", "'");
        add("´", "'");
        // ignored characters
        add("\u0301", "");
        add("\u00AD", "");
        add("ґ", "г");
        add("Ґ", "Г");
    }}.build();

    /**
     * Returns an unmodifiable instance of the default stop words set.
     *
     * @return default stop words set.
     */
    public static CharArraySet getDefaultStopSet() {
        return DefaultsHolder.DEFAULT_STOP_SET;
    }

    /**
     * Reads a .dict file produced by morfologik-ukrainian-search and constructs a new dictionary from it.
     * In case of file's absence must fail with {@link IOException}.
     *
     * @return New instance of morfologik {@link Dictionary} to be used in token filters.
     */
    public static Dictionary getDictionary() {
        return DefaultsHolder.DEFAULT_DICTIONARY;
    }

    /**
     * Atomically loads the DEFAULT_STOP_SET and DEFAULT_DICTIONARY in a lazy fashion once the outer class
     * accesses the static final set the first time.;
     */
    private static class DefaultsHolder {
        static final CharArraySet DEFAULT_STOP_SET;
        static final Dictionary DEFAULT_DICTIONARY;

        static {
            final ClassLoader loader = DefaultsHolder.class.getClassLoader();

            try {
                final Reader decodingReader = IOUtils.getDecodingReader(
                        loader.getResourceAsStream(DEFAULT_STOPWORD_FILE),
                        StandardCharsets.UTF_8);

                DEFAULT_STOP_SET = WordlistLoader.getSnowballWordSet(decodingReader);

                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("The stop-set has been loaded.");
            } catch (IOException | NullPointerException ex) {
                // default set should always be present as it is part of the
                // distribution (JAR)
                throw new RuntimeException("Unable to load default stopword set");
            }

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Started loading the ukrainian dictionary.");

            try {
                DEFAULT_DICTIONARY = Dictionary.read(loader.getResource(DICTIONARY_FILE_PATH));

                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("The ukrainian dictionary has been loaded successfully.");
            } catch (IOException | NullPointerException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
