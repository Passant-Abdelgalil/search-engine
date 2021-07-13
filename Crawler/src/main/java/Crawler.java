import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class Crawler implements Runnable {
    private int ID;
    boolean stopCrawling;
    URLsQueue readQueue;
    private RobotChecker robotChecker;
    private URINormalizer normalizer = new URINormalizer();
    private MongoDatabase db;
    private MongoCollection<org.bson.Document> crawledUrlsCollection;
    private MongoCollection<org.bson.Document> notCrawledURLsCollection;

    /**
     * Class Constructor that sets threads ID and assign values with the
     * shared objects among all threads such as readQueue, robotChcker and DB client
     * which is used to get collections to store crawled and not yet crawled urls
     *
     * @param Queue:    shared URLsQueue Object among all threads to store urls temporarily
     * @param id:       integer value that identifies the thread
     * @param database: DB client used to get collections
     * @param checker:  robot checker object shared among all threads
     */
    public Crawler(URLsQueue Queue, int id, MongoDatabase database, RobotChecker checker) {
        this.ID = id;
        this.readQueue = Queue;
        this.robotChecker = checker;
        db = database;

        // if crawled collection doesn't exist, create it
        if (!db.listCollectionNames().into(new ArrayList<String>()).contains("crawled")) {
            database.createCollection("crawled", null);
        }
        // if notCrawled collection doesn't exist, create it
        if (!db.listCollectionNames().into(new ArrayList<String>()).contains("notCrawled")) {
            database.createCollection("notCrawled", null);
        }

        crawledUrlsCollection = database.getCollection("crawled");
        notCrawledURLsCollection = database.getCollection("notCrawled");
    }

    @Override
    public void run() {
        crawl();
    }

    /**
     * This function takes a web page and parse all links in this document
     * and add them to the neighbour list of the passed urlObj onject
     *
     * @param doc:        web document to extract links from it
     * @param currentURL: utlObj object that represents the link for the passed document
     * @return urlObj object after updating its neighbour list
     */
    private URLsQueue.urlObj parseDocument(org.jsoup.nodes.Document doc, URLsQueue.urlObj currentURL) {
        for (Element link : doc.select("a[href]")) {
            // extracts absolute url
            String parsedLink = link.absUrl("href");
            // normalize the url
            parsedLink = normalizer.normalize(parsedLink);
            // insert it in notCrawledURLs collection
            Document document = new Document("url", parsedLink);
            notCrawledURLsCollection.insertOne(document);
            // update neighbours list
            currentURL.updateNeighbours(new URLsQueue.urlObj(parsedLink));
        }
        return currentURL;
    }

    private URLsQueue.urlObj getURLObject() {
        if (readQueue.queue.size() > 0)
            return readQueue.queue.remove(0);
        Document doc = notCrawledURLsCollection.find().first();
        if (doc != null)
            return new URLsQueue.urlObj(doc.get("url", String.class));
        return null;
    }

    /**
     * This is the main function run by the thread to crawl the internet
     * till it reaches a specific stopping criteria
     * whether the thread runs out of un-crawled urls or it reaches
     * 5000 crawled urls
     * it synchronizes inserting/popping from readQueue among all threads
     */
    private void crawl() {
        while (true) {
            URLsQueue.urlObj currentURL;
            org.jsoup.nodes.Document doc = null;
            synchronized (readQueue) {
                // stopping criteria
                if (crawledUrlsCollection.countDocuments() == 5000) {
                    break;
                } else {
                    currentURL = getURLObject();
                }
                // no url to be crawled
                if (currentURL == null) {
                    // try to sleep this thread until other threads wake it up, if
                    // new urls are ready for crawling
                    try {
                        System.out.println(Thread.currentThread().getName() + " sill sleep");
                        readQueue.wait();
                    } catch (InterruptedException ignored) {
                    }
                    currentURL = getURLObject();
                    // if something happened and no url was found, exit this thread
                    if (currentURL == null) {
                        System.out.println(Thread.currentThread().getName() + " will close");
                        break;
                    }
                    // else request the document for the url
                    doc = request(currentURL.getURL());
                }
            }

            // if request failed for this url
            if (doc == null)
                continue;

            // if the document type not HTML, skip
            if (doc.documentType() != null && !doc.documentType().name().equals("html"))
                continue;

            System.out.println(Thread.currentThread().getName() + " -> " + currentURL.getURL());

            // normalize URL
            String normalizedURI = normalizer.normalize(currentURL.getURL());
            currentURL.setURL(normalizedURI);

            // parse the document of this url, if it's allowed
            if(robotChecker.isAllowed(normalizedURI))
                currentURL = parseDocument(doc, currentURL);
            else
                continue;

            // add parsed URLs
            synchronized (readQueue) {
                // if number of required URLs reached, exit
                if (crawledUrlsCollection.countDocuments() >= 5000) {
                    // notify all sleeping threads if any
                    readQueue.notifyAll();
                    break;
                }

                // add it to crawled DB
                addToCrawledDB(currentURL.getURL());

                // add neighbour URLs to be parsed later
                stackNeighbours(currentURL.getNeighbours());

                // notify one of the sleeping threads
                readQueue.notify();
                System.out.println("NOTIFIED");
                if (readQueue.queue.size() == 0) break;
            }
        }
    }

    private void addToCrawledDB(String url) {
        Document document = new Document("url", url);
        Document urlDoc = crawledUrlsCollection.find(document).first();

        // check if it's already visited, skip
        if (urlDoc != null) return;

        try {
            InsertOneResult result = crawledUrlsCollection.insertOne(document);
            notCrawledURLsCollection.findOneAndDelete(document);
            System.out.println(result.getInsertedId() + "is inserted");
        } catch (Exception e) {
            System.out.println(url);
        }
    }

    private void stackNeighbours(ArrayList<URLsQueue.urlObj> neighbours) {
        if (neighbours == null || neighbours.size() > 0)
            return;

        for (URLsQueue.urlObj url : neighbours) {
            // if it's not allowed, remove from DB and skip
            Document doc = new Document("url", url.getURL());
            if (!robotChecker.isAllowed(url.getURL())) {
                notCrawledURLsCollection.findOneAndDelete(doc);
                continue;
            }

            Document urlDoc = crawledUrlsCollection.find(doc).first();

            // if it's crawled before, remove from notCrawled DB and skip
            if (urlDoc != null) {
                notCrawledURLsCollection.findOneAndDelete(urlDoc);
                continue;
            }
            // else add it
            readQueue.queue.add(url);
            notCrawledURLsCollection.insertOne(doc);
            readQueue.URLsCount++;
        }
    }

    private org.jsoup.nodes.Document request(String url) { // Parsing HTML document
        if (url == null || url.length() == 0)
            return null;

        Connection con = Jsoup.connect(url);
        org.jsoup.nodes.Document doc = null;
        try {
            doc = con.get();
        } catch (IOException e) {
            return null;
        }
        if (con.response().statusCode() == 200) {
            System.out.println("\n**Bot ID:" + ID + " Received Webpage at " + url);
        }
        return doc;
    }
}
