package org.dbpedia.topics.inference.service.models;

import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.pipeline.PipelineFinisher;

/**
 * Created by wlu on 19.07.16.
 */
public class EmptyFinisher extends PipelineFinisher {

    private Instance processed;

    @Override
    public void finishInstance(Instance instance) {
        this.processed = instance;
    }

    @Override
    public void close() {
    }

    public Instance getProcessedInstance() {
        return processed;
    }
}
