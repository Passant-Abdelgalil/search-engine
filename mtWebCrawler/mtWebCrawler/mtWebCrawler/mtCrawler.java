package mtWebCrawler;

import java.util.ArrayList;

public class mtCrawler { // the class that creates threads and their common queue

    BreadthFirstSearchExampleNeighbourList bfsQueue;
    RobotChecker rc;

    public mtCrawler() {
        
        ArrayList<String> seed = new ArrayList<String>();
        seed.add("https://en.wikipedia.org/wiki/Web_crawler");
        seed.add("https://moz.com/beginners-guide-to-seo/how-search-engines-operate");
        seed.add("https://www.searchenginejournal.com/search-engines/ranking/");
        seed.add("https://www.techopedia.com/definition/28064/search-engine-query");
        seed.add("https://www.w3schools.com/whatis/");


        FileFilling seedfile = new FileFilling("seedsFile.txt");
        seedfile.fileCreating();

        FileFilling urlfile = new FileFilling("urlsFile.txt");
        urlfile.fileCreating();
        urlfile.fileWriterCreation();

        bfsQueue = new BreadthFirstSearchExampleNeighbourList(seed, urlfile); //lazem da ya5od el seed
        //bfsQueue.bfs(bfsQueue.getQTop()); da f class el WebCrawler(one crawler (thread))
        rc = new RobotChecker();
    }   

    void crawl(int num)
        {
            for(int i=0; i<num;i++)
            {
                WebCrawler wc = new WebCrawler(bfsQueue, i, rc);
            }
            
        }

}