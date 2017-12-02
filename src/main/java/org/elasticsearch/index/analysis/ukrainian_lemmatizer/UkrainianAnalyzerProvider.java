package org.elasticsearch.index.analysis.ukrainian_lemmatizer;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianAnalyzer;

public class UkrainianAnalyzerProvider extends AbstractIndexAnalyzerProvider<UkrainianAnalyzer> {

    private final UkrainianAnalyzer analyzer;

    @Inject
    public UkrainianAnalyzerProvider(Index index, Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);

        analyzer = new UkrainianAnalyzer(version);
    }

    @Override
    public UkrainianAnalyzer get() {
        return this.analyzer;
    }
}
