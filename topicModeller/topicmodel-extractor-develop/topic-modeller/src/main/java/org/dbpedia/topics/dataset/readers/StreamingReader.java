package org.dbpedia.topics.dataset.readers;

import org.dbpedia.topics.dataset.models.Instance;

import java.util.stream.Stream;

/**
 * Interface for reading the data that will be used for topic modelling.
 * Created by wlu on 14.07.16.
 */
public abstract class StreamingReader {

    /**
     * Reads and returns the dataset.
     * @return
     */
    public abstract Stream<Instance> readDataset();
}
