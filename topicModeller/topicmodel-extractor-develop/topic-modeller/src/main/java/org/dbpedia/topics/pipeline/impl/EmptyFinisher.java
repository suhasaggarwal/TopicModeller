package org.dbpedia.topics.pipeline.impl;

import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.pipeline.PipelineFinisher;

/**
 * Created by wlu on 09.06.16.
 */
public class EmptyFinisher extends PipelineFinisher {

    @Override
    public void finishInstance(Instance instance) { }

    @Override
    public void close() {}
}
