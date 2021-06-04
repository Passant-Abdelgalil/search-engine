package Crawler;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotChecker {
    static class HostData {
        boolean checked;
        ArrayList<String> disAllowedURLs;

        HostData() {
            this.checked = false;
            this.disAllowedURLs = new ArrayList<>();
        }
    }

    private HashMap<String, HostData> robotRules;

    public RobotChecker() {
        robotRules = new HashMap<String, HostData>();
    }

    public boolean isChecked(String host) {
        try {
            return robotRules.get(host).checked;
        } catch (Exception e) {
            return false;
        }

    }

    public void getRules(String seed) throws IOException {

        if (isChecked(seed)) return;

        // generate the url for robots.txt

        URL url = new URL(seed);

        String hostName = url.getHost();

        try {
            url = new URL(url.getProtocol() + "://" + hostName + "/robots.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        try (Reader streamReader = connect(url)) {
            if (streamReader != null) {
                BufferedReader reader = new BufferedReader(streamReader);
                boolean user_agent = false;
                String inputLine;
                HostData hostData = new HostData();
                while ((inputLine = reader.readLine()) != null) {
                    if (inputLine.toLowerCase().startsWith("user-agent")) {
                        if (inputLine.contains("*")) {
                            user_agent = true;
                        } else if (user_agent) { // if it's for a specific crawler & we read '*', then no need to keep reading
                            break;
                        }
                    }
                    if (user_agent && inputLine.toLowerCase().startsWith("disallow")) {
                        String directory = inputLine.substring(inputLine.indexOf(":") + 2);
                        hostData.disAllowedURLs.add(processPatterns(directory));
                    }
                }
                hostData.checked = true;
                this.robotRules.put(hostName, hostData); // add the host with its robot rules to our map

            } else {
                System.out.println("couldn't connect to: " + url);
            }
        }
    }

    private Reader connect(URL url) throws IOException {
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
                this.getRules(url);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        if (!robotRules.containsKey(host)) return false;
        directories = robotRules.get(host).disAllowedURLs;

        for (String pattern : directories) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(url);
            if (m.matches()) { // if the url contains a disallowed directory >> return false
                return false;
            }
        }

        return true; // if no directory was matched >> allowed url, return true
    }


    private String processPatterns(String directory) {
        directory = directory.replaceAll("\\*", ".*"); // replace '*' with '.*' to meet any char any number of times
        return ".*" + directory + ".*"; // wrap the directory with .* to search for it anywhere in the url
    }

    public static void main(String... args) {
        RobotChecker checker = new RobotChecker();
        checker.isAllowed("https://www.blurtit.com/topics");
        checker.printDisallowed();
    }
}

