package org.dbpedia.topics.pipeline.impl;

import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.pipeline.PipelineTask;
import org.dbpedia.utils.annotation.SpotlightAnnotator;
import org.dbpedia.utils.annotation.models.SpotlightAnnotation;

/**
 * Created by wlu on 09.06.16.
 */
public class AnnotateTask extends PipelineTask {

    private SpotlightAnnotator spotlightAnnotator;

    public AnnotateTask(String spotlightEndpoint) {
        spotlightAnnotator = new SpotlightAnnotator(spotlightEndpoint);
    }

    @Override
    public void processInstance(Instance instance) {
        SpotlightAnnotation annotation;
        try {
            annotation = spotlightAnnotator.annotate(instance.getText(), 0, 0.5);
            instance.setSpotlightAnnotation(annotation);
        }
        catch (Exception e) {
            System.err.println("Error when annotating: " + instance.getUri());
            System.err.println(instance.getText());
            e.printStackTrace();
        }
    }
}
