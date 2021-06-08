import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;
import static java.util.Arrays.asList;
import com.mongodb.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.io.*;
import java.util.stream.Stream;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MongoDB {
    private static Set<String> IgWords= new HashSet<String>();
    public static MongoClient mongoClient;
    public static DB database;
    public static DBCollection test;
    public static void main( String [] args) throws IOException {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDB("Indexer");
        test = database.getCollection("test7");
        //Read the Ignore Words from the file to be stopped
        ReadFromFile();
        //get the path of the folder where is all the URLs
        Stream<String> htmlUrlsSplitted = Files.lines(Paths.get("C:\\Users\\Raghod\\IdeaProjects\\indexer\\src\\main\\java\\input.txt"));
        String[] URLarry = htmlUrlsSplitted.toArray(String[]::new);
        // loop to each URL
       for (int i = 0; i < URLarry.length; i++) {
            String URL=URLarry[i];
            Document docForEachHtmlURL = Jsoup.connect(URLarry[i]).get();
            String title=docForEachHtmlURL.title();
            //split the text to get the length of document
            String[] GetNumbOfWords = docForEachHtmlURL.text().split(" ");
            int Long =GetNumbOfWords.length;
            Elements elements = docForEachHtmlURL.select("*");
            // loop to every Sentence in document
            for (Element e : elements)
                if (e.childrenSize() == 0 && e.tagName() != "body" && e.tagName() != "#root" && e.tagName() != "html" && !(e.text().equals("")) && e.tagName() != "style" && e.tagName() != "script") {
                    //get each Sentence and Stemming it
                    String TAG=e.tagName();
                    String  sentance=e.text();
                    String Sentance= tokenizeStopStem(sentance.toLowerCase());
                    String[] sentenceWords = Sentance.split(" ");
                    //loop to each Word in the sentence and inset it in the database with its tag and Sentence
                    for (String wordInSentence : sentenceWords) {
                            //if we have the word and url before -> if we have the word before -> it is new word
                            if(WordExists(wordInSentence,test) && WordExistsAndurl(wordInSentence,URL,test)){
                                insertsentance(wordInSentence,URL,sentance,TAG,Long,test);
                            }else if(WordExists(wordInSentence,test)){
                                insertURL(wordInSentence,title,URL,sentance,TAG,Long,test);
                            }else{
                                insertDB(wordInSentence,title,URL,sentance,TAG,Long,test);
                            }


                    }
                }

            System.out.println("------------------");
           }

    }

    private static void ReadFromFile() {
        LineNumberReader reader =null;
        try {
            reader = new LineNumberReader(new FileReader(new File("C:\\Users\\Raghod\\IdeaProjects\\indexer\\src\\main\\java\\ignoredWords.txt")));
            String str;
            while ((str=reader.readLine()) != null) //read file till the end
            {
                IgWords.add(str);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String tokenizeStopStem(String input) {

        TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_35, new StringReader(input));
        tokenStream = new StopFilter(Version.LUCENE_35, tokenStream, IgWords);
        tokenStream = new PorterStemFilter(tokenStream);

        StringBuilder sb = new StringBuilder();
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttr = tokenStream.getAttribute(CharTermAttribute.class);
        try{
            while (tokenStream.incrementToken()) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(charTermAttr.toString());
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    public static void insertDB(String word,String title,String URL,String Sentance ,String Tag,int Long,DBCollection t){
        DBObject WordFrist = new BasicDBObject("Word", word).append("IDF",.0002)
                .append("pages",  asList(new BasicDBObject("title", title).append("URL",URL).append("Sentance", asList(Sentance)).append("tag",asList(Tag)).append("TF",1.0/Long)
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
       // System.out.println(quary.toString());
        DBCursor cursor= t.find(quary);
        return cursor.one() !=null;
    }

    public static void insertURL(String Word,String title,String Url,String Sentance,String tag,int Long ,DBCollection t){
        //DBObject WordFrist = new BasicDBObject("URL", Url).append("Sentance", asList(Sentance)).append("tag",asList(tag));
        BasicDBObject Wordobj=new BasicDBObject("Word", Word);
        BasicDBObject commentObject = new BasicDBObject();
        commentObject.put("title", title);
        commentObject.put("URL", Url);
        commentObject.put("Sentance", asList(Sentance));
        commentObject.put("tag",asList(tag));
        commentObject.put("TF",1.0/Long);
        t.update(Wordobj, new BasicDBObject(
                        "$push", new BasicDBObject("pages", commentObject)), false,
                false);
        t.update(Wordobj, new BasicDBObject("$inc",new BasicDBObject("IDF",.0002)));
    }

     public static void insertsentance(String Word,String Url,String Sentance,String tag,int Long, DBCollection t){

         BasicDBObject quary = new BasicDBObject();
         List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
         obj.add(new BasicDBObject("Word", Word));
         obj.add(new BasicDBObject("pages.URL", Url) );
         quary.put("$and", obj);
         BasicDBObject updatequ=new BasicDBObject();
         updatequ.put("$addToSet",new BasicDBObject("pages.$.Sentance", Sentance));
         t.update(quary,updatequ);
         updatequ.put("$addToSet",new BasicDBObject("pages.$.tag", tag));
        // System.out.println(updatequ.toString());
         t.update(quary,updatequ);
        // BasicDBObject Wordobj=new BasicDBObject("Word", Word);
         t.update(quary, new BasicDBObject("$inc",new BasicDBObject("pages.$.TF",1.0/Long)));

     }

}