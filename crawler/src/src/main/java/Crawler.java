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
    private boolean stopCrawling;
    URLsQueue readQueue;
    boolean wasAsleep = false;
    private URLNormalizer normalizer = new URLNormalizer();
    private MongoDatabase db;
    private MongoCollection<org.bson.Document> urlsCollection;
    private MongoCollection<org.bson.Document> notCrawledURLsCollection;

    public Crawler(URLsQueue Queue, int id, MongoDatabase database) {
        this.ID = id;
        this.readQueue = Queue;
        db = database;
        if (!db.listCollectionNames().into(new ArrayList<String>()).contains("crawled")) {
            database.createCollection("crawled", null);
        }
        if (!db.listCollectionNames().into(new ArrayList<String>()).contains("notCrawled")) {
            database.createCollection("notCrawled", null);
        }
        urlsCollection = database.getCollection("crawled");
        notCrawledURLsCollection = database.getCollection("notCrawled");
    }

    @Override
    public void run() {
        crawl();
    }

    private void crawl() {
        while (true) {
            URLsQueue.urlObj currentURL = null;
            org.jsoup.nodes.Document doc = null;
            synchronized (readQueue) {
                if (readQueue.URLsCount >= 5000) {
                    break;
                } else {
                    currentURL = (readQueue.queue.size() == 0) ? null : readQueue.queue.remove(0);
                }
                if (currentURL == null) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " sill sleep");
                        readQueue.wait();
                    } catch (InterruptedException e) {
                    }
                    currentURL = (readQueue.queue.size() == 0) ? null : readQueue.queue.remove(0);
                    if (currentURL == null) {
                        System.out.println(Thread.currentThread().getName() + " will close");
                        break;
                    }
                    doc = request(currentURL.url);
                }
            }
            if (!stopCrawling) {
                if (currentURL != null)
                    doc = request(currentURL.url);
                if (doc == null) {
                    continue;
                }
                if (doc.documentType() != null && !doc.documentType().name().equals("html")) {
                    continue;
                }
                System.out.println(Thread.currentThread().getName() + " -> " + currentURL.url);
                for (Element link : doc.select("a[href]")) {
                    String parsedLink = link.absUrl("href");
                    parsedLink = normalizer.normalizePreservedSemantics(parsedLink);
                    parsedLink = normalizer.normalizeSemantics(parsedLink);
                    Document document = new Document("url", parsedLink);
                    notCrawledURLsCollection.insertOne(document);
                    currentURL.updateNeighbours(new URLsQueue.urlObj(parsedLink));
                }
                synchronized (readQueue) {
                    String normalizedURI;
                    if (readQueue.robotChecker.isAllowed(currentURL.url)) {
                        normalizedURI = normalizer.normalizePreservedSemantics(currentURL.url);
                        normalizedURI = normalizer.normalizeSemantics(normalizedURI);

                        Document urlDoc = urlsCollection.find(new Document("url", normalizedURI)).first();
                        if (urlDoc == null) {
                            if (urlsCollection.countDocuments() > 5000) {
                                break;
                            }
                            Document document = new Document("url", normalizedURI);
                            try {
                                InsertOneResult result = urlsCollection.insertOne(document);
                                notCrawledURLsCollection.findOneAndDelete(document);
                                System.out.println(result.getInsertedId() + "is inserted");

                            } catch (Exception e) {
                                System.out.println(normalizedURI);
                            }
                        }
                    }
                    ArrayList<URLsQueue.urlObj> neighbours = currentURL.getNeighbours();
                    if (neighbours != null && neighbours.size() > 0) {

                        for (URLsQueue.urlObj url : neighbours) {
                            if (readQueue.robotChecker.isAllowed(url.url)) {
                                Document urlDoc = urlsCollection.find(new Document("url", url.url)).first();
                                if (urlDoc == null) {
                                    readQueue.queue.add(url);
                                    notCrawledURLsCollection.insertOne(new Document("url", url.url));
                                    readQueue.URLsCount++;
                                } else {
                                    notCrawledURLsCollection.findOneAndDelete(urlDoc);
                                }
                            } else {
                                notCrawledURLsCollection.findOneAndDelete(new Document("url", url.url));
                            }
                        }
                    }
                    readQueue.notifyAll();
                    System.out.println("NOTIFIED");
                    if (readQueue.queue.size() == 0) break;
                }
            } else {
                break;
            }
        }
    }

    private org.jsoup.nodes.Document request(String url) { // Parsing HTML document
        try {
            Connection con = null;
            if (url != null && url.length() != 0) {
                con = Jsoup.connect(url);
                org.jsoup.nodes.Document doc = con.get();
                if (con.response().statusCode() == 200) {
                    System.out.println("\n**Bot ID:" + ID + " Received Webpage at " + url);
                    String title = doc.title();
                    System.out.println(title);
                    return doc;
                }
                return doc;
            }
            return null;
        } catch (IOException e) {
            return null;
        }

    }
}
