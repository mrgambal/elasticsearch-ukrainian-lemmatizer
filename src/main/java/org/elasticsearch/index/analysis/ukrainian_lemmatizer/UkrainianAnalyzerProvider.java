package org.elasticsearch.index.analysis.ukrainian_lemmatizer;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettings;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianAnalyzer;

/**
 */
public class UkrainianAnalyzerProvider extends AbstractIndexAnalyzerProvider<UkrainianAnalyzer> {

    private final UkrainianAnalyzer analyzer;

    @Inject
    public UkrainianAnalyzerProvider(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);

        analyzer = new UkrainianAnalyzer();
        analyzer.setVersion(version);
    }

    @Override
    public UkrainianAnalyzer get() {
        return this.analyzer;
    }
}