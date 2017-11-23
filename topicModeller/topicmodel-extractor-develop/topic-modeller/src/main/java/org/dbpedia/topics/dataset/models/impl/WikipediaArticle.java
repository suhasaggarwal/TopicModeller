package org.dbpedia.topics.dataset.models.impl;

import org.dbpedia.topics.dataset.models.Instance;
import org.mongodb.morphia.annotations.Entity;

/**
 * Created by wlu on 31.05.16.
 */
@Entity(value = "articles", noClassnameStored = true)
public class WikipediaArticle extends Instance {
    public WikipediaArticle() {
    }
}
