package org.elasticsearch.index.analysis.ukrainian_lemmatizer;

import org.apache.lucene.analysis.CharArraySet;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.Analysis;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianAnalyzer;
import org.sotnya.lemmatizer.uk.engine.UkrainianLemmatizerResources;

public final class UkrainianAnalyzerProvider extends AbstractIndexAnalyzerProvider<UkrainianAnalyzer> {

    private final UkrainianAnalyzer analyzer;

    public UkrainianAnalyzerProvider(IndexSettings indexSettings,
                                     Environment env,
                                     String name,
                                     Settings settings) {
        super(indexSettings, name, settings);

        analyzer = new UkrainianAnalyzer(
                new UkrainianLemmatizerTokenFilterFactory(indexSettings, env, name, settings),
                new UkrainianLemmatizerCharFilterFactory(indexSettings, env, name, settings),
                Analysis.parseStopWords(env, settings, UkrainianLemmatizerResources.getDefaultStopSet()),
                Analysis.parseStemExclusion(settings, CharArraySet.EMPTY_SET)
        );
    }

    @Override
    public UkrainianAnalyzer get() {
        return this.analyzer;
    }
}
