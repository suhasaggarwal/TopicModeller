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
public class FindCategoriesInMemoryTask extends PipelineTask {

    private static final String PARSE_TRIPLE_REGEX = "<(.*?)>\\s*<http://purl.org/dc/terms/subject>\\s*<(.*?)>.*\\.";

    private Map<String, List<String>> categories = new HashMap<>();

    public FindCategoriesInMemoryTask(String categoriesTtlFile) {
        categories = Utils.readSubjectObjectMappings(PARSE_TRIPLE_REGEX, categoriesTtlFile);
        System.out.println("Read categories: "+categories.size());
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
            resource.setDctSubjects(categories.getOrDefault(resource.getUri(), new ArrayList<>()));
        });
    }
}
