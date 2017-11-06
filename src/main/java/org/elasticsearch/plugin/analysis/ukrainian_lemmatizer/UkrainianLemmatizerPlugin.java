package org.elasticsearch.plugin.analysis.ukrainian_lemmatizer;

import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianIndicesAnalysisModule;
import org.elasticsearch.plugins.Plugin;

import java.util.ArrayList;
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

    @Override
    public Collection<Class<? extends LifecycleComponent>> nodeServices() {
        return new ArrayList<>();
    }

    @Override
    public Settings additionalSettings() {
        return Settings.EMPTY;
    }
}
