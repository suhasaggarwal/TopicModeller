package org.dbpedia.topics.inference;

import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import org.dbpedia.topics.modelling.LdaModel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wlu on 20.07.16.
 */
public class Cache {
    public static Map<Integer, List<String>> getWordsForTopics(LdaModel ldaModel) throws IOException, ClassNotFoundException {
        File modelDir = getModelDirectory(ldaModel);
        prepareCache(ldaModel, modelDir);

        Map<Integer, List<String>> wordsForTopics;
        File wordsCacheFile = new File(modelDir, Config.CACHE_FILE_WORDS_FOR_TOPICS);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(wordsCacheFile))){
            wordsForTopics = (Map<Integer, List<String>>) ois.readObject();
        }
        return wordsForTopics;
    }

    public static Map<Integer, List<Double>> getWordCoveragesForTopics(LdaModel ldaModel) throws IOException, ClassNotFoundException {
        File modelDir = getModelDirectory(ldaModel);
        prepareCache(ldaModel, modelDir);

        Map<Integer, List<Double>> wordCoveragesForTopics;
        File wordCoveragesCacheFile = new File(modelDir, Config.CACHE_FILE_WORD_COVERAGES_FOR_TOPICS);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(wordCoveragesCacheFile))){
            wordCoveragesForTopics = (Map<Integer, List<Double>>) ois.readObject();
        }
        return wordCoveragesForTopics;
    }

    public static List<String> getTopicLabels(LdaModel ldaModel) throws IOException {
        List<String> result;
        File modelDir = getModelDirectory(ldaModel);
        File labelsFile = new File(modelDir, Config.CACHE_FILE_TOPICS_LABELS);
        System.out.println("Checking if file with topic labels ("+labelsFile.getAbsolutePath()+") is present.");
        if (labelsFile.exists()){
            System.out.println("Present.");
            result = Files.lines(Paths.get(labelsFile.getAbsolutePath())).collect(Collectors.toList());
        }
        else {
            System.out.println("Not there, topics will be autolabelled.");
            result = new ArrayList<>(ldaModel.getModel().getNumTopics());
            for (int i = 0; i < ldaModel.getModel().getNumTopics(); i++) {
                result.add("");
            }
        }
        return result;
    }

    private static File getModelDirectory(LdaModel ldaModel) {
        String modelName = ldaModel.getModel().getNumTopics()+"-"+
                ldaModel.getFeatures().stream().collect(Collectors.joining("-"));
        File modelDir = new File(Config.CACHE_DIR, modelName);
        modelDir.mkdirs();
        return modelDir;
    }

    private static void prepareCache(LdaModel ldaModel, File modelDir){
        File wordsCacheFile = new File(modelDir, Config.CACHE_FILE_WORDS_FOR_TOPICS);
        File wordCoveragesCacheFile = new File(modelDir, Config.CACHE_FILE_WORD_COVERAGES_FOR_TOPICS);

        System.out.println("Checking if word cache present (" + wordsCacheFile.getAbsolutePath()+")...");
        if (!wordsCacheFile.exists()) {
            System.out.println("Not present. Preparing...");
            Map<Integer, List<String>> wordsForTopics = new HashMap<>();

            for (int i = 0; i < ldaModel.getModel().getNumTopics(); i++) {
                wordsForTopics.put(i, cacheWordsForTopic(ldaModel, i, Config.NUM_TOPIC_WORDS));
            }

            try {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(wordsCacheFile))){
                    oos.writeObject(wordsForTopics);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Cache present!");
        }

        System.out.println("Checking if word coverages cache present (" + wordCoveragesCacheFile.getAbsolutePath()+")...");
        if (!wordCoveragesCacheFile.exists()) {
            System.out.println("Not present. Preparing...");
            Map<Integer, List<Double>> wordCoveragesForTopics = new HashMap<>();

            for (int i = 0; i < ldaModel.getModel().getNumTopics(); i++) {
                wordCoveragesForTopics.put(i, cacheWordCoveragesForTopic(ldaModel, i, Config.NUM_TOPIC_WORDS));
            }

            try {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(wordCoveragesCacheFile))){
                    oos.writeObject(wordCoveragesForTopics);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Cache present!");
        }
    }

    private static List<String> cacheWordsForTopic(LdaModel ldaModel, int topicId, int topN) {
        List<String> result = new ArrayList<>();

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = ldaModel.getModel().getAlphabet();

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = ldaModel.getModel().getSortedWords();

        Iterator<IDSorter> iterator = topicSortedWords.get(topicId).iterator();

        int rank = 0;
        while (iterator.hasNext() && rank < topN) {
            IDSorter idCountPair = iterator.next();
            result.add((String) dataAlphabet.lookupObject(idCountPair.getID()));
            rank++;
        }

        return result;
    }

    private static List<Double> cacheWordCoveragesForTopic(LdaModel ldaModel, int topicId, int topN) {
        List<Double> result = new ArrayList<>();

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = ldaModel.getModel().getSortedWords();

        Iterator<IDSorter> iterator = topicSortedWords.get(topicId).iterator();

        double sumWeights = topicSortedWords.get(topicId).stream().mapToDouble(ids -> ids.getWeight()).sum();

        int rank = 0;
        while (iterator.hasNext() && rank < topN) {
            IDSorter idCountPair = iterator.next();
            result.add(idCountPair.getWeight()*100/sumWeights);
            rank++;
        }

        return result;
    }
}
