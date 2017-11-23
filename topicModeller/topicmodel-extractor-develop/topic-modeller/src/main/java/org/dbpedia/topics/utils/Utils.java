package org.dbpedia.topics.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wlu on 15.06.16.
 */
public class Utils {
    public static long getNumberOfLines(String filename) {
        try {
            return Files.lines(Paths.get(filename)).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Map<String, List<String>> readSubjectObjectMappings(String parseRegex, String filename) {
        Map<String, List<String>> result = new HashMap<>();

        try {
            Pattern pattern = Pattern.compile(parseRegex);
            Files.lines(Paths.get(filename))
                    .filter(line -> !line.startsWith("#"))
                    .forEach(line -> {
                        Matcher m = pattern.matcher(line);
                        if (m.find()) {
                            String subject = m.group(1);
                            String object = m.group(2);
                            result.putIfAbsent(subject, new ArrayList<>());
                            result.get(subject).add(object);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List<String[]> createPowerSet(String... items) {
        List<String[]> result = new ArrayList<>();
        int numItems = items.length;

        for (int i = 1; i < Math.pow(2, numItems); i++) {
            String binaryRepresentation = String.format("%"+numItems+"s", Integer.toBinaryString(i));

            String[] elems = new String[numItems];
            for (int idx = 0; idx < numItems; idx++) {
                char currentVal = binaryRepresentation.charAt(idx);
                if (currentVal == '1') {
                    elems[idx] = items[idx];
                }
            }

            String[] notNullElems = Arrays.stream(elems)
                    .filter(elem -> elem != null)
                    .toArray(size -> new String[size]);

            result.add(notNullElems);
        }

        return result;
    }
}
