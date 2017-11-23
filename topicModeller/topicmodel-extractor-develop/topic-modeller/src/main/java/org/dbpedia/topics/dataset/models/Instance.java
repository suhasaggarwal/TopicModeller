package org.dbpedia.topics.dataset.models;

import org.bson.types.ObjectId;
import org.dbpedia.utils.annotation.models.SpotlightAnnotation;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the single document used for topic modelling.
 * Created by wlu on 26.05.16.
 */
public abstract class Instance implements Serializable {
    @Property
    protected String text;
    @Indexed(unique = true)
    protected String uri;
    @Id
    private ObjectId id;
    @Embedded
    protected SpotlightAnnotation spotlightAnnotation;
    @Property
    protected List<String> hypernyms = new ArrayList<>();
    @Property
    protected List<String> lemmas = new ArrayList<>();

    public Instance() {
    }

    /**
     * Returns the URI of this document.
     * @return
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the uri of this document.
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the text content of this document.
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text content of this document.
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the annotation of this document.
     * @return
     */
    public SpotlightAnnotation getSpotlightAnnotation() {
        return spotlightAnnotation;
    }

    /**
     * Sets the annotation of this document.
     * @param spotlightAnnotation
     */
    public void setSpotlightAnnotation(SpotlightAnnotation spotlightAnnotation) {
        this.spotlightAnnotation = spotlightAnnotation;
    }

    /**
     * Gets the hypernyms (objects with the property http://purl.org/linguistics/gold/hypernym) of the spotted entities
     * within this document.
     * @return
     */
    public List<String> getHypernyms() {
        return hypernyms;
    }

    /**
     * Sets the hypernyms (objects with the property http://purl.org/linguistics/gold/hypernym) of the spotted entities
     * within this document.
     * @param hypernyms
     */
    public void setHypernyms(List<String> hypernyms) {
        this.hypernyms = hypernyms;
    }

    /**
     * Adds a hypernym to the current list.
     * @param hypernym
     * @return as specified by List.add()
     */
    public boolean addHypernym(String hypernym) {
        return this.hypernyms.add(hypernym);
    }

    /**
     * Gets the lemmas of the text of this document.
     * @return
     */
    public List<String> getLemmas() {
        return lemmas;
    }

    /**
     * Sets the lemmas of the text of this document.
     * @return
     */
    public void setLemmas(List<String> lemmas) {
        this.lemmas = lemmas;
    }

    /**
     * Used to remove this property when reading instances from MongoDB and serializing them to disk as json.
     * Id field is redundant in this case.
     * @param id
     */
    public void setId(ObjectId id) {
        this.id = id;
    }
}
