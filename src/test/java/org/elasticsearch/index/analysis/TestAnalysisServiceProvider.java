package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.ukrainian_lemmatizer.UkrainianLemmatizerTokenFilterFactory;
import org.elasticsearch.indices.analysis.ukrainian_lemmatizer.UkrainianAnalyzer;
import org.elasticsearch.plugin.analysis.ukrainian_lemmatizer.UkrainianLemmatizerPlugin;
import org.elasticsearch.test.ESTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;

@RunWith(com.carrotsearch.randomizedtesting.RandomizedRunner.class)
public class TestAnalysisServiceProvider extends ESTestCase {
    @Test
    public void testDefaultsUkrainianAnalysis() throws IOException {
        final TestAnalysis analysis = createTestAnalysis(
                new Index("test", "_na_"),
                Settings.EMPTY,
                new UkrainianLemmatizerPlugin());

        TokenFilterFactory tokenizerFactory = analysis.tokenFilter.get("ukrainian");

        MatcherAssert.assertThat(tokenizerFactory, instanceOf(UkrainianLemmatizerTokenFilterFactory.class));

        Analyzer analyzer = analysis.indexAnalyzers.get("ukrainian").analyzer();

        MatcherAssert.assertThat(analyzer, instanceOf(UkrainianAnalyzer.class));
    }
}
