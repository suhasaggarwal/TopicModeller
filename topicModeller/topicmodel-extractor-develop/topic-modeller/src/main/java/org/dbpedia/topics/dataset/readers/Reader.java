package org.dbpedia.topics.dataset.readers;

import org.dbpedia.topics.dataset.models.Dataset;

/**
 * Interface for reading the data that will be used for topic modelling.
 * Created by wlu on 26.05.16.
 */
public abstract class Reader {

    /**
     * Reads and returns the dataset.
     * @return
     */
    public abstract Dataset readDataset();
}
