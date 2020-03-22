package org.elasticsearch.index.analysis.ukrainian_lemmatizer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.morfologik.MorfologikFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.sotnya.lemmatizer.uk.engine.UkrainianLemmatizerResources;


public final class UkrainianLemmatizerTokenFilterFactory extends AbstractTokenFilterFactory {
    public UkrainianLemmatizerTokenFilterFactory(IndexSettings indexSettings,
                                                 Environment environment,
                                                 String name,
                                                 Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public String name() {
        return "ukrainian";
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new MorfologikFilter(tokenStream, UkrainianLemmatizerResources.getDictionary());
    }
}
