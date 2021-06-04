package Crawler;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class URINormalizer {
    URINormalizer() { }

    public String normalizePreservedSemantics(String url) {
        /* 1. UPPERCASE  percent-encoded triplets */
        Pattern pattern = Pattern.compile("%[0-9]*[a-z]*");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            url = url.substring(0, matcher.start()) + url.substring(matcher.start(), matcher.end()).toUpperCase() + url.substring(matcher.end());
        }

        /* 2. LOWERCASE Host and Scheme*/
        url = lowercaseHostandScheme(url);

        /* 3. DECODE percent-encoded of unreserved characters*/
        url = decodeURI(url);

        /* 4. Remove dot-segments                http://example.com/foo/./bar/baz/../qux → http://example.com/foo/bar/qux*/
        url = url.replaceAll("\\/\\.+\\/", "/");

        /* 5. Remove / at the end of url */
        url = url.replaceAll("/$", "");

        /* 6. Remove Default port*/
        url = url.replaceAll(":80/", "/")      //default port of HTTP
                .replaceAll(":443/", "/");     //default port of HTTPS

        return url;
    }

    public String normalizeSemantics(String url) {

        // 2. Remove fragment
        url = url.replaceAll("#.*", "");
        // 1. Remove default directory index , www. , empty query, duplicate slashes
        url = url.replaceAll(".*\\.html", "")
                .replaceAll(".*\\.htm", "")
                .replaceAll(".*\\.asp", "")
                .replaceAll(".*\\.php", "")
                .replaceAll("www\\.", "")
                .replaceAll("\\?$", "")             /*  Empty Query         */
                .replaceAll("(?<!:)\\/\\/", "/");   /*  Duplicate Slashes   */


        // 3. Replacing IP with domain name
        Pattern pattern = Pattern.compile("\\.*[0-9]+\\.*");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            try {
                String hostName = InetAddress.getByName(matcher.group()).getHostName();
                url = url.replace("\\.*[0-9]+\\.*", hostName);
            } catch (Exception e) {
            }
        }

        // 4. Sorting the query parameters in alphabetical order
        try {
            url = sortQueryParameters(url);
        } catch (MalformedURLException e) {
        }

        return url;
    }

    private String lowercaseHostandScheme(String url) {
        try {
            URL Url = new URL(url);
            Pattern pattern = Pattern.compile(Url.getHost());
            Matcher matcher = pattern.matcher(url);

            while (matcher.find()) {
                url = url.substring(0, matcher.start()) + url.substring(matcher.start(), matcher.end()).toLowerCase() + url.substring(matcher.end());
            }
            pattern = Pattern.compile(".*://");
            matcher = pattern.matcher(url);
            if (matcher.find()) {
                url = matcher.group(0).toLowerCase() + url.substring(matcher.end());
            }
        } catch (Exception e) {
        }
        return url;
    }

    private String decodeURI(String url) {

        url = url.replaceAll("%2D", "-")
                .replaceAll("%2E", ".")
                .replaceAll("%7E", "~")
                .replaceAll("%5F", "_");

        Pattern pattern = Pattern.compile("%3[0-9]");
        Matcher matcher = pattern.matcher(url);
        //  DIGIT DECODER
        while (matcher.find()) {
            url = url.replaceAll(matcher.group(0), String.valueOf(matcher.group(0).charAt(2)));
        }
        // LETTER DECODER
        url = decodeLetter(url);
        return url;
    }

    private String decodeLetter(String url) {
        /*  CAPITAL LETTER DECODING */
        Pattern pattern = Pattern.compile("%4[1-9]");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            url = url.replaceAll(matcher.group(0), String.valueOf(matcher.group(0).charAt(2) - 1 + 'A'));
        }
        pattern = Pattern.compile("%4[a-f]");
        matcher = pattern.matcher(url);
        while (matcher.find()) {
            url = url.replaceAll(matcher.group(0), String.valueOf(matcher.group(0).charAt(2) - 'a' + 'I'));
        }
        pattern = Pattern.compile("%5[0-9]");
        matcher = pattern.matcher(url);
        while (matcher.find()) {
            url = url.replaceAll(matcher.group(0), String.valueOf(matcher.group(0).charAt(2) + 'P'));
        }
        pattern = Pattern.compile("%5a");
        matcher = pattern.matcher(url);
        if(matcher.find()){ url = url.replaceAll(matcher.group(0), "Z");}

        /*  LOWER LETTER DECODING */
        pattern = Pattern.compile("%6[1-9]");
        matcher = pattern.matcher(url);
        while (matcher.find()) {
            url = url.replaceAll(matcher.group(0), String.valueOf(matcher.group(0).charAt(2) - 1 + 'a'));
        }
        pattern = Pattern.compile("%6[a-f]");
        matcher = pattern.matcher(url);
        while (matcher.find()) {
            url = url.replaceAll(matcher.group(0), String.valueOf(matcher.group(0).charAt(2) - 'a' + 'i'));
        }
        pattern = Pattern.compile("%7[0-9]");
        matcher = pattern.matcher(url);
        while (matcher.find()) {
            url = url.replaceAll(matcher.group(0), String.valueOf(matcher.group(0).charAt(2) + 'p'));
        }
        pattern = Pattern.compile("%7a");
        matcher = pattern.matcher(url);
        if(matcher.find()){ url = url.replaceAll(matcher.group(0), "z");}

        return url;
    }

    private String sortQueryParameters(String url) throws MalformedURLException {
        URL Url = new URL(url);
        String Query = Url.getQuery();
        if (Query != null) {
            String[] _Queries = Query.split("&");
            Arrays.sort(_Queries);
            Query = "";
            for (String query : _Queries) {
                Query = String.format("%s&%s", Query, query);
            }
            url = url.replace(Url.getQuery(), Query);
        }
        return url;
    }
}