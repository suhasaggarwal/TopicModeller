package org.dbpedia.topics.inference;

import org.dbpedia.topics.inference.service.models.EmptyFinisher;
import org.dbpedia.topics.inference.service.models.InputToDataset;
import org.dbpedia.topics.modelling.LDAInputGenerator;
import org.dbpedia.topics.modelling.LdaModel;
import org.dbpedia.topics.pipeline.Pipeline;
import org.dbpedia.topics.pipeline.PipelineTask;
import org.dbpedia.topics.pipeline.impl.FindCategoriesInMemoryTask;
import org.dbpedia.topics.pipeline.impl.FindHypernymsInMemoryTask;
import org.dbpedia.topics.pipeline.impl.FindLemmasTask;
import org.dbpedia.topics.pipeline.impl.FindTypesInMemoryTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Suhas Aggarwal
 */
public class Inferencer {

    private static Inferencer instance = null;

    public static Inferencer getInferencer(String[] features) {
        if (instance == null) {
            instance = new Inferencer(features);
        }

        return instance;
    }

    /**
     * Method for convenience. Once the instance of this Singleton has been instantiated, instead of calling long
     * getInferencer(String[] features) method, you can just call this one.
     * @return
     */
    public static Inferencer getInferencer() {
        if (instance == null) {
            throw new RuntimeException("You must first call getInferencer(String[] features) method!");
        }

        return instance;
    }

    private Inferencer(String[] features) {
        ldaModel = new LdaModel(features);
        inputGenerator = new LDAInputGenerator(features);
        this.features = Arrays.asList(features);
    }

    private LdaModel ldaModel;
    private LDAInputGenerator inputGenerator;
    private PipelineTask findHypernymsTask;
    private PipelineTask findTypesTask;
    private PipelineTask findCategoriesTask;
    private List<String> features;

    private Map<Integer, List<String>> wordsForTopics;
    private Map<Integer, List<Double>> wordCoveragesForTopics;
    private List<String> topicLabels;

    public void loadFile(String file) throws IOException, ClassNotFoundException {
        System.out.println("Loading topic model ("+file+")...");
        ldaModel.readFromFile(file);
        System.out.println("Loaded.");
        wordsForTopics = Cache.getWordsForTopics(ldaModel);
        wordCoveragesForTopics = Cache.getWordCoveragesForTopics(ldaModel);
        topicLabels = Cache.getTopicLabels(ldaModel);
    }

    public void createInMemoryTasks(String typesFile, String categoriesFile, String hypernymsFile) {
        findTypesTask = new FindTypesInMemoryTask(typesFile);
        findCategoriesTask = new FindCategoriesInMemoryTask(categoriesFile);
        findHypernymsTask = new FindHypernymsInMemoryTask(hypernymsFile);
    }

    public double[] predictTopicCoverage(String spotlightAnnotation) {
        InputToDataset reader = new InputToDataset(spotlightAnnotation);
        EmptyFinisher finisher = new EmptyFinisher();
        Pipeline pipeline = new Pipeline(reader, finisher);
        if (features.contains("w")) {
            pipeline.addTask(new FindLemmasTask());
        }
        if (features.contains("t")) {
            pipeline.addTask(findTypesTask);
        }
        if (features.contains("c")) {
            pipeline.addTask(findCategoriesTask);
        }
        if (features.contains("h")) {
            pipeline.addTask(findHypernymsTask);
        }
        pipeline.doWork();
        String featureVec = inputGenerator.generateFeatureVector(finisher.getProcessedInstance());
        double[] prediction = ldaModel.predict(featureVec);
        return prediction;
    }

    public List<String> getWordsForTopic(int topicId) {
        return wordsForTopics.get(topicId);
    }

    public List<Double> getWordCoveragesForTopic(int topicId) {
        return wordCoveragesForTopics.get(topicId);
    }

    public String getLabel(int topicId) {
        return topicLabels.get(topicId);
    }
}
