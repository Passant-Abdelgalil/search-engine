public class WordObj {
    private String Word;
    private String URL;
    private String Sentance;

    public void setWord(String word) {
        Word = word;
    }

    public void setSentance(String sentance) {
        Sentance = sentance;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getSentance() {
        return Sentance;
    }

    public String getURL() {
        return URL;
    }

    public String getWord() {
        return Word;
    }
}
