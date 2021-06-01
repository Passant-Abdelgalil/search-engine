import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.Arrays.asList;
import com.mongodb.*;

public class MongoDB {
    public static MongoClient mongoClient;
    public static DB database;
    public static DBCollection test;
    public static void main( String [] args) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient("localhost",27017);
    database = mongoClient.getDB ( "MongoDBTutorial");
    test=database.getCollection("test");
    //insertDB("RaghodaaaKhaled","WW.ra","plapla","a",test);
      //  List<Integer> books = Arrays.asList(27464, 747854);

  /*  WordObj wordobj=new WordObj();
    wordobj.setWord("Raghad");
    wordobj.setSentance("Plapla");
    wordobj.setURL("ww.raghad.com");
    test.insert(Convert(wordobj));*/
    //DBObject quary= new BasicDBObject("Word","RaghodaaaKhaled");
    //DBCursor cursor= test.find(quary);
    //System.out.println(cursor.one());
        if(WordExistsAndurl("RaghodaaaKhaled","WW.ra",test))
            System.out.println("Yeeeeeeeeees");
        else
        System.out.println("Noooooooo");
        //insertURL("RaghodaaaKhaled","R.Com","Hi","div",test);
        insertsentance("RaghodaaaKhaled","R.Com","Alhmd","tr",test);
    }
    public static void insertDB(String word,String URL,String Sentance ,String Tag,DBCollection t){
        DBObject WordFrist = new BasicDBObject("Word", word)
                .append("Pages",  asList(new BasicDBObject("URL", URL).append("Sentance", asList(Sentance)).append("tag",asList(Tag))
                        ));
        t.insert(WordFrist);
    }
    public static boolean WordExists(String Word, DBCollection t) {
        DBObject quary= new BasicDBObject("Word",Word);
        DBCursor cursor= t.find(quary);
        return cursor.one() !=null;
    }
    public static boolean WordExistsAndurl(String Word,String Url, DBCollection t) {
        //DBObject quary= new BasicDBObject("Word",Word).append("scores",  asList(new BasicDBObject("URL", Url)));
        BasicDBObject quary = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        obj.add(new BasicDBObject("Word", Word));
        obj.add(new BasicDBObject("pages.URL", Url) );
        quary.put("$and", obj);
        System.out.println(quary.toString());
        DBCursor cursor= t.find(quary);
        return cursor.one() !=null;
    }
    public static void insertURL(String Word,String Url,String Sentance,String tag, DBCollection t){
        //DBObject WordFrist = new BasicDBObject("URL", Url).append("Sentance", asList(Sentance)).append("tag",asList(tag));
        BasicDBObject Wordobj=new BasicDBObject("Word", Word);
        BasicDBObject commentObject = new BasicDBObject();
        commentObject.put("URL", Url);
        commentObject.put("Sentance", asList(Sentance));
        commentObject.put("tag",asList(tag));
        t.update(Wordobj, new BasicDBObject(
                        "$push", new BasicDBObject("scores", commentObject)), false,
                false);
    }

     public static void insertsentance(String Word,String Url,String Sentance,String tag, DBCollection t){
        // BasicDBObject cmd = new BasicDBObject().append("$push", new  BasicDBObject("scores.Sentance", Sentance));
        // t.update(new BasicDBObject().append("Word", Word).append("URL",Url), cmd);
         BasicDBObject quary = new BasicDBObject();
         List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
         obj.add(new BasicDBObject("Word", Word));
         obj.add(new BasicDBObject("scores.URL", Url) );
         quary.put("$and", obj);
         BasicDBObject updatequ=new BasicDBObject();
         updatequ.put("$addToSet",new BasicDBObject("scores.$.Sentance", Sentance));
         t.update(quary,updatequ);
         updatequ.put("$addToSet",new BasicDBObject("scores.$.tag", tag));
         System.out.println(updatequ.toString());
         t.update(quary,updatequ);
         /*BasicDBObject f1=new BasicDBObject();
         f1.put("$elemMatch",new BasicDBObject("URL",Url));
         BasicDBObject f2 = new BasicDBObject("Word", Word)
                 .append("scores",f1);
         System.out.println(f2.toString());*/
         //t.update({"Word":1,"StudentOtherDetails":{"$elemMatch":{"StudentName":"David"}}},
         //{"$push":{"StudentOtherDetails.$.StudentFriendName":"James"}});

       /*  t.update(quary, new BasicDBObject(
                         "$addToSet", new BasicDBObject("Sentance", Sentance)), false,
                 false);
         t.update(quary, new BasicDBObject(
                         "$addToSet", new BasicDBObject("tag", tag)), false,
                 false);*/
     }


}