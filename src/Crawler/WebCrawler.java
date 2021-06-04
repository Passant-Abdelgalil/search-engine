package Crawler;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import Crawler.BFSNeighbourList.urlObj;


public class WebCrawler implements Runnable {

    private final Thread thread;
    private final int ID;
    public RobotChecker robotChecker;
    boolean stopCrawling = false;
    private BFSNeighbourList bfsQueue;


    public WebCrawler(BFSNeighbourList bfsQueue, int num, RobotChecker checker) {
        // Create a thread with necessary variables
        ID = num;
        this.bfsQueue = bfsQueue;
        this.robotChecker = checker;
        thread = new Thread(this);

        thread.start();
    }

    @Override
    public void run() {
        crawl();
    }

    private void crawl() { // level init =1 LineinFile init=1
        while (true) {

            urlObj top = null;
            // Check the stopping criteria
            synchronized (this.bfsQueue) {
                if (bfsQueue.count >= 5000) {   // untill we have 5000 link //&& file.count<=5000 8alat
                    stopCrawling = true;
                } else {
                    top = bfsQueue.getQTop();
                    System.out.println(getThread().getName()+"-> " + top.url);
                }
            }
            // Crawling Logic
            if (!stopCrawling && top != null) {
                // Check HTML Documents
                Document doc = request(top.url);
                // If the url is for HTML document, crawl it
                if (doc != null) {
                    if(!doc.documentType().name().equals("html"))   continue;
                    System.out.println("Thread "+ getThread().getName()+ " will crawl " + doc.select("a[href]").size() + " link");
                    for (Element link : doc.select("a[href]")) {
                        String next_link = link.absUrl("href");
                        if (robotChecker.isAllowed(next_link)) {
                            top.addneighbours(new urlObj(next_link));
                        }
                    }
                    System.out.println(getThread().getName() + " will write to file");
                    synchronized (this) {
                        bfsQueue.bfs(top);
                    }
                }
            }else{
                break;
            }
        }
    }


    private Document request(String url) { // Parsing HTML document
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if (con.response().statusCode() == 200) {
                System.out.println("\n**Bot ID:" + ID + " Received Webpage at " + url);
                String title = doc.title();
                System.out.println(title);
                return doc;
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