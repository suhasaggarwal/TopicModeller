package org.dbpedia.topics.dataset.models.impl;

import org.dbpedia.topics.dataset.models.Instance;
import org.mongodb.morphia.annotations.Entity;

/**
 * Created by wlu on 26.05.16.
 */
@Entity(value = "abstracts", noClassnameStored = true)
public class DBpediaAbstract extends Instance {
    public DBpediaAbstract() {
    }
}
