package org.elasticsearch.index.analysis.ukrainian_lemmatizer;

import java.io.IOException;
import java.util.Optional;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;

import org.sotnya.lemmatizer.uk.engine.UkrainianLemmatizer;

public class UkrainianLemmatizerTokenFilter extends TokenFilter {
    private UkrainianLemmatizer lemmatizer = null;
    private final ESLogger logger = ESLoggerFactory.getLogger(this.getClass().getSimpleName());
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

    public UkrainianLemmatizerTokenFilter(final TokenStream input, final UkrainianLemmatizer lemmatizer) {
        super(input);
        this.lemmatizer = lemmatizer;
    }

    /**
     * @return true if token was added to search/analysis stream
     * @throws IOException
     */
    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        logger.debug(String.format("Looking for term %s.", termAtt));

        Optional<CharSequence> lemma = lemmatizer.lemmatize(termAtt);

        if (lemma.isPresent()) {
            if (!keywordAttr.isKeyword() && !equalCharSequences(lemma.get(), termAtt)) {
                termAtt.setEmpty().append(lemma.get());
            }

            logger.debug(String.format("Found lemma %s", lemma.get()));
        }

        return true;
    }

    /**
     * Compare two char sequences for equality. Assumes non-null arguments.
     */
    private boolean equalCharSequences(CharSequence s1, CharSequence s2) {
        int len1 = s1.length();

        if (len1 != s2.length()) return false;

        for (int i = len1; --i >= 0; ) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }

        return true;
    }
}
