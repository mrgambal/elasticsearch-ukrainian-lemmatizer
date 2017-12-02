package org.elasticsearch.index.analysis.ukrainian_lemmatizer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.morfologik.MorfologikFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.sotnya.lemmatizer.uk.engine.UkrainianLemmatizerResources;


public class UkrainianLemmatizerTokenFilterFactory extends AbstractTokenFilterFactory {
    @Inject
    public UkrainianLemmatizerTokenFilterFactory(Index index,
                                                 Settings indexSettings,
                                                 @Assisted String name,
                                                 @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
    }

    @Override
    public String name() {
        return "ukrainian_lemmatizer";
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new MorfologikFilter(tokenStream, UkrainianLemmatizerResources.getDictionary());
    }
}
