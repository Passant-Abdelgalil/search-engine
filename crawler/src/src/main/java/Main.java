import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    static URLsQueue Queue = null;
    static RobotChecker checker = null;

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);

        MongoClient mongoClient = null;
        mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("test");
        MongoCollection<Document> collection= db.getCollection("crawled");
        checker = new RobotChecker(db);
        Queue = new URLsQueue(db, checker);
        if(collection.countDocuments()>0){
            MongoCollection<Document> urlsCollection = db.getCollection("crawled");
            Document doc = urlsCollection.find().first();
            URLsQueue.urlObj url = new URLsQueue.urlObj((String) doc.get("url"));
            Queue.queue.add(url);
        }else{
            URLsQueue.urlObj url = new URLsQueue.urlObj("https://en.wikipedia.org/");
            Queue.queue.add(url);
            url = new URLsQueue.urlObj("https://stackoverflow.com");
            Queue.queue.add(url);
            url = new URLsQueue.urlObj("https://quora.com");
            Queue.queue.add(url);
            url = new URLsQueue.urlObj("https://reddit.com");
            Queue.queue.add(url);
            url = new URLsQueue.urlObj("https://geeksforgeeks.or");
            Queue.queue.add(url);
        }


        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            if(checker==null){
                return;
            }
            threads[i] = new Thread(new Crawler(Queue, i, db));
            threads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
            }
        }

    }
}
