package org.dbpedia.topics.pipeline.impl;

import org.dbpedia.topics.utils.Utils;
import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.pipeline.PipelineTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wlu on 09.06.16.
 */
public class FindTypesInMemoryTask extends PipelineTask {

    private static final String PARSE_TRIPLE_REGEX = "<(.*?)>\\s*<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>\\s*<(.*?)>.*\\.";

    private Map<String, List<String>> types = new HashMap<>();

    public FindTypesInMemoryTask(String typesTtlFile) {
        types = Utils.readSubjectObjectMappings(PARSE_TRIPLE_REGEX, typesTtlFile);
        System.out.println("Read types: "+types.size());
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
            resource.setRdfTypes(types.getOrDefault(resource.getUri(), new ArrayList<>()));
        });
    }
}
