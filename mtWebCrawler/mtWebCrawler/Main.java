package mtWebCrawler;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        //System.out.println("please enter number of threads");
        Scanner s = new Scanner(System.in);
        //int n = s.nextInt();
        ArrayList<WebCrawler> bots = new ArrayList<WebCrawler>();

        //FileFilling.fileCreating();
        FileFilling.fileWriterCreation();

        // try {
        //     Thread.currentThread().join();
        // } catch (InterruptedException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }

        // for(int i=0; i<n;i++)
        // {
        //     bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Web_crawler", i+1, i+1));
        // }
        
        //for(thread : threads)

        bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Web_crawler", 1, 1));
        bots.add(new WebCrawler("https://moz.com/beginners-guide-to-seo/how-search-engines-operate", 2, 2));
        bots.add(new WebCrawler("https://www.searchenginejournal.com/search-engines/ranking/", 3, 3));

        for(WebCrawler w: bots){
            try {
                w.getThread().join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        FileFilling.fileClosing();

    }
}