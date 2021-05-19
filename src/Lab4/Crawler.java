package Lab4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.*;


class FileFilling {

    ArrayList<String> visitedLinks = new ArrayList<>(); // el mfrood a7ot sync 3ala di, wl file, wl level
    // n7ot impl. el crawl f class? wlla class
    // FileFilling

    int level;
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

    static void fileWriterCreation() {  //quick fix made me make it static
        try {
            myWriter = new FileWriter("/home/passant/Desktop/APT_Lab4/src/Lab4/filename.txt", true);
        } catch (Exception e) {
            System.out.println("An error occurred when Write to file.");
            System.out.println(e.getMessage());
        }
    }

    synchronized void WriteToFile(String URL) {
        try {
            myWriter = new FileWriter("/home/passant/Desktop/APT_Lab4/src/Lab4/filename.txt", true);
            myWriter.write(URL + "\n");
            System.out.println(URL);

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

    static void fileClosing() {
        try {
            myWriter.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
    }

    synchronized String ReadFromFile(int n) {
        try {
            Stream<String> lines = Files.lines(Paths.get("/home/passant/Desktop/APT_Lab4/src/Lab4/filename.txt"));
            String url = lines.skip(n).findFirst().get();
            if (!url.equals("")) {
                System.out.println(url);
            } else {
                System.out.println("not present");
            }
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
    FileFilling file;

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
        //file.WriteToFile(first_link);
        crawl();
    }

    private synchronized void crawl() { // level init =1 lineInFile init=1
            if (file.level <= MAX_DEPTH) { // until we have 5000 link
                System.out.println("Reading...");
                String URL;
                synchronized (this.file) {
                    URL = file.ReadFromFile(this.file.lineInFile);
                    file.lineInFileIncrement();
                    System.out.println(Thread.currentThread().getName() + " started with URL: "+ URL);
                }
                if (URL.equals(""))
                    System.out.println("Empty line!");

                Document doc = request(URL);
                if (doc != null) {
                    for (Element link : doc.select("a[href]")) {
                        String next_link = link.absUrl("href");
                        try {
                            robotChecker.getRules(next_link);
                        } catch (Exception e) {
                            System.out.println("Error while checking robots.txt!");
                            System.out.println(e.getMessage());
                        }
                        synchronized (this.file) {
                            if (!file.visitedLinks.contains(next_link) && robotChecker.isAllowed(next_link)) {
                                file.WriteToFile(next_link);
                                file.levelIncrement();
                                file.visitedLinks.add(next_link);
                            }
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println(Thread.currentThread().getName() + "is awaken");
                    }
                    crawl();
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }



/*
    private int level;
   private void WriteToFile(String URL) {
     try {
         FileWriter myWriter = new FileWriter("test.txt");
         myWriter.write(URL);
         myWriter.close();
     } catch (Exception e) {
         System.out.println("An error occurred when Write to file.");
         System.out.println(e.getMessage());
     }
 }
    private String ReadFromFile(int n) {
     try (Stream<String> lines = Files.lines(Paths.get("file.txt"))) {
         return lines.skip(n).findFirst().isPresent()?lines.skip(n).findFirst().get():"";
     } catch (Exception e) {
         System.out.println("An error when Read from file.");
         System.out.println(e.getMessage());
         return "";
     }
 }
*/

}
