package org.dbpedia.topics.pipeline;

/**
 * Created by wlu on 14.07.16.
 */
public interface IPipeline {
    boolean addTask(PipelineTask task);
    void doWork();
}
