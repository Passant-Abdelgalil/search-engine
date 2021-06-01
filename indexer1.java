import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.io.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class indexer1 {
    private static ArrayList<String> IgWords= new ArrayList<String>();

    private static void ReadFromFile() {
        LineNumberReader reader =null;
        try {
        reader = new LineNumberReader(new FileReader(new File("ignoredWords.txt")));
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

    /*private static Boolean ReadFromFile(String word) {//take care! I'm ignored!
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
    }*/
    public static void main(String[] args) throws IOException {
        

        File input = new File ("input.txt");//file having all HTML urls
        Document doc = Jsoup.parse(input, "UTF-8");
        ReadFromFile();
        String htmlUrls= doc.text();
        String[] htmlUrlsSplitted = htmlUrls.split(" ");//has URLs

        // System.out.println(doc);
        // System.out.println();
        // System.out.println(htmlUrls);
        // System.out.println();
        int i;

        for(i=0; i<htmlUrlsSplitted.length; i++)
            System.out.println(htmlUrlsSplitted[i]);

        for(i=0; i<htmlUrlsSplitted.length; i++)
        {
            // System.out.println(htmlUrlsSplitted[i]);
            // Document docForEachHtmlURL=null;
            // docForEachHtmlURL =Jsoup.parse(new File (htmlUrlsSplitted[i]), "UTF-8");//if it's html documents
            Document docForEachHtmlURL=Jsoup.connect(htmlUrlsSplitted[i]).get();
            // System.out.println(docForEachHtmlURL);
            // String p= docForEachHtmlURL.getElementsByTag("p").get(0).text();//if it's html documents
            // String[] sentenceWords = p.split(" ");//if it's html documents

            String[] GetNumbOfWords=docForEachHtmlURL.text().split(" ");
            System.out.println(GetNumbOfWords.length);

            Elements elements = docForEachHtmlURL.select("*");
            for(Element e : elements)
                if(e.childrenSize()==0 && e.tagName() != "body" && e.tagName() !="#root" && e.tagName() !="html"&& !(e.text().equals("")) && e.tagName() !="style" && e.tagName() !="script")
                {
                    System.out.println( e.tagName() + ": " + e.text());
                    String[] sentenceWords = e.text().split(" ");
                    
                    for(int j=0; j<sentenceWords.length; j++){//remove dot
                        // System.out.println(sentenceWords[i]);
                        if(sentenceWords[j].charAt(sentenceWords[j].length() - 1) == '.')
                            sentenceWords[j]=sentenceWords[j].substring(0, (sentenceWords[j].length() - 1));
                        if(sentenceWords[j].charAt(sentenceWords[j].length() - 1) == '?')
                            sentenceWords[j]=sentenceWords[j].substring(0, (sentenceWords[j].length() - 1));
                            
                    }
                    for (String wordInSentence : sentenceWords) {
                        //Boolean IsignoredWord= ReadFromFile(wordInSentence.toLowerCase());//don't take the ignored words
                        
                        if ( !(IgWords.contains(wordInSentence) )) 
                            System.out.println(wordInSentence);
                    }
                    
                    
                    // System.out.println(htmlUrlsSplitted.length);
                }
                System.out.println("------------------");
                    

        }
        
    }
}



