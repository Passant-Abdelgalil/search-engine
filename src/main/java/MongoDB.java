import java.net.UnknownHostException;
import com.mongodb.*;

public class MongoDB {
    public static MongoClient mongoClient;
    public static DB database;
    public static DBCollection test;
    public static void main( String [] args) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient("localhost",27017);
    database = mongoClient.getDB ( "MongoDBTutorial");
    test=database.getCollection("test");

       /* DBObject person = new BasicDBObject("_id", "jo")
                .append("name", "Jo Bloggs")
                .append("address", new BasicDBObject("street", "123 Fake St")
                        .append("city", "Faketon")
                        .append("state", "MA")
                        .append("zip", 12345));
    test.insert(person);*/
    WordObj wordobj=new WordObj();
    wordobj.setWord("Raghad");
    wordobj.setSentance("Plapla");
    wordobj.setURL("ww.raghad.com");
    test.insert(Convert(wordobj));
    DBObject quary= new BasicDBObject("Word","Raghad");
    DBCursor cursor= test.find(quary);
    System.out.println(cursor.one());
    }

    public static DBObject Convert (WordObj word){
        return new BasicDBObject("Word",word.getWord()).append("URL",word.getURL()).append("Sentance",word.getSentance());
    }
}