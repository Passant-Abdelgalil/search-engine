package Crawler;

import java.io.IOException;
import java.util.*;

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
        //node.visited = true;
        visitedLinks.add(node.url);
        count++;
        urlsFile.WriteToFile(node.url);

        List<urlObj> neighbours = node.getNeighbours();
        for (urlObj url : neighbours) {
            if (count >= 5000) break;
            String normalized = normalizer.normalizePreservedSemantics(url.url);
            normalized = normalizer.normalizeSemantics(normalized);

            if (!visitedLinks.contains(normalized)) {
                queue.add(url);
                visitedLinks.add(normalized);
                count++;
                urlsFile.WriteToFile(url.url);
            }
            try {
                Thread.sleep(200);
            } catch (Exception ignored) {
            }
        }
    }

    private Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();

            if (con.response().statusCode() == 200) {
                // System.out.println("\n**Bot ID:" + ID + " Received Webpage at " + url);
                // file.WriteToFile(url);
                // if (file.count <= 5000) {

                // file.WriteToFile(url);
                // }

                String title = doc.title();
                System.out.println(title);
                // synchronized (this.file) {
                // file.visitedLinks.add(url);
                // }

                return doc;

                // HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                // urlc.setAllowUserInteraction( false );
                // urlc.setDoInput( true );
                // urlc.setDoOutput( false );
                // urlc.setUseCaches( true );
                // urlc.setRequestMethod("HEAD");
                // urlc.connect();
                // String mime = urlc.getContentType();
                // if(mime.equals("text/html") {
                // // do your stuff

                // System.out.println("\n**Bot ID:" + ID + " Received Webpage at " + url);
                // // file.WriteToFile(url);
                // if (file.count <= 5000) {

                // file.WriteToFile(url);
                // }

                // String title = doc.title();
                // System.out.println(title);
                // synchronized (this.file) {
                // file.visitedLinks.add(url);
                // }

                // return doc;
                // }

            }
            return null;
        } catch (IOException e) {
            return null;
        }

    }
}
