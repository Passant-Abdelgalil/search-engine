import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotChecker {
    private MongoDatabase db;
    private MongoCollection<Document> rulesCollection;

    static class HostData {
        boolean checked;
        ArrayList<String> disAllowedURLs;

        HostData() {
            checked = false;
            disAllowedURLs = new ArrayList<>();
        }
    }

    private HashMap<String, HostData> robotRules;

    RobotChecker(MongoDatabase database) {
        robotRules = new HashMap<String, HostData>();
        db = database;
        if (!db.listCollectionNames().into(new ArrayList<String>()).contains("RobotRules")) {
            db.createCollection("RobotRules");
        }
        rulesCollection = db.getCollection("RobotRules");
        rulesCollection.createIndex(Indexes.text("host"), new IndexOptions().unique(true));
    }

    public boolean isAllowed(String urlString) {
        String host = "";
        String protocol = "";
        try {
            URL url = new URL(urlString);
            host = url.getHost();
            protocol = url.getProtocol();
            host = host.replaceAll("www.", "");

        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (host.equals("")) return false;
        Document document = rulesCollection.find(new Document("host", host)).first();
        if (document == null) {
            try {
                getRules(urlString);
            } catch (Exception e) {
                return false;
            }
        }
        document = rulesCollection.find(new Document("host", host)).first();
        if (document != null) {

            ArrayList<String> rules = (ArrayList<String>) document.get("rules");
            if (rules == null) return document.get("checked", boolean.class);
            for (Object pattern : rules) {
                Pattern p = Pattern.compile(pattern.toString());
                Matcher m = p.matcher(urlString);
                if (m.matches()) { // if the url contains a disallowed directory >> return false
                    return false;
                }
            }
        }
        return true;
    }

    public void getRules(String urlString) throws IOException {
        URL url = new URL(urlString);
        String hostName = url.getHost(); // stackoverflow.com
        hostName = hostName.replace("www.", "");
        if (isChecked(hostName)) return;
        String protocol = url.getProtocol(); // https: http

        url = new URL(protocol + "://" + hostName + "/robots.txt");

        Reader streamReader = connect(url);
        if (streamReader == null) {
            Document document = new Document("host", hostName).append("checked", true).append("rules", new ArrayList<String>());
            InsertOneResult result = rulesCollection.insertOne(document);
            System.out.println(result.getInsertedId() + " is inserted");
            return;
        }
        BufferedReader reader = new BufferedReader(streamReader);

        boolean user_agent = false;
        String inputLine;
        HostData hostData = new HostData();

        while ((inputLine = reader.readLine()) != null) {
            // we are only interested in 'User-Agent: *' line
            if (inputLine.toLowerCase().startsWith("user-agent")) { // if user-agent line
                if (inputLine.contains(": *")) { // if the rules are for all crawlers
                    user_agent = true; // set the flag to start parsing rules
                } else if (user_agent) { // if it's for a specific crawler & we read '*', then no need to keep
                    break;
                }
            }
            if (user_agent && inputLine.toLowerCase().startsWith("disallow")) {
                String directory = inputLine.substring(inputLine.indexOf(":") + 2);
                directory = processPatterns(directory);
                hostData.disAllowedURLs.add(directory);
            }
        }
        hostData.checked = true;
        Document document = new Document("host", hostName).append("checked", true).append("rules", hostData.disAllowedURLs);
        InsertOneResult result =  rulesCollection.insertOne(document);
        System.out.println(result.getInsertedId() + " is inserted");
    }

    private String processPatterns(String pattern) {
        pattern = pattern.replaceAll("/$", "");
        pattern = pattern.replaceAll("\\*", ".*"); // replace '*' with '.*' to meet any char any number of times
        return ".*" + pattern + ".*"; // wrap the directory with .* to search for it anywhere in the url
    }

    public boolean isChecked(String hostName) {
        Document doc = rulesCollection.find(new Document("host", hostName)).first();
        return doc != null && doc.get("checked", boolean.class);
    }

    private Reader connect(URL url) throws IOException {
        // open connection to send requests
        HttpURLConnection connection;

        // sending the GET request to /robots.txt

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // reading the response
        int status = connection.getResponseCode();
        if (status > 399) {
            System.out.println("Request Failed! " + url.toString());
            return null;
        }
        return new InputStreamReader(connection.getInputStream());
    }

    public static void main(String... args) {
        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("test");
        RobotChecker checker = new RobotChecker(db);
        checker.isAllowed("https://stats.wikimedia.org/robots.txt");

    }
}
