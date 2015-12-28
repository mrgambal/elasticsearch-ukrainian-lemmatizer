package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.ukrainian_lemmatizer.UkrainianLemmatizerTokenFilterFactory;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianAnalyzer;
import org.elasticsearch.test.ElasticsearchTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static com.google.common.io.Files.createTempDir;
import static org.elasticsearch.index.analysis.TestUtils.createAnalysisService;
import static org.hamcrest.Matchers.instanceOf;

public class UkrainianAnalysisTests extends ElasticsearchTestCase {

    @Test
    public void testDefaultsUkrainianAnalysis() {
        Index index = new Index("test");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("path.home", createTempDir())
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();

        AnalysisService analysisService = createAnalysisService(index, settings);
        TokenFilterFactory tokenizerFactory = analysisService.tokenFilter("ukrainian_lemmatizer");

        MatcherAssert.assertThat(tokenizerFactory, instanceOf(UkrainianLemmatizerTokenFilterFactory.class));

        Analyzer analyzer = analysisService.analyzer("ukrainian").analyzer();

        MatcherAssert.assertThat(analyzer, instanceOf(UkrainianAnalyzer.class));
    }
}
