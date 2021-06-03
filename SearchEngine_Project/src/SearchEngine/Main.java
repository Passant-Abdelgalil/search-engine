package SearchEngine;

class SearchEngine {

    public static void main(String... args) throws InterruptedException {

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
/*
        RobotChecker.checkEncoding("https://yoast.com/search-operators/");
        RobotChecker.checkEncoding("https://yoast.com/search-operators/1");
*/
    }
}