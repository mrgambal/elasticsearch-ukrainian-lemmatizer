package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Analyzer;
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
import org.elasticsearch.index.analysis.ukrainian_lemmatizer.UkrainianLemmatizerTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianAnalyzer;
import org.elasticsearch.test.ElasticsearchTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static com.google.common.io.Files.createTempDir;
import static org.elasticsearch.common.settings.ImmutableSettings.Builder.EMPTY_SETTINGS;
import static org.hamcrest.Matchers.instanceOf;

/**
 */
public class UkrainianAnalysisTests extends ElasticsearchTestCase {

    @Test
    public void testDefaultsUkrainianAnalysis() {
        Index index = new Index("test");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("path.home", createTempDir())
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();
        Injector parentInjector = new ModulesBuilder().add(new SettingsModule(EMPTY_SETTINGS), new EnvironmentModule(new Environment(settings)), new IndicesAnalysisModule()).createInjector();
        Injector injector = new ModulesBuilder().add(
                new IndexSettingsModule(index, settings),
                new IndexNameModule(index),
                new AnalysisModule(EMPTY_SETTINGS, parentInjector.getInstance(IndicesAnalysisService.class)).addProcessor(new UkrainianLemmatizerBinderProcessor()))
                .createChildInjector(parentInjector);

        AnalysisService analysisService = injector.getInstance(AnalysisService.class);
        TokenFilterFactory tokenizerFactory = analysisService.tokenFilter("ukrainian_lemmatizer");

        MatcherAssert.assertThat(tokenizerFactory, instanceOf(UkrainianLemmatizerTokenFilterFactory.class));

        Analyzer analyzer = analysisService.analyzer("ukrainian").analyzer();

        MatcherAssert.assertThat(analyzer, instanceOf(UkrainianAnalyzer.class));
    }
}
