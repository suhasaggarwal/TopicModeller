package org.dbpedia.topics;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by wlu on 09.06.16.
 */
public class Config {
    public static String SPOTLIGHT_ENDPOINT;
    public static String DBPEDIA_ENDPOINT;
    public static String HYPERNYMS_ENDPOINT;
    public static String HYPERNYMS_TRIPLE_FILE;
    public static String TYPES_TRIPLE_FILE;
    public static String CATEGORIES_TRIPLE_FILE;
    public static String ABSTRACTS_TRIPLE_FILE;
    public static String WIKI_AS_XML_FOLDER;
    public static String MONGO_SERVER;
    public static int MONGO_PORT;
    public static String MONGO_DB;
    public static String ELASTIC_SERVER;
    public static int ELASTIC_PORT;
    public static String ELASTIC_INDEX;
    public static int LDA_NUM_THREADS;
    public static int LDA_NUM_ITERATIONS;
    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("props.properties"));
            SPOTLIGHT_ENDPOINT = properties.getProperty("spotlight_endpoint", "http://spotlight.sztaki.hu:2222/rest/annotate");
            DBPEDIA_ENDPOINT = properties.getProperty("dbpedia_sparql_endpoint", "http://dbpedia.org/sparql");
            HYPERNYMS_ENDPOINT = properties.getProperty("hypernyms_sparql_endpoint");
            MONGO_SERVER = properties.getProperty("mongo_server", "localhost");
            MONGO_PORT = Integer.parseInt(properties.getProperty("mongo_port", "27017"));
            MONGO_DB = properties.getProperty("mongo_dbname", "gsoc");
            HYPERNYMS_TRIPLE_FILE = properties.getProperty("hypernyms_triple_file");
            TYPES_TRIPLE_FILE = properties.getProperty("types_triple_file");
            CATEGORIES_TRIPLE_FILE = properties.getProperty("categories_triple_file");
            ABSTRACTS_TRIPLE_FILE = properties.getProperty("abstracts_triple_file");
            WIKI_AS_XML_FOLDER = properties.getProperty("wiki_as_xml_folder");
            ELASTIC_SERVER = properties.getProperty("elastic_server", "localhost");
            ELASTIC_PORT = Integer.parseInt(properties.getProperty("elastic_port", "9300"));
            ELASTIC_INDEX = properties.getProperty("elastic_index", "gsoc");
            LDA_NUM_THREADS = Integer.parseInt(properties.getProperty("lda_num_threads", "16"));
            LDA_NUM_ITERATIONS = Integer.parseInt(properties.getProperty("lda_num_iterations", "1000"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
