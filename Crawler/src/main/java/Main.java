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
        // logger
        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);

        // create database client to be shared among threads
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("test");

        // create the shared data structure to store urls
        Queue = new URLsQueue(db);

        // change this value as you wish, make sure it's < 10
        int numThreads = 4;

        // create threads array to manage threads
        Thread[] threads = new Thread[numThreads];

        // initialize threads and run them
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new Crawler(Queue, i, db, new RobotChecker(db)));
            threads[i].start();
        }


        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
                // TODO: check for running threads, if > 1 restart this thread
            } catch (InterruptedException ignored) {
            }
        }

    }
}
