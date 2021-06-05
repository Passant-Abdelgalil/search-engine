import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebCrawler implements Runnable {

    private final Thread thread;
    private final int ID;
    boolean stopCrawling = false;
    BFSNeighbourList bfsQueue;

    public WebCrawler(BFSNeighbourList bfsQueue, int num) {
        // Create a thread with necessary variables
        ID = num;
        this.bfsQueue = bfsQueue;
        thread = new Thread(this);

        thread.start();
    }

    @Override
    public void run() {
        crawl();
    }

    private void crawl() { // level init =1 LineinFile init=1
        while (true) {

            BFSNeighbourList.urlObj top = null;
            Document doc = null;
            // Check the stopping criteria
            synchronized (this.bfsQueue) {
                if (bfsQueue.count >= 5000) { // untill we have 5000 link //&& file.count<=5000 8alat
                    stopCrawling = true;
                } else {
                    top = bfsQueue.getQTop();

                }
            }
            // Crawling Logic
            if (!stopCrawling) { // && top != null //emta el top tb2a null?
                // Check HTML Documents
                if (top != null)
                    doc = request(top.url);
                if (doc != null) {
                    if (!doc.documentType().name().equals("html")) {
                        System.out.println(doc.documentType().name());
                        continue;
                    }
                    System.out.println(getThread().getName() + "-> " + top.url);
                    System.out.println("Thread " + getThread().getName() + " will crawl " + doc.select("a[href]").size()
                            + " link");
                    for (Element link : doc.select("a[href]")) {
                        String next_link = link.absUrl("href");
                        top.addneighbours(new BFSNeighbourList.urlObj(next_link));
                    }
                    System.out.println(getThread().getName() + " will write to file");
                    synchronized (this.bfsQueue) {
                        bfsQueue.bfs(top);
                    }
                }else{
                    System.out.println("Empty document");
                }
            } else {
                break;
            }
        }
    }

    private Document request(String url) { // Parsing HTML document
        try {
            Connection con = null;
            if (url != null && url.length() != 0) {
                con = Jsoup.connect(url);
                Document doc = con.get();
                if (con.response().statusCode() == 200) {
                    System.out.println("\n**Bot ID:" + ID + " Received Webpage at " + url);
                    String title = doc.title();
                    System.out.println(title);
                    return doc;
                }
                return null;
            }
            return null;

        } catch (IOException e) {
            return null;
        }

    }

    public Thread getThread() {
        return thread;
    }

}
