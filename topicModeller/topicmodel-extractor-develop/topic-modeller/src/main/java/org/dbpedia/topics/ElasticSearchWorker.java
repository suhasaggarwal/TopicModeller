package org.dbpedia.topics;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.utils.annotation.models.SpotlightResource;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by wojlukas on 4/18/16.
 */
public class ElasticSearchWorker {
    public static void main(String[] args) throws Exception {
        String dir = "abstracts-serialized";
        ElasticSearchWorker worker = new ElasticSearchWorker();

        Files.walk(Paths.get(dir)).filter(Files::isRegularFile).forEach(path -> {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                List<Instance> docs = (List<Instance>) ois.readObject();
                worker.insertInstances(docs, "abstracts");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private Client client;
    private double similarityScoreModifier = 0.99999999d;
    private float perc2ndRankModifier = 1e-10f;

    public ElasticSearchWorker() throws UnknownHostException {
        this("localhost", 9300);
    }

    public ElasticSearchWorker(String host, int port) throws UnknownHostException {
        Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch").build();

        client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
    }

    public void insertInstances(List<? extends Instance> documents, String elasticSearchType) throws UnknownHostException {
        for (Instance document : documents) {
            List<SpotlightResource> spotlightResources = document.getSpotlightAnnotation().getResources();
            for (int j = 0; j < spotlightResources.size(); j++) {
                SpotlightResource resource = spotlightResources.get(j);
                resource.setSimilarityScore(resource.getSimilarityScore() * similarityScoreModifier);
                resource.setPercentageOfSecondRank(resource.getPercentageOfSecondRank() + perc2ndRankModifier);
                spotlightResources.set(j, resource);
            }
            document.getSpotlightAnnotation().setResources(spotlightResources);

            JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(document);
            IndexRequest indexRequest = new IndexRequest("gsoc", elasticSearchType, document.getUri());
            indexRequest.source(jsonObject.toString());
            IndexResponse response = client.index(indexRequest).actionGet();
        }
    }

    public void close() {
        client.close();
    }
}
