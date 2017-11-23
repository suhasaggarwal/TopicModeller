package org.dbpedia.topics.dataset.readers.impl;

import org.dbpedia.topics.dataset.models.Dataset;
import org.dbpedia.topics.dataset.models.impl.DBpediaAbstract;
import org.dbpedia.topics.dataset.models.impl.DBpediaAbstractsDataset;
import org.dbpedia.topics.dataset.readers.Reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wlu on 29.05.16.
 */
public class DBpediaAbstractsReader extends Reader {

    private static final String PARSE_TRIPLE_REGEX = "<(.*?)>\\s*<http://dbpedia.org/ontology/abstract>\\s*\"(.*?)\"@en.*\\.";

    private String abstractsTtlFile;
    private long start;
    private long end;

    /**
     * Constructs a Reader that reads a file with DBpedia abstracts in a turtle format.
     * @param abstractsTtlFile
     */
    public DBpediaAbstractsReader(String abstractsTtlFile) {
        this(abstractsTtlFile, 0, Long.MAX_VALUE);
    }

    /**
     * Constructs a Reader that reads specified number of abstracts from a file with DBpedia abstracts in a turtle format.
     * @param abstractsTtlFile
     * @param end
     */
    public DBpediaAbstractsReader(String abstractsTtlFile, long end) {
        this(abstractsTtlFile, 0, end);
    }

    /**
     * Constructs a Reader that reads specified number of abstracts from a file with DBpedia abstracts in a turtle format.
     * @param abstractsTtlFile
     * @param start
     * @param end
     */
    public DBpediaAbstractsReader(String abstractsTtlFile, long start, long end) {
        this.abstractsTtlFile = abstractsTtlFile;
        this.start = start;
        this.end = end;
    }

    /*
        Tested two methods for reading the dataset:
            - using Apache Jena
            - using regular expressions
        Regex method is faster, approx. 108149ms vs. 158681ms.
     */
    @Override
    public Dataset readDataset() {
        Dataset dataset = new DBpediaAbstractsDataset();
        try {
            Pattern pattern = Pattern.compile(PARSE_TRIPLE_REGEX);
            Files.lines(Paths.get(abstractsTtlFile))
                    .filter(line -> !line.startsWith("#"))
                    .skip(start)
                    .limit(end-start)
                    .forEach(line -> {
                        Matcher m = pattern.matcher(line);
                        if (m.find()) {
                            String uri = m.group(1);
                            String text = m.group(2);
                            DBpediaAbstract document = new DBpediaAbstract();
                            document.setUri(uri);
                            document.setText(text);
                            dataset.addDocument(document);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }
}
