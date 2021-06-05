package mtWebCrawler;

import java.io.IOException;
import java.util.ArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.*;

class FileFilling {

    ArrayList<String> visitedLinks = new ArrayList<String>(); // el mfrood a7ot synch 3ala di, wl file, wl level
    // n7ot impl. el crawl f class? wlla class
    // FileFilling

    int level; // msh mohm
    int lineInFile;
    File myObj;
    FileWriter myWriter;
    int count = 0; // static bec common to all class instances
    String filename;
    FileInputStream fis;

    FileFilling(String filename) {
        this.filename = filename;
    }

    void fileCreating() {
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

    void fileWriterCreation() { // quikfix made me make it static
        try {
            myWriter = new FileWriter(filename, true);
            // myWriter.write(URL);

        } catch (IOException e) {
            System.out.println("An error occurred when Write to file.");
        }

    }

    synchronized void WriteToFile(String URL) {
        try {
            this.fileWriterCreation();
            myWriter.write(URL+"\r\n");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred when Write to file.");
        }
    }

    synchronized void fileWriterClosing() {
        try {
            myWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    synchronized String ReadFromFile(long l) {
        try (Stream<String> lines = Files.lines(Paths.get(filename))) {
            String link = lines.skip(l).findFirst().get();
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

    boolean isFileEmpty() {
        if (myObj.length() == 0)
            return true;
        else
            return false;
    }

    long numLinesInFile() {
        try {
            fis = new FileInputStream(myObj);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        byte[] byteArray = new byte[(int)myObj.length()];

        try {
            fis.read(byteArray);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String data = new String(byteArray);
        String[] stringArray = data.split("\r\n");
        long num = stringArray.length;
        System.out.println("Number of lines in the file are: " + num);

        return num;

    }

    void closeFis() {
        try {
            fis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
