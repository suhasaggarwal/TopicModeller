package org.dbpedia.topics.inference.service.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wlu on 19.07.16.
 */
public class Prediction implements Serializable {
    private int topicId;
    private double topicProbability;
    private String topicLabel;
    private List<String> topicWords;
    private List<Double> topicWordsCoverage;

    public Prediction() {
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public double getTopicProbability() {
        return topicProbability;
    }

    public void setTopicProbability(double topicProbability) {
        this.topicProbability = topicProbability;
    }

    public String getTopicLabel() {
        return topicLabel;
    }

    public void setTopicLabel(String topicLabel) {
        this.topicLabel = topicLabel;
    }

    public List<String> getTopicWords() {
        return topicWords;
    }

    public void setTopicWords(List<String> topicWords) {
        this.topicWords = topicWords;
    }

    public List<Double> getTopicWordsCoverage() {
        return topicWordsCoverage;
    }

    public void setTopicWordsCoverage(List<Double> topicWordsCoverage) {
        this.topicWordsCoverage = topicWordsCoverage;
    }
}
