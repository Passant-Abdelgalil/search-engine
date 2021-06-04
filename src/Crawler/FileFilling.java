package Crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class FileFilling {
    ArrayList<String> visitedLinks = new ArrayList<String>(); // el mfrood a7ot synch 3ala di, wl file, wl level
    // n7ot impl. el crawl f class? wlla class
    // FileFilling

    int level; // msh mohm
    static int lineInFile;
    static File myObj;
    static FileWriter myWriter;
    static int count = 0; // static bec common to all class instances
    static String filename;

    FileFilling(String filename)
    {
        FileFilling.filename=filename;
    }
    static void fileCreating() {
        try {
            myObj = new File(filename);
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

    static void fileWriterCreation() { // quikfix made me make it static
        try {
            myWriter = new FileWriter(filename);
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

    synchronized static void fileClosing() {
        try {
            myWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    synchronized String ReadFromFile(int n) {
        try (Stream<String> lines = Files.lines(Paths.get(filename))) {
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

    synchronized void countIncrement() {
        count++;
    }

    synchronized int lineInFileIncrement() {
        return lineInFile++;
    }

}
