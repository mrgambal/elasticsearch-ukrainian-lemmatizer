package org.elasticsearch.index.analysis.ukrainian_lemmatizer;

import org.apache.lucene.analysis.util.CharArraySet;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.Analysis;
import org.elasticsearch.index.settings.IndexSettingsService;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianAnalyzer;
import org.sotnya.lemmatizer.uk.engine.UkrainianLemmatizerResources;

public class UkrainianAnalyzerProvider extends AbstractIndexAnalyzerProvider<UkrainianAnalyzer> {

    private final UkrainianAnalyzer analyzer;

    @Inject
    public UkrainianAnalyzerProvider(Index index, IndexSettingsService indexSettingsService, Environment env, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);

        analyzer = new UkrainianAnalyzer(
                Analysis.parseStopWords(env, settings, UkrainianLemmatizerResources.getDefaultStopSet()),
                Analysis.parseStemExclusion(settings, CharArraySet.EMPTY_SET),
                version
        );
    }

    @Override
    public UkrainianAnalyzer get() {
        return this.analyzer;
    }
}
