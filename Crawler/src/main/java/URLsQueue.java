import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.print.Doc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class URLsQueue {
    private MongoDatabase db;
    long URLsCount = 0;
    ArrayList<urlObj> queue;
    private MongoCollection<org.bson.Document> crawledUrlsCollection;
    private MongoCollection<org.bson.Document> notCrawledURLsCollection;

    static class urlObj {
        private String url;
        private ArrayList<urlObj> neighbours;

        urlObj(String url) {
            this.url = url;
            this.neighbours = new ArrayList<>();
        }

        public void updateNeighbours(urlObj childNode) {
            this.neighbours.add(childNode);
        }

        public ArrayList<urlObj> getNeighbours() {
            return this.neighbours;
        }

        public String getURL() {
            return url;
        }
        public void setURL(String url){
            this.url = url;
        }
    }

    /**
     * Class Constructor that initialize the queue and assign values to
     * DB client and DB collections
     *
     * @param database: MongoDB client that's shared among all the threads, used to get the collections
     */
    URLsQueue(MongoDatabase database) {
        queue = new ArrayList<urlObj>();
        this.db = database;
        if (!db.listCollectionNames().into(new ArrayList<String>()).contains("crawled")) {
            db.createCollection("crawled");
        }
        crawledUrlsCollection = db.getCollection("crawled");
        notCrawledURLsCollection = db.getCollection("notCrawled");
        initializeQueue();
    }

    /**
     * This function check if there is any urls not crawled from previous runs
     * it fills the queue with these urls
     *
     * @return boolean flag set to true on successful insertion from DB, false otherwise
     */
    private boolean fillQueueFromDB() {
        // if no urls in the Collection, return false
        long documentsNum = notCrawledURLsCollection.countDocuments();
        if (documentsNum == 0) return false;

        // else, get the urls
        ArrayList<Document> array = new ArrayList<Document>();
        array = notCrawledURLsCollection.find().into(array);

        // add to the queue only 5 urls or less if no enough urls in the DB
        for (int i = 0; i < Math.min(5, array.size()); i++) {
            // build url object and add it to the queue
            URLsQueue.urlObj url = new URLsQueue.urlObj((String) array.get(i).get("url"));
            queue.add(url);
        }
        // return true on successful insertion
        return true;
    }

    /**
     * This function fills the queue from seeds.txt file if exists
     * otherwise it calls createFileAndFillQueue
     *
     * @param file : File object for `seeds.txt` file
     */
    private void fillQueueFromFile(File file) {
        Scanner reader = null;
        // try to read the file if exists
        try {
            reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String urlString = reader.nextLine();
                URLsQueue.urlObj url = new URLsQueue.urlObj(urlString);
                queue.add(url);
            }
        } catch (FileNotFoundException e) {
            // if file doesn't exist, create it for future runs
            try {
                createFileAndFillQueue(file);
            } catch (IOException exc) {
            }
        }
    }

    /**
     * This function creates `seeds.txt` file and fill it with seeds for future runs
     * @param file: File object for `seeds.txt`
     * @throws IOException: This function throws Exception if something wrong happened on
     * createNewFile fucntion call
     */
    private void createFileAndFillQueue(File file) throws IOException {
        if (file.createNewFile()) {
            FileWriter writer = new FileWriter("seeds.txt");
            writer.write("https://en.wikipedia.org/");
            writer.write("https://stackoverflow.com");
            writer.write("https://quora.com");
            writer.write("https://reddit.com");
            writer.write("https://geeksforgeeks.or");
            writer.close();
            fillQueueFromFile(file);
        }
    }

    private void initializeQueue() {
        // if there are previous un-crawled urls, get one
        boolean filled = fillQueueFromDB();
        File file = new File("seeds.txt");

        // if no urls in DB, read from seeds.txt file
        if (!filled)
            fillQueueFromFile(file);
    }

}
