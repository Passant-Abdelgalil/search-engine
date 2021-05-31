import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class simple2 {

    private static Boolean ReadFromFile(String word) {//take care! I'm ignored!
    LineNumberReader reader =null;
    try {
        reader = new LineNumberReader(new FileReader(new File("ignoredWords.txt")));
        String str;
        while ((str=reader.readLine()) != null) //read file till the end
        {
            if(str.equals(word))
            {
                if(reader != null){
                    try {
                      reader.close();
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                }
                return false;
            }
        }
        
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    if(reader != null){
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    return true;
    }
    public static void main(String[] args) throws IOException {
        //String html= "<html><head><title> first parser </title></head> <body>  <h1> Hi </h1><p>paragraph 1. I'm reem I hope you are fine.</p></body></html>";
        
        // String title= "";
        // try {
            Document doc=Jsoup.connect("http://example.com/").get();
            // System.out.println(doc.body().text());
            // System.out.println(doc.body().tagName());
            // System.out.println(doc.wholeText());

            Elements elements = doc.select("*");
            for(Element e : elements)
                if( e.tagName() != "body" && e.tagName() !="#root" && e.tagName() !="html" && e.tagName() !="meta" && e.tagName() !="style")
                    System.out.println( e.tagName() + ": " + e.text());

            // System.out.println(doc.getElementsContainingOwnText("illustrative"));
            // System.out.println(doc.tagName("body"));

        //System.out.println("Jsoup Can read HTML page from URL, title : " + title);
           
}
}
