package org.elasticsearch.plugin.analysis.ukrainian_lemmatizer;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianIndicesAnalysisModule;
import org.elasticsearch.plugins.Plugin;

import java.util.ArrayList;
import java.util.Collection;

public class UkrainianLemmatizerPlugin extends Plugin {
    private final static String NAME = "ukrainian-lemmatizer";
    private final static String DESCRIPTION = "Ukrainian lemmatizer analysis support";

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
        Collection<Module> classes = new ArrayList<>();
        classes.add(new UkrainianIndicesAnalysisModule());

        return classes;
    }
}
