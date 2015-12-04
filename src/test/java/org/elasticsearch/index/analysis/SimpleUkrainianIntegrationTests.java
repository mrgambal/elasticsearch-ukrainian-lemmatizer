package org.elasticsearch.index.analysis;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.plugin.analysis.ukrainian_lemmatizer.UkrainianLemmatizerPlugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ESIntegTestCase.ClusterScope(numDataNodes = 1, scope = ESIntegTestCase.Scope.SUITE)
public class SimpleUkrainianIntegrationTests extends ESIntegTestCase {

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return Settings.builder()
                .put(super.nodeSettings(nodeOrdinal))
                .put("plugin.types", UkrainianLemmatizerPlugin.class.getName())
                .build();
    }

    @Test
    public void testUkrainianAnalyzer() throws ExecutionException, InterruptedException {
        AnalyzeResponse response = client().admin().indices()
                .prepareAnalyze("б'ючи іменинника").setAnalyzer("ukrainian")
                .execute().get();

        assertThat(response, notNullValue());
        assertThat(response.getTokens().size(), is(2));
    }

    @Test
    public void testUkrainianLemmatizerTokenFilter() throws ExecutionException, InterruptedException {
        AnalyzeResponse response = client().admin().indices()
                .prepareAnalyze("конденсаторної").setTokenFilters("ukrainian_lemmatizer")
                .execute().get();

        assertThat(response, notNullValue());
        assertThat(response.getTokens().size(), is(1));
    }

    @Test
    public void testUkrainianAnalyzerInMapping() throws ExecutionException, InterruptedException, IOException {
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
