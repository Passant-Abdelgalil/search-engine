package SearchEngine;

class SearchEngine {

    public static void main(String... args) throws InterruptedException {

        RobotChecker checker = new RobotChecker();
        FileFilling file = new FileFilling();
        FileFilling.fileCreating();
        FileFilling.fileWriterCreation();
        // should be a USER INPUT
        int numOfThreads = 2;
        Thread[] crawlerThreads = new Thread[numOfThreads];

        for (int i = 0; i < numOfThreads; i++) {
            crawlerThreads[i] = new Thread(new Crawler(checker, file,  i));
            crawlerThreads[i].start();
        }

        for (int i = 0; i < numOfThreads; i++) {
            crawlerThreads[i].join();
        }
        FileFilling.fileClosing();
    }
}