/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.elasticsearch.indices.analysis.ukrainian_lemmatizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.MappingCharFilter;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.morfologik.MorfologikFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.sotnya.lemmatizer.uk.engine.UkrainianLemmatizerResources;

import java.io.Reader;

/**
 * A back-ported version of dictionary-based {@link Analyzer} for Ukrainian.
 * Original file is located [here](https://github.com/apache/lucene-solr/blob/master/lucene/analysis/morfologik/src/java/org/apache/lucene/analysis/uk/UkrainianMorfologikAnalyzer.java)
 */
public final class UkrainianAnalyzer extends StopwordAnalyzerBase {
    private final CharArraySet stemExclusionSet;

    /**
     * Builds an analyzer with the default stop words.
     */
    public UkrainianAnalyzer() {
        this(UkrainianLemmatizerResources.getDefaultStopSet());
    }

    /**
     * Builds an analyzer with the given stop words.
     *
     * @param stopwords a stopword set
     */
    public UkrainianAnalyzer(CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }

    /**
     * Builds an analyzer with the given stop words. If a non-empty stem exclusion set is
     * provided this analyzer will add a {@link SetKeywordMarkerFilter} before
     * stemming.
     *
     * @param stopwords        a stopword set
     * @param stemExclusionSet a set of terms not to be stemmed
     */
    public UkrainianAnalyzer(CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
    }

    @Override
    protected Reader initReader(String fieldName, Reader reader) {
        NormalizeCharMap normMap = new NormalizeCharMap.Builder() {{
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
        reader = new MappingCharFilter(normMap, reader);

        return reader;
    }

    /**
     * Creates a {@link TokenStreamComponents}
     * which tokenizes all the text in the provided {@link Reader}.
     *
     * @return A {@link TokenStreamComponents}
     * built from an {@link StandardTokenizer} filtered with
     * {@link StandardFilter}, {@link LowerCaseFilter}, {@link StopFilter}
     * , {@link SetKeywordMarkerFilter} if a stem exclusion set is
     * provided and {@link MorfologikFilter} on the Ukrainian dictionary.
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream result = new StandardFilter(source);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopwords);

        if (!stemExclusionSet.isEmpty()) {
            result = new SetKeywordMarkerFilter(result, stemExclusionSet);
        }

        result = UkrainianLemmatizerResources.getUkrainianLemmatizerTokenFilter(result);

        return new TokenStreamComponents(source, result);
    }
}
