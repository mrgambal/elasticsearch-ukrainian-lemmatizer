package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.ukrainian_lemmatizer.UkrainianLemmatizerTokenFilterFactory;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianAnalyzer;
import org.elasticsearch.test.ESTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.elasticsearch.common.settings.Settings.settingsBuilder;
import static org.hamcrest.Matchers.instanceOf;

/**
 */
public class UkrainianAnalysisTests extends ESTestCase {

    @Test
    public void testDefaultsUkrainianAnalysis() {
        Settings settings = settingsBuilder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", createTempDir())
                .build();
        AnalysisService analysisService = TestUtils.createAnalysisService(new Index("test"), settings);
        TokenFilterFactory tokenizerFactory = analysisService.tokenFilter("ukrainian_lemmatizer");

        MatcherAssert.assertThat(tokenizerFactory, instanceOf(UkrainianLemmatizerTokenFilterFactory.class));

        Analyzer analyzer = analysisService.analyzer("ukrainian").analyzer();

        MatcherAssert.assertThat(analyzer, instanceOf(UkrainianAnalyzer.class));
    }
}
