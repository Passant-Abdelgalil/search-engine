import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import java.util.ArrayList;


public class URLsQueue {
    private MongoDatabase db;
    long URLsCount = 0;
    RobotChecker robotChecker;
    ArrayList<urlObj>queue;
    private MongoCollection<Document> collection;

    static class urlObj{
        String url;
        boolean visited;
        ArrayList<urlObj> neighbours;

        urlObj(String url){
            this.url = url;
            this.neighbours = new ArrayList<>();
        }
        public void updateNeighbours(urlObj childNode){this.neighbours.add(childNode);}
        public ArrayList<urlObj> getNeighbours(){return this.neighbours;}
        public void printQueue(){
            if(neighbours.size()>0){
                for(urlObj url: neighbours){
                    System.out.println(url.url);
                }
            }
        }
    }

    URLsQueue(MongoDatabase database, RobotChecker robot){
        queue = new ArrayList<urlObj>();
        this.robotChecker = robot;
        this.db = database;
        if(!db.listCollectionNames().into(new ArrayList<String>()).contains("crawled")){
                db.createCollection("crawled");
        }
        collection = db.getCollection("crawled");
        collection.createIndex(Indexes.text("url"), new IndexOptions().unique(true));
    }
}
