package org.elasticsearch.index.analysis.ukrainian_lemmatizer;

import org.apache.lucene.analysis.charfilter.MappingCharFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractCharFilterFactory;
import org.sotnya.lemmatizer.uk.engine.UkrainianLemmatizerResources;

import java.io.Reader;

public class UkrainianLemmatizerCharFilterFactory extends AbstractCharFilterFactory {
    public UkrainianLemmatizerCharFilterFactory(IndexSettings indexSettings,
                                                Environment env,
                                                String name,
                                                Settings settings) {
        super(indexSettings, name);
    }

    @Override
    public Reader create(Reader tokenStream) {
        return new MappingCharFilter(UkrainianLemmatizerResources.NORMALIZE_MAP, tokenStream);
    }
}
