package org.dbpedia.topics.pipeline.impl;

import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.pipeline.PipelineTask;
import org.dbpedia.utils.SparqlConnector;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wlu on 09.06.16.
 */
public class FindHypernymsTask extends PipelineTask {

    private SparqlConnector sparqlConnector;
    private static Map<String, List<String>> cache = new HashMap<>();

    public FindHypernymsTask(String sparqlEndpoint) throws URISyntaxException {
        sparqlConnector = new SparqlConnector(sparqlEndpoint);
    }

    @Override
    public void processInstance(Instance instance) {
        if (instance.getSpotlightAnnotation() == null) {
            System.err.println("Document not annotated....");
            return;
        }

        if (instance.getSpotlightAnnotation().isEmpty()) {
            System.err.println("Annotation empty....");
            return;
        }

        List<String> hypernyms = new ArrayList<>();

        instance.getSpotlightAnnotation().getResources().forEach(res -> {
            if (!cache.containsKey(res.getUri())) {
                cache.put(res.getUri(), sparqlConnector.getHypernyms(res.getUri()));
            }
            hypernyms.addAll(cache.get(res.getUri()));
        });
        instance.setHypernyms(hypernyms);
    }
}
