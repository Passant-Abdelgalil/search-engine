package WebCrawler;


import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RobotChecker {
    private HashMap<String,ArrayList<String>> mapped_robotRules = null;

    public RobotChecker(){
        mapped_robotRules = new HashMap<String, ArrayList<String>>();
    }

    public void getRules(String seed) throws IOException {

        // generate the url for robots.txt
        URL url = null;
        String hostName = extract_from_url(seed, "hostname");
        String protocol = extract_from_url(seed, "protocol");
        try {
            url = new URL(protocol + "://" + hostName + "/robots.txt");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        // open connection to send requests
        HttpsURLConnection connection =  (HttpsURLConnection) url.openConnection();


        // sending the GET request to /robots.txt
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return;
        }

        // reading the response
        int status = connection.getResponseCode();
        Reader streamReader = new InputStreamReader(connection.getInputStream());
        String inputLine;
        ArrayList<String> directories = new ArrayList<String>();

        if (status > 299) {
            System.out.println("Request Failed!");
            return;
        } else {
            BufferedReader reader = new BufferedReader(streamReader);
            boolean user_agent = false;
            while((inputLine = reader.readLine())!=null) {
                // we are only interested in 'User-Agent: *' line
                if(inputLine.toLowerCase().startsWith("user-agent")){                       // if user-agent line
                    if(inputLine.contains("*")){                                            // if the rules are for all crawlers
                        user_agent = true;                                                  // set the flag to start parsing rules
                    }else if (user_agent){                                                  // if it's for a specific crawler & we read '*', then no need to keep reading
                        break;
                    }
                }
                if(user_agent && inputLine.toLowerCase().startsWith("disallow")){           // rule line
                    String directory = inputLine.substring(inputLine.indexOf(":")+2);       // read the disallowed directory/file
                    directories.add(process_patterns(directory));                           // preprocess the directory pattern before adding it to the list
                }
            }
            this.mapped_robotRules.put(hostName, directories);                              // add the host with its robot rules to our map
        }
    }

    public void print_disallowed(){                                                         // for debugging
        for (Map.Entry<String, ArrayList<String>> entry : mapped_robotRules.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            for (String val : value) {
                System.out.println(key + " = " + val);
            }
        }
    }
    public boolean isAllowed(String url){

        String hostName = extract_from_url(url, "hostname");                                // get host name from the url
        if(hostName == ""){                                                                 // invalid url, couldn't extract host name >> return false
            return false;
        }
        ArrayList<String> directories = mapped_robotRules.get(hostName);                    // get disallowed directories for that host
        for(String pattern: directories){
            if (url.contains(pattern)){                                                     // if the url contains a disallowed directory >> return false
                return false;
            }
        }
        return true;                                                                        // if no directory was matched >> allowed url, return true
    }


    private String extract_from_url(String url, String info){
        Pattern p = null;
        if (info == "hostname") {
            p = Pattern.compile("(?<=www.).*?/");                                             // ex: example.com
        }else if(info == "protocol"){
            p = Pattern.compile("^.*(?=://www)");                                             // ex: https
        }
        Matcher matcher = p.matcher(url);
        String extracted_info="";
        if(matcher.find()){                                                                   // check the pattern in the url
            extracted_info = matcher.group(0);
        }else{
            return "";                                                                        // malformed url
        }
        return extracted_info;                                                                // host name | protocol
    }



    private String process_patterns(String directory){
        directory = directory.replaceAll("\\*", ".*");                                        // replace '*' with '.*' to meet any char any number of times
        return ".*" + directory + ".*";                                                       // wrap the directory with .* to search for it anywhere in the url
    }


}
