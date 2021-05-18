import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;

public class indexer1 {

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
        

        File input = new File ("input.html");//file having all HTML documents
        Document doc = Jsoup.parse(input, "UTF-8");
        
        String htmlUrls= doc.getElementsByTag("body").get(0).text();
        String[] htmlUrlsSplitted = htmlUrls.split(" ");//has URLs

        // System.out.println(doc);
        // System.out.println();
        // System.out.println(htmlUrls);
        // System.out.println();
        int i;

        // for(i=0; i<htmlUrlsSplitted.length; i++)
            // System.out.println(htmlUrlsSplitted[i]);

        for(i=0; i<htmlUrlsSplitted.length; i++)
        {
            // System.out.println(htmlUrlsSplitted[i]);
            Document docForEachHtmlURL=null;
            docForEachHtmlURL =Jsoup.parse(new File (htmlUrlsSplitted[i]), "UTF-8");
            // System.out.println(docForEachHtmlURL);
            String p= docForEachHtmlURL.getElementsByTag("p").get(0).text();
            String[] sentenceWords = p.split(" ");
            for(int j=0; j<sentenceWords.length; j++){//remove dot
                // System.out.println(sentenceWords[i]);
                if(sentenceWords[j].charAt(sentenceWords[j].length() - 1) == '.')
                    sentenceWords[j]=sentenceWords[j].substring(0, (sentenceWords[j].length() - 1));
                    
            }
            for (String wordInSentence : sentenceWords) {
                Boolean IsignoredWord= ReadFromFile(wordInSentence.toLowerCase());//don't take the ignored words
                
                if ( IsignoredWord ) 
                    System.out.println(wordInSentence);
            }
            System.out.println("------------------");
            // System.out.println(htmlUrlsSplitted.length);
        }
         
        
        
        
    }
}



