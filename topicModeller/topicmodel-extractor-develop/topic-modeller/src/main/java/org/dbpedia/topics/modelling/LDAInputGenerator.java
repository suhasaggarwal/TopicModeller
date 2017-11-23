package org.dbpedia.topics.modelling;

import org.dbpedia.topics.Constants;
import org.dbpedia.topics.dataset.models.Dataset;
import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.utils.annotation.models.SpotlightResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by wlu on 15.06.16.
 */
public class LDAInputGenerator {
    private List<String> features;

    public LDAInputGenerator(String... _features) {
        features = Arrays.asList(_features);
    }

    private String generateForOneFeature(Instance document, String feature) {
        List<String> featureVector = new ArrayList<>();

        if (feature.equals(Constants.FEATURE_DESCRIPTOR_WORDS)) {
            featureVector.addAll(document.getLemmas());
        }
        else if (feature.equals(Constants.FEATURE_DESCRIPTOR_ENTITIES)) {
            List<String> entities = document.getSpotlightAnnotation().getResources()
                .parallelStream()
                .map(r -> Constants.FEATURE_PREFIX_ENTITIES + r.getUri())
                .collect(Collectors.toList());
            featureVector.addAll(entities);
        }
        else if (feature.equals(Constants.FEATURE_DESCRIPTOR_TYPES)) {
            Stream<SpotlightResource> s = document.getSpotlightAnnotation().getResources().stream();
            s.forEach(resource -> {
                List<String> types = resource.getRdfTypes()
                    .stream()
                    .filter(rdfType -> rdfType.contains("dbpedia.org") && !rdfType.contains("yago"))
                    .map(rdfType -> Constants.FEATURE_PREFIX_TYPES + rdfType)
                    .collect(Collectors.toList());
                featureVector.addAll(types);
            });
        }
        else if (feature.equals(Constants.FEATURE_DESCRIPTOR_CATEGORIES)) {
            Stream<SpotlightResource> s = document.getSpotlightAnnotation().getResources().stream();
            s.forEach(resource -> {
                List<String> categories = new ArrayList<>();
                categories.addAll(
                    resource.getDctSubjects()
                        .stream()
                        .map(dcSubj -> Constants.FEATURE_PREFIX_CATEGORIES + dcSubj)
                        .collect(Collectors.toList())
                );
                featureVector.addAll(categories);
            });
        }
        else if (feature.equals(Constants.FEATURE_DESCRIPTOR_HYPERNYMS)) {
            featureVector.addAll(
                document.getHypernyms()
                    .parallelStream()
                    .map(h -> Constants.FEATURE_PREFIX_HYPERNYMS + h)
                    .collect(Collectors.toList())
            );
        }

        String featureString = featureVector.stream().collect(Collectors.joining(" "));
        return featureString;
    }

    public String generateFeatureVector(Instance document) {
        List<String> featureVector = new ArrayList<>();

        features.forEach(feature -> featureVector.add(generateForOneFeature(document, feature)));

        String featureString = featureVector.stream().collect(Collectors.joining(" "));
        return featureString;
    }

    public List<String> generateFeatureMatrix (Dataset dataset) {
        List<String> featureMatrix = new ArrayList<>();

        for (Instance instance : dataset) {
            featureMatrix.add(generateFeatureVector(instance));
        }

        return featureMatrix;
//        int size = dataset.size();
//
//        List<List<String>> temp = new ArrayList<>();
//        for (int i = 0; i < size; i++) {
//            temp.add(new ArrayList<>());
//        }
//
//        //go through passed features
//        for (String feature : features) {
//            //go through every document
//            for (int i = 0; i < size; i++) {
//                List<String> current = temp.get(i);
//                String words = generateForOneFeature(dataset.getDocument(i), feature);
//                current.add(words);
//                temp.set(i, current);
//            }
//        }
//
//        List<String> featureMatrix = temp.stream()
//                .map(list -> list.stream().collect(Collectors.joining(" ")))
//                .collect(Collectors.toList());
//        return featureMatrix;
    }
}
