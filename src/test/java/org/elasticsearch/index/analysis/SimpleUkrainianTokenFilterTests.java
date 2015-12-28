package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.test.ElasticsearchTestCase;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static com.google.common.io.Files.createTempDir;
import static org.elasticsearch.index.analysis.TestUtils.createAnalysisService;
import static org.hamcrest.Matchers.equalTo;

public class SimpleUkrainianTokenFilterTests extends ElasticsearchTestCase {

    @Test
    public void testTokenFilter() throws Exception {
        testToken("цямруймось", "цямруватися");
        testToken("статечної", "статечний");
        // apostrophe 2019
        testToken("любимо-мар'ївського", "любимо-мар'ївський");
        // apostrophe U+02BC
        testToken("любимо-марʼївського", "любимо-мар'ївський");
        testToken("стросс-кану", "стросс-кан");
    }

    @Test
    public void testAnalyzer() throws Exception {
        // apostrophe 2019
        testAnalyzer("б'ю іменинника", "бити", "іменинник");
        // apostrophe U+02BC
        testAnalyzer("бʼю іменинника", "бити", "іменинник");
    }

    private void testToken(String source, String expected) throws IOException {
        Index index = new Index("test");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", createTempDir())
                .put("index.analysis.filter.myLemmatizer.type", "ukrainian_lemmatizer")
                .build();
        AnalysisService analysisService = createAnalysisService(index, settings);
        TokenFilterFactory filterFactory = analysisService.tokenFilter("myLemmatizer");
        Tokenizer tokenizer = new KeywordTokenizer(new StringReader(source));
        TokenStream ts = filterFactory.create(tokenizer);
        CharTermAttribute term1 = ts.addAttribute(CharTermAttribute.class);

        ts.reset();

        assertThat(ts.incrementToken(), equalTo(true));
        assertThat(term1.toString(), equalTo(expected));
    }

    private void testAnalyzer(String source, String... expected_terms) throws IOException {
        Index index = new Index("test");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", createTempDir())
                .build();
        AnalysisService analysisService = createAnalysisService(index, settings);
        Analyzer analyzer = analysisService.analyzer("ukrainian").analyzer();
        TokenStream ts = analyzer.tokenStream("test", source);

        CharTermAttribute term1 = ts.addAttribute(CharTermAttribute.class);
        ts.reset();

        for (String expected : expected_terms) {
            assertThat(ts.incrementToken(), equalTo(true));
            assertThat(term1.toString(), equalTo(expected));
        }
    }
}
