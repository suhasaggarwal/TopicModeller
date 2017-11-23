package org.dbpedia.topics.modelling;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import org.dbpedia.topics.Constants;
import org.dbpedia.topics.io.StopWords;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by wlu on 26.05.16.
 */
public class LdaModel {
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
    private ParallelTopicModel model;
    private ArrayList<Pipe> pipeList = new ArrayList<>();

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

    public LdaModel(String... topicModes) {
        this.features = Arrays.asList(topicModes).stream().filter(t -> t!=null).collect(Collectors.toList());

        // Pipes: lowercase, remove stopwords, map to features

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

    public void createModel(List<String> input, int numTopics, int numIterations, int numThreads) throws IOException {
        InstanceList instances = new InstanceList (new SerialPipes(pipeList));
        instances.addThruPipe(new ArrayIterator(input));

        this.model = new ParallelTopicModel(numTopics, 1.0, 0.01);
        model.addInstances(instances);
        model.setNumThreads(numThreads);
        model.setNumIterations(numIterations);
        model.estimate();
    }

    public void saveToFile(String outputfile) throws IOException{
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputfile))){
            oos.writeObject(model);
        }
    }

    public void readFromFile(String inputfile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputfile))){
            this.model = (ParallelTopicModel) ois.readObject();
        }
    }

    public ParallelTopicModel getModel() {
        return model;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void describeTopicModel(String outputFilename, int numTopicDescribingWords) throws IOException {
        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = model.getAlphabet();

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputFilename))){
            // Show top words in topics with proportions for the first document
            for (int topic = 0; topic < model.getNumTopics(); topic++) {
                Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

                int rank = 0;
                List<String> topicWords = new ArrayList<>();
                List<Double> topicWordsWeights = new ArrayList<>();

                while (iterator.hasNext() && rank < numTopicDescribingWords) {
                    IDSorter idCountPair = iterator.next();
                    topicWords.add((String) dataAlphabet.lookupObject(idCountPair.getID()));
                    topicWordsWeights.add(idCountPair.getWeight());
                    rank++;
                }

                double sumWeights = model.getSortedWords().get(topic).stream().mapToDouble(ids -> ids.getWeight()).sum();

                bw.write(topicWords.stream().map(f -> "\"" + f + "\"").collect(Collectors.joining(",")));
                bw.newLine();
                bw.write(topicWordsWeights.stream().map(wt -> String.valueOf(wt/sumWeights)).collect(Collectors.joining(",")));
                bw.newLine();
            }
        }
    }

    public double[] predict(String text) {
        InstanceList instances = new InstanceList(new SerialPipes(pipeList));
        instances.addThruPipe(new Instance(text, null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        double[] probabilities = inferencer.getSampledDistribution(instances.get(0), 10, 1, 5);
        return probabilities;
    }
}
