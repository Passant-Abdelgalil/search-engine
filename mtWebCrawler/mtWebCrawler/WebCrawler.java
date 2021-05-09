package mtWebCrawler;

import java.io.IOException;
import java.util.ArrayList;

//import javax.swing.text.Document;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.*;

class FileFilling {

    ArrayList<String> visitedLinks = new ArrayList<String>(); // el mfrood a7ot synch 3ala di, wl file, wl level
                                                              // n7ot impl. el crawl f class? wlla class
                                                              // FileFilling

    int level;
    int lineInFile;
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
        } catch (IOException ex) {
            System.out.println("An error occurred.");
            ex.printStackTrace();
        }
    }

    static void fileWriterCreation() {  //quikfix made me make it static
        try {
            myWriter = new FileWriter("filename.txt");
            // myWriter.write(URL);

        } catch (IOException e) {
            System.out.println("An error occurred when Write to file.");
        }

    }

    synchronized void WriteToFile(String URL) {
        try {
            myWriter.write(URL + "\n");

        } catch (IOException e) {
            System.out.println("An error occurred when Write to file.");
        }
    }

    static void fileClosing() {
        try {
            myWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    synchronized String ReadFromFile(int n) {
        try (Stream<String> lines = Files.lines(Paths.get("filename.txt"))) {
            String link = lines.skip(n).findFirst().get();
            return link;
        } catch (IOException e) {
            System.out.println("An error when Read from file.");
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

public class WebCrawler implements Runnable {

    private static final int MAX_DEPTH = 3;
    private Thread thread;
    private String first_link;
    private int ID;
    private int startLine;

    FileFilling file = new FileFilling();

    public WebCrawler(String link, int num, int startLine) {
        System.out.print("WebCrawler created");
        first_link = link;
        ID = num;
        this.startLine = startLine;

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

        file.WriteToFile(first_link);

        crawl(startLine);

    }

    private void crawl(int lineInFile) { // level init =1 LineinFile init=1

        if (file.level <= MAX_DEPTH) { // untill we have 5000 link
            System.out.println("want to read");
            String URL = file.ReadFromFile(lineInFile);
            if (URL == "")
                System.out.println("didn't read");

            Document doc = request(URL);
            if (doc != null) {
                for (Element link : doc.select("a[href]")) {
                    String next_link = link.absUrl("href");

                    if (file.visitedLinks.contains(next_link) == false) {
                        file.WriteToFile(next_link);
                        file.levelIncrement();
                    }
                }
                crawl(file.lineInFileIncrement());
            }
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
        } catch (IOException e) {
            return null;
        }

    }

    public Thread getThread() {
        return thread;
    }

}