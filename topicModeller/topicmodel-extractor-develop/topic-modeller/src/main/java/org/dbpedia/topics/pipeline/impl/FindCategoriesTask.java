package org.dbpedia.topics.pipeline.impl;

import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.pipeline.PipelineTask;
import org.dbpedia.utils.SparqlConnector;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wlu on 09.06.16.
 */
public class FindCategoriesTask extends PipelineTask {

    private SparqlConnector sparqlConnector;
    private static Map<String, List<String>> cache = new HashMap<>();

    public FindCategoriesTask(String sparqlEndpoint) throws URISyntaxException {
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

        instance.getSpotlightAnnotation().getResources().forEach(resource -> {
            try {
                if (!cache.containsKey(resource.getUri())) {
                    cache.put(resource.getUri(), sparqlConnector.getCategories(resource.getUri()));
                }
                resource.setDctSubjects(cache.get(resource.getUri()));
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println("Couldn't query for categories " + resource.getUri());
            }
        });
    }
}
