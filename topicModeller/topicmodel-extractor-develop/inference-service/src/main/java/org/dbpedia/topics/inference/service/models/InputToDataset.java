package org.dbpedia.topics.inference.service.models;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.dbpedia.topics.dataset.models.Dataset;
import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.dataset.readers.Reader;
import org.dbpedia.utils.annotation.SpotlightAnnotator;
import org.dbpedia.utils.annotation.models.SpotlightAnnotation;

/**
 * Created by wlu on 19.07.16.
 */
public class InputToDataset extends Reader {
    private String spotlightAnnotationJSON;

    public InputToDataset(String spotlightAnnotationJSON) {
        this.spotlightAnnotationJSON = spotlightAnnotationJSON;
    }

    @Override
    public Dataset readDataset() {
        Dataset dataset = new Dataset();
        SpotlightAnnotation annotation = new SpotlightAnnotator("")
                .parseJsonResponseObject((JSONObject) JSONSerializer.toJSON(spotlightAnnotationJSON));
        Instance instance = new InferencerInput();
        instance.setText(annotation.getText());
        instance.setSpotlightAnnotation(annotation);
        dataset.addDocument(instance);
        return dataset;
    }
}
