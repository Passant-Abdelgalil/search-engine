import java.util.ArrayList;

public class Crawler{
    BFSNeighbourList bfsQueue;
    RobotChecker rc;

    public Crawler() {
        ArrayList<String> seed = new ArrayList<String>();
        seed.add("https://en.wikipedia.org/wiki/Main_Page");
        seed.add("https://stackoverflow.com/questions/");
        seed.add("https://stackexchange.com/");
        seed.add("https://www.blurtit.com/topics");
        seed.add("https://www.bbc.com/news/world");


        FileFilling urlFile = new FileFilling("urlsFile.txt");
        urlFile.fileCreating();
        urlFile.fileWriterCreation();

        rc = new RobotChecker();
        bfsQueue = new BFSNeighbourList(seed, urlFile, new URINormalizer(), rc); //lazem da ya5od el seed

    }

    public void crawl(int num) {
        for (int i = 0; i < num; i++) {
            WebCrawler wc = new WebCrawler(bfsQueue, i);
        }
    }
}