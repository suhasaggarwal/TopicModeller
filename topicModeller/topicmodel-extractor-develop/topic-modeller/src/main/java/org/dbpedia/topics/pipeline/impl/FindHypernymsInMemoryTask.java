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
public class FindHypernymsInMemoryTask extends PipelineTask {

    private static final String PARSE_TRIPLE_REGEX = "<(.*?)>\\s*<http://purl.org/linguistics/gold/hypernym>\\s*<(.*?)>.*\\.";

    private Map<String, List<String>> hypernyms = new HashMap<>();

    public FindHypernymsInMemoryTask(String hypernymsTtlFile) {
        hypernyms = Utils.readSubjectObjectMappings(PARSE_TRIPLE_REGEX, hypernymsTtlFile);
        System.out.println("Read hypernyms: "+hypernyms.size());
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

        List<String> temp = new ArrayList<>();

        instance.getSpotlightAnnotation().getResources().forEach(resource -> {
            temp.addAll(hypernyms.getOrDefault(resource.getUri(), new ArrayList<>()));
        });
        instance.setHypernyms(temp);
    }
}
