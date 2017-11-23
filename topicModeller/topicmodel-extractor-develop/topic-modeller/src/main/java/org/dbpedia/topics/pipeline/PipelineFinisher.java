package org.dbpedia.topics.pipeline;

import org.dbpedia.topics.dataset.models.Dataset;
import org.dbpedia.topics.dataset.models.Instance;

/**
 * An interface for the last step of the pipeline.
 * Created by wlu on 07.06.16.
 */
public abstract class PipelineFinisher {
    /**
     * Performs the last step of the pipeline.
     * @param dataset
     */
    public void finishPipeline(Dataset dataset) {
        int ct = 0;

        for (Instance instance : dataset) {
            if (ct++ % 500 == 0) {
                System.out.println(ct);
            }

            finishInstance(instance);
        }
    }

    public abstract void finishInstance(Instance instance);

    public abstract void close();
}
