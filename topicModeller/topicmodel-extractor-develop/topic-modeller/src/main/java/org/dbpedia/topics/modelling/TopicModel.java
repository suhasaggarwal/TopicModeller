package org.dbpedia.topics.modelling;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by wlu on 26.05.16.
 */
public interface TopicModel {
    /**
     * Predicts the coverage of mined topics for the given text.
     * @param text
     * @return
     */
    double[] predict (String text);

    void createModel(List<String> input, int numIterations, int numThreads) throws IOException;

    void saveToFile(String outputfile) throws IOException;

    void readFromFile(String inputfile) throws IOException, ClassNotFoundException;

    void describeTopicModel(String outputFilename, int numTopicDescribingWords) throws IOException;
}
