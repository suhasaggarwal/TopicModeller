package org.dbpedia.utils.annotation.models;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suhas Aggarwal
 */
@Embedded
public class SpotlightAnnotation implements Serializable {
    @Property
    private String text;
    @Property
    private double confidence;
    @Property
    private int support;
    @Property
    private String types;
    @Property
    private String sparql;
    @Property
    private String policy;
    private List<SpotlightResource> resources = new ArrayList<>();

    public SpotlightAnnotation() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getSparql() {
        return sparql;
    }

    public void setSparql(String sparql) {
        this.sparql = sparql;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public List<SpotlightResource> getResources() {
        return resources;
    }

    public void setResources(List<SpotlightResource> resources) {
        this.resources = resources;
    }

    public void addResource(SpotlightResource resource) {
        this.resources.add(resource);
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }
}
