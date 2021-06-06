import java.util.ArrayList;

public class Main {
    static BFSNeighbourList bfsQueue = null;
    static  RobotChecker rc = null;
    public static void main(String[] args) {
        ArrayList<String> seed = null;
        FileFilling urlFile = init(seed);
        rc = new RobotChecker();
        bfsQueue = new BFSNeighbourList(seed, urlFile, new URINormalizer(), rc);

        /*Crawler mtc = new Crawler();
        mtc.crawl(4);*/

        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new WebCrawler(bfsQueue, i));
            threads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
            }
        }

    }

    private static FileFilling init(ArrayList<String> seed) {
        seed = new ArrayList<String>();
        seed.add("https://en.wikipedia.org/");
        seed.add("https://stackoverflow.com");
        seed.add("https://quora.com");
        seed.add("https://reddit.com");
        seed.add("https://geeksforgeeks.or");

        FileFilling urlFile = new FileFilling("urlsFile.txt");
        urlFile.fileCreating();
        urlFile.fileWriterCreation();

        return  urlFile;
    }
}
