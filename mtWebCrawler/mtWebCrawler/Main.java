package mtWebCrawler;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        mtCrawler mtc = new mtCrawler();
        mtc.crawl(4);

        //System.out.println("please enter number of threads");
        // Scanner s = new Scanner(System.in);
        // //int n = s.nextInt();
        // ArrayList<WebCrawler> bots = new ArrayList<WebCrawler>();

        // FileFilling file = new FileFilling();
        // //FileFilling.fileCreating();
        // file.fileWriterCreation();

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

        // bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Web_crawler", 1, 1, file));
        // bots.add(new WebCrawler("https://moz.com/beginners-guide-to-seo/how-search-engines-operate", 2, 2, file));
        // bots.add(new WebCrawler("https://www.searchenginejournal.com/search-engines/ranking/", 3, 3, file));
        // bots.add(new WebCrawler("https://www.techopedia.com/definition/28064/search-engine-query", 4, 4, file));
        // bots.add(new WebCrawler("https://www.w3schools.com/whatis/", 5, 5, file));
        // bots.add(new WebCrawler("https://www.guru99.com/java-static-variable-methods.html", 6, 6, file));
        // bots.add(new WebCrawler("https://softwareengineering.stackexchange.com/questions/185527/how-to-find-a-good-seed-page-for-a-web-crawler", 7, 7, file));
        // bots.add(new WebCrawler("https://nlp.stanford.edu/IR-book/html/htmledition/crawling-1.html", 8, 8, file));
        // bots.add(new WebCrawler("https://www.guru99.com/multithreading-java.html", 9, 9, file));
        // bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Operating_system", 10, 10, file));
        // bots.add(new WebCrawler("https://en.wikipedia.org/wiki/NoSQL", 11, 11, file));
        // bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Inverted_index", 12, 12, file));
        // bots.add(new WebCrawler("https://en.wikipedia.org/wiki/MongoDB", 13, 13, file));
        // bots.add(new WebCrawler("https://www.igi-global.com/dictionary/adaptive-query-processing-data-grids/24374", 14, 14, file));
        // bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Phrase_search", 15, 15, file));
        // bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Jsoup", 16, 16, file));
        // bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Elasticsearch", 17, 17, file));

        // for(WebCrawler w: bots){
        //     try {
        //         w.getThread().join();
        //     } catch (InterruptedException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        // }

        //FileFilling.fileClosing();

    }
}