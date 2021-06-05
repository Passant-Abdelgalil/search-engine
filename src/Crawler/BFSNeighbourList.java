package Crawler;

import java.io.IOException;
import java.util.*;
import java.net.URI;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

public class BFSNeighbourList {
    FileFilling seedsFile;
    FileFilling urlsFile;
    ArrayList<String> seed;
    urlObj element;
    int count = 0;
    URINormalizer normalizer;
    ArrayList<String> visitedLinks = new ArrayList<String>();

    public Queue<urlObj> queue;
    static ArrayList<urlObj> urls = new ArrayList<urlObj>();

    public void printQueue() {
        Iterator<urlObj> iterator = queue.iterator();
        while (iterator.hasNext()) {
            urlObj url = iterator.next();
            if (url != null) {
                System.out.println(url.url);
            }
        }
    }

    static class urlObj // urlObj
    {
        String url;
        boolean visited;
        List<urlObj> neighbours;

        urlObj(String url) {
            this.url = url;
            this.neighbours = new ArrayList<>();

        }

        public synchronized void addneighbours(urlObj neighbourNode) {
            this.neighbours.add(neighbourNode);
        }

        public List<urlObj> getNeighbours() {
            return neighbours;
        }

        public void setNeighbours(List<urlObj> neighbours) {
            this.neighbours = neighbours;
        }
    }

    public BFSNeighbourList(ArrayList<String> seed, FileFilling urlsFile, URINormalizer normalizer) {
        queue = new LinkedList<urlObj>();
        this.normalizer = normalizer;
        this.seed = seed;
        this.urlsFile = urlsFile;

        for (int i = 0; i < 5; i++) {
            queue.add(new urlObj(seed.get(i)));
        }
        System.out.println("Queue filled with seeds");
    }

    public synchronized urlObj getQTop() {
        if (queue.size() > 0) {
            return queue.remove();
        }
        return null;
    }

    public synchronized void bfs(urlObj node) {
        // normalized
        String normalized = normalizer.normalizePreservedSemantics(node.url);
        normalized = normalizer.normalizeSemantics(normalized);

        if (!visitedLinks.contains(normalized)) {
            visitedLinks.add(normalized);
            count++;
            urlsFile.WriteToFile(node.url);
        }
        List<urlObj> neighbours = node.getNeighbours();
        for (urlObj url : neighbours) {
            if (count >= 5000) break;
            normalized = normalizer.normalizePreservedSemantics(url.url);
            normalized = normalizer.normalizeSemantics(normalized);
            if (!visitedLinks.contains(normalized)) {
                queue.add(url);
                visitedLinks.add(normalized);
            }
        }
        System.out.println(Thread.currentThread().getName()+" Has " + visitedLinks.size() + " Unique Link");
    }

}
