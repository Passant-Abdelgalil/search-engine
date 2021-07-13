import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

import javax.print.Doc;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

        ArrayList<String> allowedURLs;

        HostData() {
            checked = false;
            disAllowedURLs = new ArrayList<String>();
            allowedURLs = new ArrayList<String>();
        }
    }

    private HashMap<String, HostData> robotRules;

    /**
     * Class Constructor that initialize the rules map and DB client
     *
     * @param database: mongodb client to get the collection to store extracted rules
     */
    RobotChecker(MongoDatabase database) {
        robotRules = new HashMap<String, HostData>();
        db = database;

        // if no RobotRules connection, create one
        if (!db.listCollectionNames().into(new ArrayList<String>()).contains("RobotRules")) {
            db.createCollection("RobotRules");
        }

        rulesCollection = db.getCollection("RobotRules");
    }

    /**
     * This function returns robots rules for the passed url host from the DB if it exists
     * first it extracts the hostname from the passed url string, then get its rules from DB
     * if exists, else it calls getRules to request and parse the rules to be used
     *
     * @param urlString: string representation of the url to retrieve its host rules from DB
     * @return mongodb document contains the rules for this host if exists, or null
     * @throws Exception: this function can throw MalformedURLException if urlString is malformed
     *                    or IOException that's thrown by getRules function
     */
    private Document getRulesFromDB(String urlString) throws Exception {
        // extract host from url string
        URL url = new URL(urlString);
        String host = url.getHost();
        host = host.replace("www.", "");
        if (host.equals("")) return null;

        // create mongodb document to use it in queries
        Document hostDoc = new Document("host", host);

        // check if the rules for this host is already parsed
        Document document = rulesCollection.find(hostDoc).first();
        if (document != null)
            return document;

        // else, get rules for this host
        getRules(urlString);
        // return it
        return rulesCollection.find(hostDoc).first();
    }

    /**
     * This function check whether the passed url is allowed to be requested by spyders
     * it calls getRulesFromDB and if any rule was found for this host
     * it tries to match the url to one of the patterns retrieved from the DB
     *
     * @param urlString: string representation of the url to check if it's allowed to request
     * @return boolean set to true if the url is allowed, false otherwise
     */
    public boolean isAllowed(String urlString) {
        Document document = null;

        // retrieve from DB the rules for this host
        try {
            document = getRulesFromDB(urlString);
        } catch (Exception e) {
            // if something wrong happened, it's more safe to return false
            return false;
        }

        if (document != null) {
            // get rules of this host
            ArrayList<String> disAllowedRules = document.get("disallowedRules", ArrayList.class);
            ArrayList<String> allowedRules = document.get("allowedRules", ArrayList.class);

            // if no rules are found, return checked flag value
            if (disAllowedRules == null && allowedRules == null) return document.get("checked", boolean.class);

            // else, check the rules
            boolean checkDisallowed = false;
            if (allowedRules != null)
                checkDisallowed = checkRules(allowedRules, urlString);

            // if the route was explicitly found in allowed routes, return true
            if(checkDisallowed) return true;
            // else check if it's disallowed
            if (disAllowedRules != null)
                return checkRules(disAllowedRules, urlString);

        }
        return true;
    }

    private boolean checkRules(ArrayList<String> rules, String urlString) {
        for (Object pattern : rules) {
            Pattern p = Pattern.compile(pattern.toString());
            Matcher m = p.matcher(urlString);
            if (m.matches()) // if the url contains a disallowed directory >> return false
                return false;
        }
        return true;
    }

    /**
     * This function gets rules for the hostname of the passed url
     * it builds the URI for robots.txt and request it, if it exists it parse the .txt
     * by calling parseRules function and insert the returned rules in the DB
     * if the request failed, then no robots.txt for this host
     * the hostname is stored with empty rules list in the DB
     *
     * @param urlString: string representation of the URI to get its host robots rules
     * @throws Exception: This function throws IOException if parseRules throws an Exception or
     * General Exception if connect function throws an Exception (refer to connect function for further information)
     */
    public void getRules(String urlString) throws Exception {
        // extract host from url string
        URL url = new URL(urlString);
        String hostName = url.getHost(); // stackoverflow.com
        hostName = hostName.replace("www.", "");

        if (isChecked(hostName)) return;

        String protocol = url.getProtocol(); // https: or http:

        // connect to robots.txt URI to start parsing the rules
        Reader streamReader = connect(new URL(protocol + "://" + hostName + "/robots.txt"));

        // if connection failed, there's no robots.txt for this host
        // mark this hostname as visited with no rules
        if (streamReader == null) {
            Document document = new Document("host", hostName).append("checked", true).append("rules", new ArrayList<String>());
            rulesCollection.insertOne(document);
            return;
        }
        // else, read robots.txt line by line and extract disallowed routes
        BufferedReader reader = new BufferedReader(streamReader);
        // parse robots.txt and extract rules
        HostData hostRules = parseRules(reader);

        // add rules to DB
        Document document = new Document("host", hostName).append("checked", true).append("disallowedRules", hostRules.disAllowedURLs).append("allowedRules", hostRules.allowedURLs);
        InsertOneResult result = rulesCollection.insertOne(document);
    }

    /**
     * This function reads robots.txt line by line and extract allowed & disallowed routes
     * associated with `user-agent:*` and returns HostData object that stores them
     *
     * @param reader: Buffer reader for robots.txt document to be able to read & parse it
     * @return HostData object, a class to store host data {allowed/disallowed rules, checked flag}
     * @throws IOException: This function throws IOException if something happens while reading the document
     */
    private HostData parseRules(BufferedReader reader) throws IOException {
        String inputLine;               // to store rule line
        boolean startParsing = false;     // to mark the start of the rules
        HostData hostData = new HostData();

        while ((inputLine = reader.readLine()) != null) {
            if (inputLine.toLowerCase().startsWith("user-agent")) { // if user-agent line
                // we are only interested in `User-Agent: *` line
                if (inputLine.contains(": *")) // if the rules are for all crawlers
                    startParsing = true; // set the flag to start parsing rules
                else if (startParsing) // if it's for a specific crawler & we read 'User-Agent: *'
                    break;          // then no need to keep reading further
            }
            if (startParsing && inputLine.toLowerCase().startsWith("disallow")) {
                // get disallowed route and process it before inserting
                String directory = processPatterns(inputLine);
                // insert the regex in the list
                hostData.disAllowedURLs.add(directory);
            } else if (startParsing && inputLine.toLowerCase().startsWith("allow")) {
                // get allowed route and process it before inserting
                String directory = processPatterns(inputLine);
                // insert the regex in the list
                hostData.allowedURLs.add(directory);
            }
        }
        hostData.checked = true;
        return hostData;
    }

    /**
     * This function extracts the route from the rule line
     * and process it to be used as a valid regex later
     *
     * @param ruleLine: the rule line to be parsed
     * @return String representing the final valid regex
     */
    private String processPatterns(String ruleLine) {
        ruleLine = ruleLine.substring(ruleLine.indexOf(":") + 2);
        ruleLine = ruleLine.replaceAll("/$", "");
        ruleLine = ruleLine.replaceAll("\\*", ".*"); // replace '*' with '.*' to meet any char any number of times
        return ".*" + ruleLine + ".*"; // wrap the directory with .* to search for it anywhere in the url
    }

    /**
     * This function checks if the hostname robots rules already exists in the DB
     * @param hostName: String representation of the hostname to be checked
     * @return boolean value set to true if the rules are in the DB, false otherwise
     */
    public boolean isChecked(String hostName) {
        Document doc = rulesCollection.find(new Document("host", hostName)).first();
        return doc != null && doc.get("checked", boolean.class);
    }

    /**
     * This function builds a connection to the passed url
     * @param url: URL object to connect to
     * @return Reader object to be used for reading the document later
     * @throws Exception: The Exception can be one of the following:
     * IOException: if something wrong happened on opening the connection or getting the responseCode
     *                                       or on getInputStream
     * ProtocolException: if something wrong happened on setting the request method
     * UnknownServiceException: if something wrong happened on getInputStream
     */
    private Reader connect(URL url) throws Exception {
        // open connection to send requests
        HttpURLConnection connection;

        // sending the GET request to /robots.txt
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // reading the response
        int status = connection.getResponseCode();

        // if status code >= 400, the request failed return null
        if (status > 399) {
            System.out.println("Request Failed! " + url);
            return null;
        }
        // else return reader for the file
        return new InputStreamReader(connection.getInputStream());
    }

    public static void main(String... args) {
        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("test");
        RobotChecker checker = new RobotChecker(db);
        checker.isAllowed("https://www.wikipedia.org//robots.txt");
    }
}
