import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class simple2 {

    public static void main(String[] args) throws IOException {
        //String html= "<html><head><title> first parser </title></head> <body>  <h1> Hi </h1><p>paragraph 1. I'm reem I hope you are fine.</p></body></html>";
        
        // String title= "";
        // try {
            Document doc=Jsoup.connect("http://example.com/").get();
            // System.out.println(doc.body().text());
            // System.out.println(doc.body().tagName());
            // System.out.println(doc.wholeText());
            String[] GetNumbOfWords=doc.text().split(" ");
            System.out.println(GetNumbOfWords.length);
            // System.out.println(doc.text());

            Elements elements = doc.select("*");
            for(Element e : elements)
                if( e.tagName() != "body" && e.tagName() !="#root" && e.tagName() !="html" && e.tagName() !="style")
                    System.out.println( e.tagName() + ": " + e.text());

            // System.out.println(doc.getElementsContainingOwnText("illustrative"));
            // System.out.println(doc.tagName("body"));

        //System.out.println("Jsoup Can read HTML page from URL, title : " + title);
           
}
}
