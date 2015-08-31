package org.elasticsearch.index.analysis.uk;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import org.sotnya.lemmagen.uk.engine.UkrainianLemmagen;

public class UkrainianLemmagenTokenFilter extends TokenFilter {
    private UkrainianLemmagen lemmatizer = null;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

    public UkrainianLemmagenTokenFilter(final TokenStream input, final UkrainianLemmagen lemmatizer) {
        super(input);
        this.lemmatizer = lemmatizer;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        if (!keywordAttr.isKeyword()) {
            termAtt.setEmpty().append(lemmatizer.lemmatize(termAtt));
        }

//        CharSequence lemma = lemmatizer.lemmatize(termAtt);
//        if (!keywordAttr.isKeyword() && !equalCharSequences(lemma, termAtt)) {
//            termAtt.setEmpty().append(lemmatizer.lemmatize(termAtt));
//        }

        return true;
    }

    /**
     * Compare two char sequences for equality. Assumes non-null arguments.
     */
    private boolean equalCharSequences(CharSequence s1, CharSequence s2) {
        char rawSymbol;
        int len1 = s1.length();
        int len2 = s2.length();

        if (len1 != len2) return false;

        for (int i = len1; --i >= 0; ) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }

        return true;
    }
}
