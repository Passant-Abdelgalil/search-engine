package SearchEngine;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SearchEngine {

    public static void main(String... args) throws InterruptedException, UnknownHostException {

        RobotChecker checker = new RobotChecker();
        FileFilling file = new FileFilling();
        FileFilling.fileCreating();
        // should be a USER INPUT
        int numOfThreads = 1;

        Thread[] crawlerThreads = new Thread[numOfThreads];

        for (int i = 0; i < numOfThreads; i++) {
            crawlerThreads[i] = new Thread(new Crawler(checker, file,  i));
            crawlerThreads[i].start();
        }

        for (int i = 0; i < numOfThreads; i++) {
            crawlerThreads[i].join();

        }

    }

}