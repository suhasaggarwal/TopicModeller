package org.dbpedia.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Suhas Aggarwal
 */
public class SparqlConnector {

    private URI endpointUri;

    public SparqlConnector(String endpointUri) throws URISyntaxException {
        this.endpointUri = new URI(endpointUri);
    }

    public URI getEndpointUri() {
        return endpointUri;
    }

    public void setEndpointUri(URI endpointUri) {
        this.endpointUri = endpointUri;
    }

    public List<String> getTypes(String resourceUri) {
        return selectObject(resourceUri, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    }

    public List<String> getCategories(String resourceUri) {
        return selectObject(resourceUri, "http://purl.org/dc/terms/subject");
    }

    public List<String> getHypernyms(String resourceUri) {
        return selectObject(resourceUri, "http://purl.org/linguistics/gold/hypernym", "hypernyms");
    }

    private List<String> selectObject(String s, String p) {
        return selectObject(s, p, Constants.DEFAULT_GRAPH_URI);
    }

    private List<String> selectObject(String s, String p, String graphUri) {
        String varName = "tempVar";
        List<String> result = new ArrayList<>();

        List<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("default-graph-uri", graphUri));
        String queryString = String.format("select distinct ?%s where {<%s> <%s> ?%s}", varName, s, p, varName);
        nameValuePairs.add(new BasicNameValuePair("query", queryString));
        nameValuePairs.add(new BasicNameValuePair("format", "application/sparql-results+json"));
        nameValuePairs.add(new BasicNameValuePair("timeout", "30000"));


        HttpClient client = HttpClientBuilder.create().build();
        URIBuilder uriBuilder = new URIBuilder(endpointUri);
        uriBuilder.setParameters(nameValuePairs);


        HttpGet get;
        try {
            get = new HttpGet(uriBuilder.build());
        } catch (URISyntaxException e) {
            //should never happen
            throw new RuntimeException(e);
        }

        get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonText = null;
        try {
            jsonText = IOUtils.toString(response.getEntity().getContent(), "UTF-8").trim();
        } catch (IOException e) {
            //TODO: log the exception
            return result;
        }

        JSONObject json;
        try
        {
            json = (JSONObject) JSONSerializer.toJSON(jsonText);
        }
        catch (JSONException e) {
            //TODO: log the exception with the text
            e.printStackTrace();
            throw e;
        }

        try {
            JSONArray conceptsArray = json.getJSONObject("results").getJSONArray("bindings");

            Iterator i = conceptsArray.iterator();
            while (i.hasNext()) {
                JSONObject jsonResource = (JSONObject) i.next();

                result.add(jsonResource.getJSONObject(varName).getString("value"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            throw e;
        }

        return result;
    }
}
