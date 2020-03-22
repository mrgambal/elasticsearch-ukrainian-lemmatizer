package org.elasticsearch.index.analysis;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.plugin.analysis.ukrainian_lemmatizer.UkrainianLemmatizerPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class TestUkrainianAnalysisIntegration extends ESIntegTestCase {

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Collections.singleton(UkrainianLemmatizerPlugin.class);
    }

    @Test
    public void testUkrainianAnalyzer() throws ExecutionException, InterruptedException {
        AnalyzeResponse response = client().admin().indices()
                .prepareAnalyze("б'ючи іменинника").setAnalyzer("ukrainian")
                .execute().get();

        MatcherAssert.assertThat(response, notNullValue());
        MatcherAssert.assertThat(response.getTokens().size(), is(2));
    }

    @Test
    public void testUkrainianLemmatizerTokenFilter() throws ExecutionException, InterruptedException {
        AnalyzeResponse response = client().admin().indices()
                .prepareAnalyze("конденсаторної").addTokenFilter("ukrainian")
                .execute().get();

        MatcherAssert.assertThat(response, notNullValue());
        MatcherAssert.assertThat(response.getTokens().size(), is(1));
    }

    @Test
    public void testUkrainianAnalyzerInMapping() throws IOException {
        final XContentBuilder mapping = jsonBuilder().startObject()
                .startObject("type")
                .startObject("properties")
                .startObject("foo")
                .field("type", "string")
                .field("analyzer", "ukrainian")
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        client().admin().indices().prepareCreate("test").addMapping("type", mapping).get();

        index("test", "type", "1", "foo", "б'ючи іменинника");

        ensureYellow();
    }

}
