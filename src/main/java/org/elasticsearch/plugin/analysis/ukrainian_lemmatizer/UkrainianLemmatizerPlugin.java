package org.elasticsearch.plugin.analysis.ukrainian_lemmatizer;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.ukrainian_lemmatizer.UkrainianLemmatizerBinderProcessor;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianIndicesAnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

import java.util.ArrayList;
import java.util.Collection;

public class UkrainianLemmatizerPlugin extends AbstractPlugin {
    @Override
    public String name() {
        return "ukrainian-lemmatizer";
    }

    @Override
    public String description() {
        return "Ukrainian lemmatizer analysis support";
    }

    @Override
    public Collection<Class<? extends Module>> modules() {
        Collection<Class<? extends Module>> classes = new ArrayList<>();
        classes.add(UkrainianIndicesAnalysisModule.class);
        return classes;
    }

    public void onModule(AnalysisModule module) {
        module.addProcessor(new UkrainianLemmatizerBinderProcessor());
    }
}
