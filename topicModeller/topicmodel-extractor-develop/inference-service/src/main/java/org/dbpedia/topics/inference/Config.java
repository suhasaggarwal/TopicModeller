package org.dbpedia.topics.inference;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by wojlukas on 7/15/16.
 */
public class Config {
    public static String SERVER_BASE_URI;
    public static Integer NUM_TOPIC_WORDS;
    public static String HYPERNYMS_TRIPLE_FILE;
    public static String TYPES_TRIPLE_FILE;
    public static String CATEGORIES_TRIPLE_FILE;
    public static String CACHE_DIR;
    public static String CACHE_FILE_WORDS_FOR_TOPICS;
    public static String CACHE_FILE_WORD_COVERAGES_FOR_TOPICS;
    public static String CACHE_FILE_TOPICS_LABELS;
    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("inferencer.properties"));
            SERVER_BASE_URI = properties.getProperty("server_base_uri", "http://0.0.0.0:8182/inference-service/");
            NUM_TOPIC_WORDS = Integer.valueOf(properties.getProperty("num_topic_words", "15"));
            HYPERNYMS_TRIPLE_FILE = properties.getProperty("hypernyms_triple_file");
            TYPES_TRIPLE_FILE = properties.getProperty("types_triple_file");
            CATEGORIES_TRIPLE_FILE = properties.getProperty("categories_triple_file");
            CACHE_DIR = properties.getProperty("cache_directory", "inferencer-cache");
            CACHE_FILE_WORDS_FOR_TOPICS = properties.getProperty("cache_file_words_for_topics", "wordsForTopics.ser");
            CACHE_FILE_WORD_COVERAGES_FOR_TOPICS = properties.getProperty("cache_file_word_coverages_for_topics",
                                                                                        "wordCoveragesForTopics.ser");
            CACHE_FILE_TOPICS_LABELS = properties.getProperty("cache_file_topics_labels", "topicsLabels.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
