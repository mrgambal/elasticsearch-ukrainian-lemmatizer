package org.elasticsearch.indices.analysis.ukrainian_lemmatizer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.morfologik.MorfologikFilter;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lucene.Lucene;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalyzerScope;
import org.elasticsearch.index.analysis.PreBuiltAnalyzerProviderFactory;
import org.elasticsearch.index.analysis.PreBuiltTokenFilterFactoryFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.sotnya.lemmatizer.uk.engine.UkrainianLemmatizerResources;

/**
 * Registers indices level analysis components so, if not explicitly configured, will be shared
 * among all indices.
 */
public class UkrainianIndicesAnalysis extends AbstractComponent {

    @Inject
    public UkrainianIndicesAnalysis(Settings settings, IndicesAnalysisService indicesAnalysisService) {
        super(settings);

        final String ANALYZER_KEY = "ukrainian";
        final String FILTER_KEY = "ukrainian_lemmatizer";

        indicesAnalysisService.analyzerProviderFactories().put(
                ANALYZER_KEY,
                new PreBuiltAnalyzerProviderFactory(
                        ANALYZER_KEY,
                        AnalyzerScope.INDICES,
                        new UkrainianAnalyzer(Lucene.VERSION)));

        indicesAnalysisService.tokenFilterFactories().put(
                FILTER_KEY,
                new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
                    @Override
                    public String name() {
                        return FILTER_KEY;
                    }

                    @Override
                    public TokenStream create(TokenStream tokenStream) {
                        return new MorfologikFilter(tokenStream, UkrainianLemmatizerResources.getDictionary());
                    }
                }));
    }
}
