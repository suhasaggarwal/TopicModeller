package org.dbpedia.topics.pipeline;

import org.dbpedia.topics.dataset.readers.Reader;

/**
 * Created by wlu on 15.06.16.
 */
public class PipelineThread implements Runnable {
    private Reader reader;
    private PipelineFinisher finisher;
    private Pipeline pipeline;

    public PipelineThread(Reader reader, PipelineFinisher finisher, Pipeline pipeline) {
        this.reader = reader;
        this.finisher = finisher;
        this.pipeline = pipeline;
    }

    @Override
    public void run() {
        System.out.println("Hello from thread " + Thread.currentThread());
        try {
            pipeline.doWork();
        }
        finally {
            finisher.close();
//            reader.close();
        }
    }
}
