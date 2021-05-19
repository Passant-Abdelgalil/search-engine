package SearchEngine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.net.URL;

class FileFilling {

    ArrayList<String> visitedLinks = new ArrayList<>(); // el mfrood a7ot sync 3ala di, wl file, wl level
    // n7ot impl. el crawl f class? wlla class
    // FileFilling

    int level = 2;
    int lineInFile = 0;
    static File myObj;
    static FileWriter myWriter;

    static void fileCreating() {
        try {
            myObj = new File("filename.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (Exception ex) {
            System.out.println("An error occurred while creating the file");
            System.out.println(ex.getMessage());
        }
    }

    synchronized void WriteToFile(String URL) {
        try {
            myWriter = new FileWriter("filename.txt", true);
            myWriter.write(URL + "\n");
            System.out.println(Thread.currentThread().getName()+": "+URL);

        } catch (Exception e) {
            System.out.println("An error occurred when Write to file.");
            System.out.println(e.getMessage());
        } finally {
            try {
                myWriter.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    synchronized String ReadFromFile(int n) {
        try {
            Stream<String> lines = Files.lines(Paths.get("filename.txt"));
            String url = lines.skip(n).findFirst().orElse("");
            //System.out.println(Thread.currentThread().getName() + ": " + url);
            if (url.equals("")) {
                System.out.println("no url in the file");
            }
            lines.close();
            return url;
        } catch (Exception e) {
            System.out.println("An error when Read from file. " + e.getMessage());
            return "";
        }
    }

    synchronized void levelIncrement() {
        level++;
    }

    synchronized int lineInFileIncrement() {
        return lineInFile++;
    }

}


public class Crawler implements Runnable {
    private static final int MAX_DEPTH = 5000;
    private int ID;
    private ArrayList<String> VisitedLinks = new ArrayList<>();
    private RobotChecker robotChecker;
    final FileFilling file;

    public Crawler(RobotChecker checker, FileFilling fileFill, int id) {
        System.out.printf("Crawler with ID = %d just created!\n ", id);
        this.ID = id;
        this.robotChecker = checker;
        this.file = fileFill;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
        crawl();
    }

    private synchronized void crawl() { // level init =1 lineInFile init=1
        if (file.level <= MAX_DEPTH) { // until we have 5000 link
            String url;
            Document doc = null;
            synchronized (this.file) {
                url = file.ReadFromFile(this.file.lineInFile);
                file.lineInFileIncrement();
            }
            if (url.equals("")) {
                System.out.println("Empty line!");
                return;
            }
            System.out.println(Thread.currentThread().getName() + ": " + url);
            try {
                URLConnection uConn = new URL(url).openConnection();
                String contentType = uConn.getHeaderField("Content-Type");
                if (contentType.equals("text/html; charset=utf-8") || contentType.equals("text/html")) {
                    doc = request(url);
                    System.out.println("HTML");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            if (doc != null) {
                for (Element link : doc.select("a[href]")) {
                    String next_link = link.absUrl("href");
                    try {
                        robotChecker.getRules(next_link);
                    } catch (Exception e) {
                        System.out.println("Error while checking robots.txt! " + e.getMessage());
                        continue;
                    }
                    synchronized (this.file) {
                        if (!file.visitedLinks.contains(next_link) && robotChecker.isAllowed(next_link)) {
                            file.WriteToFile(next_link);
                            this.file.level++;
                            file.levelIncrement();
                            file.visitedLinks.add(next_link);
                        }
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        System.out.println(Thread.currentThread().getName() + "is awaken");
                    }
                }


            }
            crawl();
        }

    }

    private Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();

            if (con.response().statusCode() == 200) {
                System.out.println("\n**Bot ID:" + ID + " Received Webpage at " + url);

                String title = doc.title();
                System.out.println(title);
                file.visitedLinks.add(url);

                return doc;
            }
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }


}
