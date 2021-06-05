package mtWebCrawler;

import java.util.ArrayList;

public class mtCrawler {
    BFSNeighbourList bfsQueue;
    RobotChecker rc;

    public mtCrawler() {
        /*                                          Seeds                                               */
        ArrayList<String> seed = new ArrayList<String>();
        seed.add("https://en.wikipedia.org/wiki/Main_Page");
        seed.add("https://stackoverflow.com/questions/");
        seed.add("https://stackexchange.com/");
        seed.add("https://www.blurtit.com/topics");
        seed.add("https://www.bbc.com/news/world");


/*        FileFilling seedfile = new FileFilling("seedsFile.txt");
        FileFilling.fileCreating();
*/
        FileFilling urlFile = new FileFilling("urlsFile.txt");
        urlFile.fileCreating();
        //FileFilling.fileWriterCreation();
        urlFile.fileWriterCreation();


        bfsQueue = new BFSNeighbourList(seed, urlFile, new URINormalizer()); //lazem da ya5od el seed
        //bfsQueue.bfs(bfsQueue.getQTop()); da f class el WebCrawler(one crawler (thread))
        rc = new RobotChecker();
    }

    public void crawl(int num) {
        for (int i = 0; i < num; i++) {
            WebCrawler wc = new WebCrawler(bfsQueue, i, rc);
        }
    }
}
