package org.dbpedia.topics.pipeline.impl;

import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.io.StanfordLemmatizer;
import org.dbpedia.topics.io.StopWords;
import org.dbpedia.topics.pipeline.PipelineTask;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wlu on 09.06.16.
 */
public class FindLemmasTask extends PipelineTask {

    private StanfordLemmatizer lemmatizer;

    public FindLemmasTask() {
        lemmatizer = new StanfordLemmatizer();
    }

    @Override
    public void processInstance(Instance instance) {
        List<String> lemmas = lemmatizer.lemmatize(instance.getText());
        List<String> stopWords = Arrays.asList(StopWords.STOPWORDS);
        lemmas = lemmas.parallelStream()
                .filter(lemma -> {
                    boolean result = true;
                    //remove stop words
                    result = result && !stopWords.contains(lemma);
                    //remove tokens which consist of non-letters only
                    result = result && !lemma.matches("\\p{P}+");
                    return result;
                })
                .collect(Collectors.toList());
        instance.setLemmas(lemmas);
    }
}
