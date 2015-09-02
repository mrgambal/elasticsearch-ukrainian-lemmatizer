package org.elasticsearch.index.analysis.uk;

import org.elasticsearch.index.analysis.AnalysisModule;

public class UkrainianLemmatizerBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {
    @Override
    public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
        tokenFiltersBindings.processTokenFilter("ukrainian_lemmagen", UkrainianLemmatizerTokenFilterFactory.class);
    }
}
