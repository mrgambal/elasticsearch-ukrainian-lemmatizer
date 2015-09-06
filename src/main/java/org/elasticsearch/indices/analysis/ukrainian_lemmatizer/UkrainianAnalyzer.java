package org.elasticsearch.indices.analysis.ukrainian_lemmatizer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.elasticsearch.index.analysis.ukrainian_lemmatizer.UkrainianLemmatizerTokenFilter;
import org.sotnya.lemmatizer.uk.engine.UkrainianLemmatizer;

import java.io.Reader;

public class UkrainianAnalyzer extends StopwordAnalyzerBase {
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final Tokenizer source = new StandardTokenizer(reader);
        TokenStream result = new StandardFilter(source);

        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopwords);
        result = new SetKeywordMarkerFilter(result, new CharArraySet(0, true));
        result = new UkrainianLemmatizerTokenFilter(result, new UkrainianLemmatizer());

        return new TokenStreamComponents(source, result);
    }
}
