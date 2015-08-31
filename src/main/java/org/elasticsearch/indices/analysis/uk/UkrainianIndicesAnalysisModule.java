package org.elasticsearch.indices.analysis.uk;

import org.elasticsearch.common.inject.AbstractModule;

/**
 */
public class UkrainianIndicesAnalysisModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UkrainianIndicesAnalysis.class).asEagerSingleton();
    }
}
