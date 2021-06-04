package Crawler;
import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.URLConnection;
//import java.net.HttpURLConnection;
import java.util.ArrayList;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.net.URL;
// import java.net.URLEncoder;

//import javax.swing.text.Document;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import Crawler.BreadthFirstSearchExampleNeighbourList.urlObj;


public class WebCrawler implements Runnable {

    // private static final int MAX_DEPTH = 5000;
    private Thread thread;
    // private String first_link;

    private int ID;
    private RobotChecker robotChecker;
    BreadthFirstSearchExampleNeighbourList bfsQueue;
    // private int startLine;

    // FileFilling file;

    public WebCrawler(BreadthFirstSearchExampleNeighbourList bfsQueue, int num, RobotChecker checker) {
        System.out.print("WebCrawler created");
        // first_link = link;
        ID = num;
        this.bfsQueue = bfsQueue;
        robotChecker = checker;
        // this.startLine = startLine;
        // this.file = file;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // file.WriteToFile(first_link);

        // crawl(startLine);

        crawl();

    }

    private void crawl() { // level init =1 LineinFile init=1

        if (bfsQueue.count <= 5000) { // untill we have 5000 link //&& file.count<=5000 8alat

            urlObj top = bfsQueue.getQTop();
            Document doc = request(top.url);
            // Document doc = null;
            // try {
            // URLConnection uConn = new URL(bfsQueue.getQTop().url).openConnection();
            // String contentType = uConn.getHeaderField("Content-Type");
            // if (contentType.equals("text/html; charset=utf-8") ||
            // contentType.equals("text/html")) {
            // doc = request(top.url);
            // System.out.println("HTML");
            // }
            // } catch (Exception e) {
            // System.out.println(e.getMessage());
            // }

            if (doc != null) {
                for (Element link : doc.select("a[href]")) {
                    String next_link = link.absUrl("href");
                    // next_link = URI.create(next_link).normalize().toString();
                    try {
                        //robotChecker.getRules(next_link);
                        // System.out.println(next_link);
                    } catch (Exception e) {
                        System.out.println("Error while checking robots.txt! " + e.getMessage());
                        continue;
                    }

                    //if (next_link != null && !bfsQueue.visitedLinks.contains(next_link)) {
                    urlObj URLOBJJ = new urlObj(next_link);
                    top.addneighbours(URLOBJJ);
                    //}

                }

                bfsQueue.bfs(top);
            }

            crawl();
            // bfsQueue.bfs(bfsQueue.getQTop());
        }
    }

    // private void crawl(int level, String URL) { // level init =1 LineinFile
    // init=1

    // if (level <= MAX_DEPTH) { // untill we have 5000 link //&& file.count<=5000
    // 8alat
    // // System.out.println("want to read");
    // // String URL = file.ReadFromFile(lineInFile);
    // // if (URL == "")
    // // System.out.println("didn't read");

    // Document doc = request(URL);
    // if (doc != null) {
    // for (Element link : doc.select("a[href]")) {
    // String next_link = link.absUrl("href");

    // if (file.visitedLinks.contains(next_link) == false) {
    // file.countIncrement();
    // if (file.count <= 5000) {

    // // file.WriteToFile(next_link);
    // crawl(level++, next_link);

    // } else {
    // return;
    // }

    // // file.levelIncrement();
    // }
    // }
    // // crawl(file.lineInFileIncrement());
    // }
    // }

    // }

    private Document request(String url) { // Parsing HTML document
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();

            if (con.response().statusCode() == 200) {
                System.out.println("\n**Bot ID:" + ID + " Received Webpage at " + url);
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

    public Thread getThread() {
        return thread;
    }

}