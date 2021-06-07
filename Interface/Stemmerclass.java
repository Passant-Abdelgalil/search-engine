
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
import java.util.ArrayList;
import java.io.*;
import java.util.stream.Stream;


public class Stemmerclass {
    
    private static Set<String> IgWords= new HashSet<String>();
    private static String sentance="pla";
    public static void main( String [] args) throws IOException {
        if(Read()){
        String Sentance= tokenizeStopStem(sentance.toLowerCase());
        System.out.println(Sentance);
        }
    
    }
    public static boolean Read() {
        try {
          File myObj = new File("test.txt");
          Scanner myReader = new Scanner(myObj);
          if (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            sentance=data;
            myReader.close();
            return true;
          }
          myReader.close();
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
        return false;
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

}