package org.dbpedia.topics.pipeline;

import org.dbpedia.topics.dataset.models.Dataset;
import org.dbpedia.topics.dataset.models.Instance;

/**
 * An interface for a single processing step of the data within the processing pipeline.
 * Created by wlu on 07.06.16.
 */
public abstract class PipelineTask {
    /**
     * Processes the dataset.
     * @param dataset
     * @return Returns the processed dataset.
     */
    public Dataset start(Dataset dataset) {
        int ct = 0;

        for (Instance instance : dataset) {
            if (ct++ % 500 == 0) {
                System.out.println(ct);
            }

            processInstance(instance);
        }

        return dataset;
    }

    public abstract void processInstance(Instance instance);
}
