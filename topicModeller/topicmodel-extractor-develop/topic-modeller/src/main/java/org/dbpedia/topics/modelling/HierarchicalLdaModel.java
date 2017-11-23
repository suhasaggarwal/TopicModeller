package org.dbpedia.topics.modelling;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.topics.HierarchicalLDA;
import cc.mallet.topics.HierarchicalLdaUtils;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Randoms;
import org.dbpedia.topics.Config;
import org.dbpedia.topics.Constants;
import org.dbpedia.topics.io.StopWords;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by wlu on 25.07.16.
 */
public class HierarchicalLdaModel {

    /**
     * defines if
     * words (words)
     * entities (entities)
     * rdf-types (types)
     * dcTerm-subjects (categories)
     * hypernyms (hypernyms)
     * should be used as input for LDA.
     * See Constants.FEATURE_DESCRIPTOR_* for allowed values.
     */
    private List<String> features;

    private HierarchicalLDA hierarchicalLDAModel;
    private List<Pipe> pipeList = new ArrayList<>();

    private int numWords = 15;

    private String[] blacklistEntities = new String[]{
    };

    private String[] blacklistTypes = new String[]{
            Constants.FEATURE_PREFIX_TYPES + "http://dbpedia.org/ontology/Agent",
            Constants.FEATURE_PREFIX_TYPES + "http://dbpedia.org/ontology/Place",
            Constants.FEATURE_PREFIX_TYPES + "http://dbpedia.org/ontology/Location",
            Constants.FEATURE_PREFIX_TYPES + "http://dbpedia.org/ontology/PopulatedPlace"
    };

    private String[] blacklistCategories = new String[]{
            Constants.FEATURE_PREFIX_CATEGORIES + "http://dbpedia.org/resource/Category:Living_people"
    };

    private String[] blacklistHypernyms = new String[]{
    };

    public HierarchicalLdaModel(String... topicModes) {
        this.features = Arrays.asList(topicModes).stream().filter(t -> t!=null).collect(Collectors.toList());

        TokenSequenceRemoveStopwords tsrs = new TokenSequenceRemoveStopwords();
        tsrs.setCaseSensitive(true);

        if (this.features.contains(Constants.FEATURE_DESCRIPTOR_WORDS)) {
            tsrs.addStopWords(StopWords.STOPWORDS);
        }
        if (this.features.contains(Constants.FEATURE_DESCRIPTOR_ENTITIES)) {
            tsrs.addStopWords(blacklistEntities);
        }
        if (this.features.contains(Constants.FEATURE_DESCRIPTOR_TYPES)) {
            tsrs.addStopWords(blacklistTypes);
        }
        if (this.features.contains(Constants.FEATURE_DESCRIPTOR_CATEGORIES)) {
            tsrs.addStopWords(blacklistCategories);
        }
        if (this.features.contains(Constants.FEATURE_DESCRIPTOR_HYPERNYMS)) {
            tsrs.addStopWords(blacklistHypernyms);
        }

        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\d\\p{L}\\p{P}]+[\\p{L}\\d\\p{P}]")));

        pipeList.add(tsrs);

        pipeList.add(new TokenSequence2FeatureSequence());
    }

    public double[] predict(String text) {
        InstanceList testing = new InstanceList(new SerialPipes(pipeList));
        testing.addThruPipe(new Instance(text, null, "test instance", null));
        return new double[]{};
    }

    public void createModel(List<String> input, int numIterations, int numLevels) throws IOException {
        InstanceList trainInstances = new InstanceList (new SerialPipes(pipeList));
        trainInstances.addThruPipe(new ArrayIterator(input));
        InstanceList testInstances = null;

        hierarchicalLDAModel = new HierarchicalLDA();
        hierarchicalLDAModel.setTopicDisplay(50, numWords);
        hierarchicalLDAModel.setProgressDisplay(true);
        hierarchicalLDAModel.initialize(trainInstances, testInstances, numLevels, new Randoms());
        hierarchicalLDAModel.estimate(numIterations);
    }

    public void saveToFile(String outputfile) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputfile))){
            oos.writeObject(hierarchicalLDAModel);
        }
    }

    public void readFromFile(String inputfile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputfile))){
            this.hierarchicalLDAModel = (HierarchicalLDA) ois.readObject();
        }
    }

    public void describeTopicModel(String outputFilename) throws IOException {
        HierarchicalLdaUtils utils = new HierarchicalLdaUtils(hierarchicalLDAModel);
        String hierarchyDescription = utils.nodesAsString();
        Files.write(Paths.get(outputFilename), hierarchyDescription.getBytes());
    }

    public int getNumWords() {
        return numWords;
    }

    public void setNumWords(int numWords) {
        this.numWords = numWords;
    }
}
