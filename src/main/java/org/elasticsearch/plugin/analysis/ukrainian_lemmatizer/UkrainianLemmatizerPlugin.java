package org.elasticsearch.plugin.analysis.ukrainian_lemmatizer;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.ukrainian_lemmatizer.UkrainianLemmatizerBinderProcessor;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianIndicesAnalysisModule;
import org.elasticsearch.plugins.Plugin;

import java.util.Collection;
import java.util.Collections;

public class UkrainianLemmatizerPlugin extends Plugin {
    private final static String NAME = "ukrainian-lemmatizer";
    private final static String DESCRIPTION = "Ukrainian lemmatizer analysis support";

    private final Settings settings;

    public UkrainianLemmatizerPlugin(Settings settings) {
        this.settings = settings;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public Collection<Module> nodeModules() {
        return Collections.singletonList(new UkrainianIndicesAnalysisModule());
    }

    /**
     * Provides the availability to use the analyser within the custom analysis settings on per-index basis.
     * IntelliJ though marks it as unused.
     *
     * @param module Current analysis module within the scope's injector.
     */
    public void onModule(AnalysisModule module) {
        module.addProcessor(new UkrainianLemmatizerBinderProcessor());
    }
}
