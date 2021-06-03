package SearchEngine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class RobotChecker {
    private HashMap<String, HostData> robotRules;

    static class HostData {
        boolean checked;
        ArrayList<String> disAllowedURLs;

        HostData() {
            this.checked = false;
            this.disAllowedURLs = new ArrayList<>();
        }
    }



    public RobotChecker() {
        robotRules = new HashMap<String, HostData>();
    }

    public boolean isChecked(byte[] parsedHTML) {
        try {
            return robotRules.get(parsedHTML).checked;
        } catch (Exception e) {
            return false;
        }

    }
    static boolean checkEncoding(String Url){
        try {
            Document document = Jsoup.connect(Url).get();
            String parsedURL = document.toString();
            //System.out.println(parsedURL);
            byte[] htmlBody = parsedURL.getBytes("UTF-8");
            System.out.println(htmlBody);
            MessageDigest messageDigest= MessageDigest.getInstance("SHA-256");
            System.out.println(Arrays.toString(messageDigest.digest(htmlBody)));

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void getRules(String seed) throws IOException {
        String hostName = "";String protocol = "";URL url= null;


        //if (isChecked()) return;

        // generate the url for robots.txt
        try {
        url = new URL(seed);
        hostName = url.getHost(); // stackoverflow.com
        hostName = hostName.replace("www.", "");

        protocol = url.getProtocol(); // https: http
 url = new URL(protocol + "://" + hostName + "/robots.txt");
        } catch (Exception e) {
            System.out.println(url);
            System.out.println("Host name is: " + hostName);
            System.out.println("Protocol is: " + protocol);
            System.out.println(e.getMessage());
            return;
        }
        Reader streamReader = null;
        try {
            streamReader = connect(url);
        } catch (IOException e) {
            streamReader = null;
        }

        if (streamReader != null) {

            BufferedReader reader = new BufferedReader(streamReader);
            boolean user_agent = false;
            String inputLine;
            HostData hostData = new HostData();
            while ((inputLine = reader.readLine()) != null) {
                // we are only interested in 'User-Agent: *' line
                if (inputLine.toLowerCase().startsWith("user-agent")) { // if user-agent line
                    if (inputLine.contains("*")) { // if the rules are for all crawlers
                        user_agent = true; // set the flag to start parsing rules
                    } else if (user_agent) { // if it's for a specific crawler & we read '*', then no need to keep
                        // reading
                        break;
                    }
                }
                if (user_agent && inputLine.toLowerCase().startsWith("disallow")) { // rule line   /api/
                    String directory = inputLine.substring(inputLine.indexOf(":") + 2); // read the disallowed
                    // directory/file
                    hostData.disAllowedURLs.add(processPatterns(directory)); // preprocess the directory pattern before adding it
                    // to the list
                }
            }
            hostData.checked = true;
            this.robotRules.put(hostName, hostData); // add the host with its robot rules to our map
        }
    }

    private Reader connect(URL url) throws IOException {
        // open connection to send requests
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        // sending the GET request to /robots.txt
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            System.out.println(e.getMessage());
            return null;
        }

        // reading the response
        int status = connection.getResponseCode();
        if (status > 299) {
            System.out.println("Request Failed!");
            return null;
        }
        return new InputStreamReader(connection.getInputStream());
    }

    public void printDisallowed() { // for debugging
        for (Map.Entry<String, HostData> entry : robotRules.entrySet()) {
            String key = entry.getKey();
            HostData value = entry.getValue();
            for (String val : value.disAllowedURLs) {
                System.out.println(key + " = " + val);
            }
        }
    }

    public boolean isAllowed(String url) {

        String host = ""; // get host name from the url
        String protocol = "";
        try {
            host = new URL(url).getHost();
            protocol = new URL(url).getProtocol();
            host = host.replace("www.", "");
        } catch (MalformedURLException e) {
            return false;
        }
        if (host.equals("")) { // invalid url, couldn't extract host name >> return false
            return false;
        }
        ArrayList<String> directories = new ArrayList<>();
        if (!robotRules.containsKey(host)) {
            try {
                System.out.println("Getting Rules...");
                this.getRules(protocol+"://"+host);
                System.out.println("Done Rules.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        try {
        directories = robotRules.get(host).disAllowedURLs;


            for (String pattern : directories) {
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(url);
                if (m.matches()) { // if the url contains a disallowed directory >> return false
                    return false;
                }
            }
        }catch (Exception e){
            System.out.println(host);
            System.out.println(e.getMessage());
        }
        return true; // if no directory was matched >> allowed url, return true
    }


    private String processPatterns(String directory) {
        directory = directory.replaceAll("\\*", ".*"); // replace '*' with '.*' to meet any char any number of times
        return ".*" + directory + ".*"; // wrap the directory with .* to search for it anywhere in the url
    }

    public static void main(String... args) {
        RobotChecker checker = new RobotChecker();
        checker.isAllowed("https://meta.stackoverflow.com");
    }

}
