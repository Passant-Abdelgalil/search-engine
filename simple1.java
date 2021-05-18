import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;

public class simple1 {

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
        String html= "<html><head><title> first parser </title></head> <body>  <h1> Hi </h1><p>paragraph 1. I'm reem I hope you are fine.</p></body></html>";
        Document doc =Jsoup.parse(html);
        
        String p= doc.getElementsByTag("p").get(0).text();
        String[] sentenceWords = p.split(" ");

        for(int i=0; i<sentenceWords.length; i++){//remove dot
            // System.out.println(sentenceWords);
            if(sentenceWords[i].charAt(sentenceWords[i].length() - 1) == '.')
                sentenceWords[i]=sentenceWords[i].substring(0, (sentenceWords[i].length() - 1));
                
        }
        
        for (String wordInSentence : sentenceWords) {
            Boolean IsignoredWord= ReadFromFile(wordInSentence.toLowerCase());//don't take the ignored words
            
            if ( IsignoredWord ) 
                System.out.println(wordInSentence);
        }
    }
}
