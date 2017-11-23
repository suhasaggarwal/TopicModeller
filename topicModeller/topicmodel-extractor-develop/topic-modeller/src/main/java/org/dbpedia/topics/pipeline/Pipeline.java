package org.dbpedia.topics.pipeline;

import org.dbpedia.topics.dataset.models.Dataset;
import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.dataset.readers.Reader;
import org.dbpedia.topics.pipeline.impl.MongoDBInsertFinisher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlu on 07.06.16.
 */
public class Pipeline implements IPipeline {
    private List<PipelineTask> pipelineTasks = new ArrayList<>();
    private Reader datasetReader;
    private PipelineFinisher pipelineFinisher;

    /**
     * Creates a Pipeline.
     * The source dataset will be provided by the Reader.readDataset() method.
     * @param datasetReader
     * @param pipelineFinisher
     */
    public Pipeline(Reader datasetReader, PipelineFinisher pipelineFinisher) {
        this.datasetReader = datasetReader;
        this.pipelineFinisher = pipelineFinisher;
    }

    /**
     * Adds a task to the pipeline.
     * @param task
     * @return as specified in List.add()
     */
    public boolean addTask(PipelineTask task) {
        return this.pipelineTasks.add(task);
    }

    /**
     * Reads the dataset, passes it for sequential processing by the tasks and finishes the pipeline.
     * Eventually, the finisher
     */
    public void doWork() {
        Dataset dataset = datasetReader.readDataset();
        int ct = 0;

        for (Instance instance : dataset) {
            if (++ct % 500 == 0) {
                System.out.println(ct);
            }

            // skip the pipeline if the instance is present in the database
            if (pipelineFinisher instanceof MongoDBInsertFinisher) {
                MongoDBInsertFinisher casted = (MongoDBInsertFinisher) pipelineFinisher;
                if (casted.recordAlreadyExists(instance)) {
                    continue;
                }
            }

            for (PipelineTask pipelineTask : pipelineTasks) {
                pipelineTask.processInstance(instance);
            }
            pipelineFinisher.finishInstance(instance);
        }

    }
    /**
     * Reads the dataset, passes it for sequential processing by the tasks and finishes the pipeline.
     * Eventually, the finisher
     */
    public void doWorkBulk() {
        Dataset dataset = datasetReader.readDataset();

        for (PipelineTask pipelineTask : pipelineTasks) {
            System.out.println(pipelineTask.getClass());
            dataset = pipelineTask.start(dataset);
        }

        pipelineFinisher.finishPipeline(dataset);
    }
}
