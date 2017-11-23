package org.dbpedia.topics.pipeline.impl;

import com.mongodb.DuplicateKeyException;
import org.bson.BSONException;
import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.io.MongoWrapper;
import org.dbpedia.topics.pipeline.PipelineFinisher;
import org.mongodb.morphia.Datastore;

/**
 * Created by wlu on 09.06.16.
 */
public class MongoDBInsertFinisher extends PipelineFinisher {
    private MongoWrapper mongo;
    private Datastore datastore;
    private boolean dontSaveText;

    public MongoDBInsertFinisher(String server, int port) {
        this(server, port, false);
    }

    public MongoDBInsertFinisher(String server, int port, boolean dontSaveText) {
        mongo = new MongoWrapper(server, port);
        datastore = mongo.getDatastore();
        this.dontSaveText = dontSaveText;
    }

    @Override
    public void finishInstance(Instance instance) {
        if (instance.getSpotlightAnnotation() == null) {
            System.err.println("Document not annotated....");
            return;
        }

        if (instance.getSpotlightAnnotation().isEmpty()) {
            System.err.println("Annotation empty....");
            return;
        }

        if (dontSaveText){
            instance.setText(null);
            instance.getSpotlightAnnotation().setText(null);
        }

        try {
            datastore.save(instance);
        } catch (BSONException e) {
            System.out.println("BSONException while inserting: " + instance.getUri());
            e.printStackTrace();
        } catch (DuplicateKeyException e) {
            System.out.println("Duplicate entry: " + instance.getUri());
        }
    }

    @Override
    public void close() {
        mongo.close();
    }

    public boolean recordAlreadyExists(Instance instance) {
        return mongo.recordExists(instance.getClass(), instance.getUri());
    }
}
