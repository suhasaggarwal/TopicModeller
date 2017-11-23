package org.dbpedia.topics.dataset.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Representation of the document corpus that will be used for topic modelling.
 * Created by wlu on 26.05.16.
 */
public class Dataset implements Iterable<Instance> {

    private List<Instance> documents = new ArrayList<>();

    public List<Instance> getDocuments() {
        return documents;
    }

    public void addDocument(Instance document) {
        documents.add(document);
    }

    public Instance getDocument(int index) {
        return documents.get(index);
    }

    public int size() {
        return documents.size();
    }

    @Override
    public Iterator<Instance> iterator() {
        Iterator<Instance> it = new Iterator<Instance>() {

            private int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < documents.size();
            }

            @Override
            public Instance next() {
                return documents.get(idx++);
            }
        };

        return it;
    }
}
