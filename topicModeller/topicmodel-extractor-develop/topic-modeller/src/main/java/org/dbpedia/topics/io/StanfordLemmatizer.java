package org.dbpedia.topics.io;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class StanfordLemmatizer {

    protected StanfordCoreNLP pipeline;

    public StanfordLemmatizer() {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        this.pipeline = new StanfordCoreNLP(props);
    }

    public List<String> lemmatize(String documentText) {
        List<String> lemmas = new LinkedList<String>();
        Annotation document = new Annotation(documentText);
        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }

        // remove -lrb-, -rrb-, -lsb-, ... -- the brackets
        lemmas = lemmas.stream()
            .map(lemma -> {
                return lemma
                        .replaceAll("(?i)-[lr].b-", "")
                        .toLowerCase()
                        .trim();
            })
            .filter(lemma -> lemma.length() > 0)
            .collect(Collectors.toList());

        return lemmas;
    }
}
