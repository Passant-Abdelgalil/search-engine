import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public class WebCrawler implements Runnable {

    private final Thread thread;
    private final int ID;
    boolean stopCrawling = false;
    BFSNeighbourList bfsQueue;

    public WebCrawler(BFSNeighbourList bfsQueue, int num) {
        // Create a thread with necessary variables
        ID = num;
        this.bfsQueue = bfsQueue;
        thread = new Thread(this);

        thread.start();
    }

    @Override
    public void run() {
        crawl();
    }

    private void crawl() { // level init =1 LineinFile init=1
        while (true) {
            BFSNeighbourList.urlObj top = null;
            Document doc = null;
            // Check the stopping criteria
            synchronized (bfsQueue) {
                if (bfsQueue.count >= 5001) { // untill we have 5000 link //&& file.count<=5000 8alat
                    stopCrawling = true;
                } else {
                    while (top == null && bfsQueue.queue.size() != 0) {
                        top = bfsQueue.getQTop();
                    }
                }
                if (top == null) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " will sleep");
                        bfsQueue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("INTERRUPTED");
                    }
                    System.out.println(bfsQueue.queue.size());
                    top = bfsQueue.getQTop();
                    if (top == null) {
                        System.out.println(Thread.currentThread().getName() + " will close, no more urls. queue size is " + bfsQueue.queue.size());
                        break;
                    }
                    doc = request(top.url);
                }
            }

            System.out.println(Thread.currentThread().getName());
            // Crawling Logic
            if (!stopCrawling) { // && top != null //emta el top tb2a null?
                // Check HTML Documents
                if (top != null)
                    doc = request(top.url);
                if (doc != null) {
                    if (doc.documentType() != null && !doc.documentType().name().equals("html")) {
                        System.out.println(doc.documentType().name() + "is not HTML document");
                        continue;
                    }
                    System.out.println(Thread.currentThread().getName() + "-> " + top.url);
                    System.out.println("Thread " + Thread.currentThread().getName() + " will crawl " + doc.select("a[href]").size()
                            + " link");
                    for (Element link : doc.select("a[href]")) {
                        String next_link = link.absUrl("href");
                        top.addneighbours(new BFSNeighbourList.urlObj(next_link));
                    }

                    synchronized (this.bfsQueue) {

                        System.out.println(Thread.currentThread().getName() + " is writing to the file");
                        String normalized;
                        if (bfsQueue.robotChecker.isAllowed(top.url)) {
                            normalized = bfsQueue.normalizer.normalizePreservedSemantics(top.url);
                            normalized = bfsQueue.normalizer.normalizeSemantics(normalized);

                            if (!bfsQueue.visitedLinks.contains(normalized)) {
                                bfsQueue.visitedLinks.add(normalized);
                                bfsQueue.count++;
                                if (bfsQueue.count == 5001) {
                                    bfsQueue.reached5000 = true;
                                }
                                if (!bfsQueue.reached5000) {
                                    bfsQueue.urlsFile.WriteToFile(top.url);
                                    System.out.println("count of urls = " + bfsQueue.count);
                                }
                            } else {

                            }
                        }

                        List<BFSNeighbourList.urlObj> neighbours = top.getNeighbours();
                        for (BFSNeighbourList.urlObj urlo : neighbours) {
                            if (bfsQueue.count >= 5001)
                                break;
                            if (bfsQueue.robotChecker.isAllowed(urlo.url)) {
                                normalized = bfsQueue.normalizer.normalizePreservedSemantics(urlo.url);
                                normalized = bfsQueue.normalizer.normalizeSemantics(normalized);

                                if (!bfsQueue.visitedLinks.contains(normalized)) {
                                    bfsQueue.queue.add(urlo);
                                }
                            } else {
                                System.out.println("not allowed");
                            }
                        }

                        bfsQueue.notifyAll();
                        if (bfsQueue.queue.size() == 0) {
                            break;
                        }
                    }
                }
            } else {
                break;
            }
        }

    }

    public void addUrls(BFSNeighbourList.urlObj node) {
        synchronized (this.bfsQueue) {
            System.out.println(Thread.currentThread().getName() + " is writing to the file");
            String normalized;
            if (bfsQueue.robotChecker.isAllowed(node.url)) {
                normalized = bfsQueue.normalizer.normalizePreservedSemantics(node.url);
                normalized = bfsQueue.normalizer.normalizeSemantics(normalized);

                if (!bfsQueue.visitedLinks.contains(normalized)) {
                    bfsQueue.visitedLinks.add(normalized);
                    bfsQueue.count++;
                    if (bfsQueue.count <= 5000)
                        bfsQueue.urlsFile.WriteToFile(node.url);
                    else
                        return;
                    System.out.println("count of urls = " + bfsQueue.count);
                } else {

                }
            }

            List<BFSNeighbourList.urlObj> neighbours = node.getNeighbours();
            for (BFSNeighbourList.urlObj urlo : neighbours) {
                if (bfsQueue.count >= 5000)
                    break;
                if (bfsQueue.robotChecker.isAllowed(urlo.url)) {
                    normalized = bfsQueue.normalizer.normalizePreservedSemantics(urlo.url);
                    normalized = bfsQueue.normalizer.normalizeSemantics(normalized);

                    if (!bfsQueue.visitedLinks.contains(normalized)) {
                        bfsQueue.queue.add(urlo);
                        bfsQueue.notifyAll();
                    }
                } else {
                    System.out.println("not allowed");
                }
            }
            System.out.println("OUT");
        }
    }

    private Document request(String url) { // Parsing HTML document
        try {
            Connection con = null;
            if (url != null && url.length() != 0) {
                con = Jsoup.connect(url);
                if (con != null) {
                    Document doc = con.get();
                    if (con.response().statusCode() == 200) {
                        System.out.println("\n**Bot ID:" + ID + " Received Webpage at " + url);
                        String title;
                        if (doc != null) {
                            title = doc.title();
                            System.out.println(title);
                            return doc;
                        }
                        return null;
                    }
                    return null;
                }
                return null;
            }
            return null;
        } catch (IOException e) {
            return null;
        }

    }

}
