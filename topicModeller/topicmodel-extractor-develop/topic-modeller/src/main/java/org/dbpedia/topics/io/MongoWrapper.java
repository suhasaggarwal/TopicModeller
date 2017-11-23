package org.dbpedia.topics.io;

import com.mongodb.MongoClient;
import org.dbpedia.topics.Config;
import org.dbpedia.topics.dataset.models.impl.DBpediaAbstract;
import org.dbpedia.topics.dataset.models.impl.WikipediaArticle;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.MorphiaIterator;
import org.mongodb.morphia.query.Query;

import java.util.List;

/**
 * Created by wlu on 14.06.16.
 */
public class MongoWrapper {
    private MongoClient mongoClient;
    private Morphia morphia;
    private Datastore datastore;

    public MongoWrapper(String server, int port) {
        mongoClient = new MongoClient(server, port);
        morphia = new Morphia();

        morphia.map(DBpediaAbstract.class);
        morphia.map(WikipediaArticle.class);

        datastore = morphia.createDatastore(mongoClient, Config.MONGO_DB);
        datastore.ensureIndexes();
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public void close() {
        this.mongoClient.close();
    }

    public <T> List<T> getAllRecords(Class<T> clazz) {
        List<T> result = datastore.createQuery(clazz).asList();
        return result;
    }

    public <T> MorphiaIterator<T, T> getAllRecordsIterator(Class<T> clazz) {
        MorphiaIterator iterator = getAllRecordsQuery(clazz).fetch();
        return iterator;
    }

    public <T> MorphiaIterator<T, T> getAllRecordsIterator(Class<T> clazz, int limit) {
        MorphiaIterator iterator = getAllRecordsQuery(clazz).limit(limit).fetch();
        return iterator;
    }

    private <T> Query<T> getAllRecordsQuery(Class<T> clazz) {
        Query<T> query = datastore.find(clazz).field("spotlightAnnotation").exists();
        return query;
    }

    public <T> boolean recordExists(Class<T> clazz, String uri) {
        List<T> result = datastore.createQuery(clazz).field("uri").equal(uri).retrievedFields(true, "uri").asList();
        if (result.size() > 1) {
            throw new RuntimeException("Duplicate entry in the database for uri " + uri);
        }

        return result.size() == 1;
    }
}
