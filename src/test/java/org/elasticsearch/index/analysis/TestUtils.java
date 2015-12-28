package org.elasticsearch.index.analysis;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.EnvironmentModule;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexNameModule;
import org.elasticsearch.index.analysis.ukrainian_lemmatizer.UkrainianLemmatizerBinderProcessor;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;

public class TestUtils {
    public static AnalysisService createAnalysisService(Index index, Settings settings) {
        Settings indexSettings = ImmutableSettings.settingsBuilder().put(settings)
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();
        Injector parentInjector = new ModulesBuilder().add(new SettingsModule(settings), new EnvironmentModule(new Environment(settings))).createInjector();
        Injector injector = new ModulesBuilder().add(
                new IndexSettingsModule(index, indexSettings),
                new IndexNameModule(index),
                new AnalysisModule(settings, parentInjector.getInstance(IndicesAnalysisService.class)).addProcessor(new UkrainianLemmatizerBinderProcessor()))
                .createChildInjector(parentInjector);

        return injector.getInstance(AnalysisService.class);
    }
}