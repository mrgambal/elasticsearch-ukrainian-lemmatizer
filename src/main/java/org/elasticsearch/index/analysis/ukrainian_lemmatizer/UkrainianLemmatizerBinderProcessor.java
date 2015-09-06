package org.elasticsearch.index.analysis.ukrainian_lemmatizer;

import org.elasticsearch.index.analysis.AnalysisModule;

public class UkrainianLemmatizerBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {
    @Override
    public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
        tokenFiltersBindings.processTokenFilter("ukrainian_lemmatizer", UkrainianLemmatizerTokenFilterFactory.class);
    }

    @Override
    public void processAnalyzers(AnalyzersBindings analyzersBindings) {
        analyzersBindings.processAnalyzer("ukrainian", UkrainianAnalyzerProvider.class);
    }
}
