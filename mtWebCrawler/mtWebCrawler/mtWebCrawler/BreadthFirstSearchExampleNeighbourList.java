package mtWebCrawler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
//import java.net.HttpURLConnection;
import java.util.ArrayList;

public class BreadthFirstSearchExampleNeighbourList // its constructor makes the queue
{
    // from abeer
    FileFilling seedsFile;
    FileFilling urlsFile;
    ArrayList<String> seed;
    // abeer
    urlObj element;
    int count = 0;
    ArrayList<String> visitedLinks = new ArrayList<String>();

    private Queue<urlObj> queue;
    static ArrayList<urlObj> urls = new ArrayList<urlObj>();

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

    public BreadthFirstSearchExampleNeighbourList(ArrayList<String> seed, FileFilling urlsFile) {
        queue = new LinkedList<urlObj>();

        this.seed = seed;
        this.urlsFile = urlsFile;

        for (int i = 0; i < 5; i++) {
            // String urlstr = seedsFile.ReadFromFile(i);
            queue.add(new urlObj(seed.get(i)));
        }
        System.out.println("q filled with seed");
        // System.out.println(getQTop().url);
    }

    // from abeer
    public synchronized urlObj getQTop() {
        element = queue.remove();
        // return queue.peek();
        return element;

    }

    public synchronized void bfs(urlObj node) {
        // queue.add(node);
        //node.visited = true;
        visitedLinks.add(node.url);
        count++;
        urlsFile.WriteToFile(node.url);
        // abeer
        // List<urlObj> neighbours = element.getNeighbours();
        // for (int i = 0; i < neighbours.size(); i++) {
        // urlObj n = neighbours.get(i);
        // if (n != null && !n.visited) {
        // queue.add(n);
        // n.visited = true;
        // count++;
        
        /*check url*/
        
        // urlsFile.WriteToFile(n.url);

        // }
        // }
        // end abeer

        // while (!queue.isEmpty() && count <= 5000) {

        // urlObj element = queue.remove(); haaam 3mltha fl getqtop
        // System.out.print(element.url + "t"); //add to the file

        //List<urlObj> neighbours = element.getNeighbours();
        List<urlObj> neighbours = node.getNeighbours();
        for (int i = 0; i < neighbours.size(); i++) {
            urlObj n = neighbours.get(i);
            if (n != null  && !visitedLinks.contains(n.url)) {  //&& !n.visited 
                queue.add(n);
                //n.visited = true;
                //visitedLinks.add(n.url);
               

                urlsFile.WriteToFile(n.url);

            }
        }

        // }
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
