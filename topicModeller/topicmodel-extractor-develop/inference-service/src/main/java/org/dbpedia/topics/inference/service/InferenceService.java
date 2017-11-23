package org.dbpedia.topics.inference.service;

import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;
import org.dbpedia.topics.inference.Inferencer;
import org.dbpedia.topics.inference.service.models.Prediction;
import org.dbpedia.topics.inference.service.models.PredictionResponse;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Root resource (exposed at "get-topics" path)
 */
@Path("inference-service")
public class InferenceService {
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @POST
    @Path("get-topics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response predict(@FormParam("spotlightAnnotationJSON") String spotlightAnnotationJSON) {
        System.out.println("GOT MESSAGE " + spotlightAnnotationJSON);
        PredictionResponse predictionResponse = new PredictionResponse();
        try {
            JSONSerializer.toJSON(spotlightAnnotationJSON);
            System.out.println("JSON WORKS");
        }
        catch (JSONException e) {
            predictionResponse.setStatus("Input must be a valid Spotlight annotation in JSON format!");
            return Response.serverError()
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .entity(JSONSerializer.toJSON(predictionResponse).toString())
                    .build();
        }

        Inferencer inferencer = Inferencer.getInferencer();
        double[] predictions = new double[0];
        try {
            predictions = inferencer.predictTopicCoverage(spotlightAnnotationJSON);
        }
        catch (Exception e) {
            System.err.println("Unable to predict topic coverage... " + e.getMessage());
            e.printStackTrace();
        }

        for (int i = 0; i < predictions.length; i++) {
            Prediction prediction = new Prediction();
            prediction.setTopicId(i+1);
            prediction.setTopicProbability(predictions[i]);
            prediction.setTopicWords(inferencer.getWordsForTopic(i));
            prediction.setTopicWordsCoverage(inferencer.getWordCoveragesForTopic(i));
            if (inferencer.getLabel(i).length() > 0) {
                prediction.setTopicLabel(inferencer.getLabel(i));
            }
            predictionResponse.addPrediction(prediction);
        }

        String predictionRespJson = JSONSerializer.toJSON(predictionResponse).toString();

        return Response.ok().header("Content-Type", MediaType.APPLICATION_JSON).entity(predictionRespJson).build();
    }
}
