package org.dbpedia.utils.annotation;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.dbpedia.utils.annotation.models.SpotlightAnnotation;
import org.dbpedia.utils.annotation.models.SpotlightResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Suhas Aggarwal
 */
public class SpotlightAnnotator {

    private String endpointUrl;

    public SpotlightAnnotator(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public SpotlightAnnotation annotate(String text, int supportThreshold, double confidenceThreshold){
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(endpointUrl);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("text", text));
        nameValuePairs.add(new BasicNameValuePair("support", String.valueOf(supportThreshold)));
        nameValuePairs.add(new BasicNameValuePair("confidence", String.valueOf(confidenceThreshold)));

        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");

        SpotlightAnnotation result = new SpotlightAnnotation();
        HttpResponse response;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            //TODO: log the exception
            return result;
        }

        String jsonText;
        try {
            jsonText = IOUtils.toString(response.getEntity().getContent(), "UTF-8").trim();
        } catch (IOException e) {
            //TODO: log the exception
            return result;
        }

        JSONObject json;
        try {
            json = (JSONObject) JSONSerializer.toJSON(jsonText);
        }
        catch (JSONException e) {
            //TODO: add logging
            throw e;
        }

        try {
            result = parseJsonResponseObject(json);
        }
        catch (JSONException e) {
            //TODO: add logging
            throw e;
        }

        return result;
    }

    public SpotlightAnnotation parseJsonResponseObject(JSONObject response) {
        SpotlightAnnotation result = new SpotlightAnnotation();

        result.setText(response.getString("@text"));
        result.setConfidence(Double.parseDouble(response.getString("@confidence")));
        result.setSupport(Integer.parseInt(response.getString("@support")));
        result.setTypes(response.getString("@types"));
        result.setSparql(response.getString("@sparql"));
        result.setPolicy(response.getString("@policy"));

        if (response.containsKey("Resources")) {
            JSONArray resourcesArray = response.getJSONArray("Resources");
            Iterator i = resourcesArray.iterator();
            while (i.hasNext()) {
                JSONObject jsonResource = (JSONObject) i.next();

                SpotlightResource resource = new SpotlightResource();

                resource.setUri(jsonResource.getString("@URI"));
                resource.setSupport(Integer.parseInt(jsonResource.getString("@support")));
                resource.setTypesString(jsonResource.getString("@types"));
                resource.setSurfaceForm(jsonResource.getString("@surfaceForm"));
                resource.setOffset(Integer.parseInt(jsonResource.getString("@offset")));
                resource.setSimilarityScore(Double.parseDouble(jsonResource.getString("@similarityScore")));
                resource.setPercentageOfSecondRank(Float.parseFloat(jsonResource.getString("@percentageOfSecondRank")));

                result.addResource(resource);
            }
        }

        return result;
    }
}
