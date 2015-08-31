package org.sotnya.lemmagen.uk.engine;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;

public class UkrainianLemmagen {
    private final Map<String, String> x;

    public UkrainianLemmagen() throws IOException, URISyntaxException {
        final InputStream is = UkrainianLemmagen.class.getClassLoader().getResourceAsStream("mapping_sorted.csv");
        final String separator = ",";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            x = reader.lines()
                    .map(line -> line.split(separator))
                    .collect(Collectors.toMap(p -> p[0], p -> p[1]));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public CharSequence lemmatize(CharTermAttribute termAtt) {
        // replace ukrainian apostrophe to english single quote is being used in mapping
        final String term = termAtt.toString().replace('’', '\'');

        if (x.containsKey(term)) {
            return x.get(term);
        } else
            return term;
    }
}
