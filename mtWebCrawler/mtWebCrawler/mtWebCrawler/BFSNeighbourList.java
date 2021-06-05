package mtWebCrawler;

import java.util.*;

public class BFSNeighbourList {
    FileFilling seedsFile;
    FileFilling urlsFile;
    ArrayList<String> seed;
    urlObj element;
    long count = 0;
    boolean reached5000 = false;
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

        if (urlsFile.isFileEmpty()) {
            for (int i = 0; i < 5; i++) {
                queue.add(new urlObj(seed.get(i)));
            }
            System.out.println("Queue filled with seeds");
        } else {
            long numLines = urlsFile.numLinesInFile();
            String lastUrlInFile = urlsFile.ReadFromFile(numLines - 1);
            queue.add(new urlObj(lastUrlInFile));
            for (long i = 0; i <= numLines - 1; i++) {
                String u = urlsFile.ReadFromFile(i);
                visitedLinks.add(u);
            }

            count = numLines;
            urlsFile.closeFis();
        }

    }

    public synchronized urlObj getQTop() {
        if (queue.size() > 0) {
            return queue.remove();
        }
        return null;
    }

    public synchronized void bfs(urlObj node) {
        // node.visited = true;
        // normalized
        // String normalized = normalizer.normalizePreservedSemantics(node.url);
        // normalized = normalizer.normalizeSemantics(normalized);
        // if (!visitedLinks.contains(normalized)) {
        // visitedLinks.add(normalized);
        // count++;
        // urlsFile.WriteToFile(node.url);
        // }

        if (!visitedLinks.contains(node.url)) {
            visitedLinks.add(node.url);
            count++;
            if (count == 5001)
                reached5000 = true;

            if (!reached5000)
                urlsFile.WriteToFile(node.url);
            System.out.println("count of urls = " + count);
        }
        List<urlObj> neighbours = node.getNeighbours();
        for (urlObj urlo : neighbours) {
            if (count >= 5001)
                break;
            // normalized = normalizer.normalizePreservedSemantics(url.url);
            // normalized = normalizer.normalizeSemantics(normalized);

            // if (!visitedLinks.contains(normalized)) {
            if (!visitedLinks.contains(urlo.url)) {
                queue.add(urlo);
                // visitedLinks.add(normalized);
                // count++;
                // urlsFile.WriteToFile(url.url);
            }
            // try {
            // Thread.sleep(200);
            // } catch (Exception ignored) {
            // }
        }
        System.out.println(Thread.currentThread().getName() + " Has " + visitedLinks.size() + " Unique Link");
    }

}