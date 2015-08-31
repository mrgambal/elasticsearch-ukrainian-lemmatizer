package org.elasticsearch.index.analysis.uk;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettings;
import org.sotnya.lemmagen.uk.engine.UkrainianLemmagen;

import java.io.IOException;
import java.net.URISyntaxException;


public class UkrainianLemmagenTokenFilterFactory extends AbstractTokenFilterFactory {
    @Inject
    public UkrainianLemmagenTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, String name, Settings settings) {
        super(index, indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        UkrainianLemmagen lemmatizer;

        try {
            lemmatizer = new UkrainianLemmagen();
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException("Unable to load default mapping tables", ex);
        }

        return new UkrainianLemmagenTokenFilter(tokenStream, lemmatizer);
    }
}
