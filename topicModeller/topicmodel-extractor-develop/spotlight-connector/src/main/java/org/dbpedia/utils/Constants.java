package org.dbpedia.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by wlu on 09.06.16.
 */
public class Constants {
    /**
     * Default graph URI used for SPARQL queries.
     */
    public static String DEFAULT_GRAPH_URI;
    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("props.properties"));
            DEFAULT_GRAPH_URI = properties.getProperty("default_graph_uri", "http://dbpedia.org");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
