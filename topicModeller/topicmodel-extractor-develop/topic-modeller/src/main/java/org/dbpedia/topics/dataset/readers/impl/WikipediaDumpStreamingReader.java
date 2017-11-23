package org.dbpedia.topics.dataset.readers.impl;

import org.dbpedia.topics.dataset.models.Instance;
import org.dbpedia.topics.dataset.models.impl.WikipediaArticle;
import org.dbpedia.topics.dataset.readers.StreamingReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by wlu on 29.05.16.
 */
public class WikipediaDumpStreamingReader extends StreamingReader {

    private String pathToWikiXmlFolder;

    public WikipediaDumpStreamingReader(String pathToWikiXmlFolder) {
        this.pathToWikiXmlFolder = pathToWikiXmlFolder;
    }

    @Override
    public Stream<Instance> readDataset() {
        Stream<Instance> result;
        Stream<Path> paths = Stream.empty();
        try {
            paths = Files.walk(Paths.get(pathToWikiXmlFolder))
                    .filter(path -> !Files.isDirectory(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        result = readWikiArticlesFromXmlFiles(paths);

        return result;
    }

    private Stream<Instance> readWikiArticlesFromXmlFiles(Stream<Path> paths) {
        Stream<Instance> result = paths
                .map(path -> parseXmlWikiextractorFile(path))
                .flatMap(list -> list.stream());

        return result;
    }

    private List<Instance> parseXmlWikiextractorFile(Path path) {
        List<Instance> result = new ArrayList<>();
        Document doc;

        try {
            doc = Jsoup.parse(path.toFile(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }

        for (Element element : doc.getElementsByTag("doc")) {
            String url = element.attr("url");
            String text = element.text();
            Instance wikiPage = new WikipediaArticle();
            wikiPage.setText(text);
            wikiPage.setUri(url);
            result.add(wikiPage);
        }

        return result;
    }
}
