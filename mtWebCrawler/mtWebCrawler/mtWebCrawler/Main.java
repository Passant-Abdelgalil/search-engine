package mtWebCrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

       /* mtCrawler mtc = new mtCrawler();
        mtc.crawl(4);*/

        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/Main_Page#searchInput").get();
            String parsed = doc.toString();
            System.out.println(parsed.getBytes(StandardCharsets.UTF_8));
            System.out.println("END");
            doc = Jsoup.connect("https://en.wikipedia.org/wiki/Main_Page").get();
            parsed = doc.toString();
            System.out.println(parsed.getBytes(StandardCharsets.UTF_8));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}