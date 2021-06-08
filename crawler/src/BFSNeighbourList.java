import java.io.FileNotFoundException;
import java.util.*;

public class BFSNeighbourList {
    FileFilling seedsFile;
    FileFilling urlsFile;
    ArrayList<String> seed;
    long count = 0;
    boolean reached5000 = false;
    URINormalizer normalizer;
    ArrayList<String> visitedLinks = new ArrayList<String>();
    RobotChecker robotChecker;
    public Queue<urlObj> queue;
    static ArrayList<urlObj> urls = new ArrayList<urlObj>();

    public void printQueue() {
        Iterator<urlObj> iterator = queue.iterator();
        while (iterator.hasNext()) {
            urlObj url = iterator.next();
            if (url != null) {
                System.out.println(url.url);
            }
        }
    }

    static class urlObj // urlObj
    {
        String url;
        boolean visited;
        List<urlObj> neighbours;

        urlObj(String url) {
            this.url = url;
            this.neighbours = new ArrayList<>();

        }

        public synchronized void addneighbours(urlObj neighbourNode) {
            this.neighbours.add(neighbourNode);
        }

        public List<urlObj> getNeighbours() {
            return neighbours;
        }

        public void setNeighbours(List<urlObj> neighbours) {
            this.neighbours = neighbours;
        }
    }

    public BFSNeighbourList(ArrayList<String> seed, FileFilling urlsFile, URINormalizer normalizer, RobotChecker robot) {
        queue = new LinkedList<urlObj>();
        this.normalizer = normalizer;
        this.seed = seed;
        this.urlsFile = urlsFile;
        this.robotChecker = robot;

        if (urlsFile.isFileEmpty()) {
            for (int i = 0; i < 5; i++) {
                queue.add(new urlObj(seed.get(i)));
            }
            System.out.println("Queue filled with seeds");
        } else {
            long numLines = urlsFile.numLinesInFile();
            String lastUrlInFile = urlsFile.ReadFromFile(numLines - 1);
            queue.add(new urlObj(lastUrlInFile));
            for (long i = 0; i <= numLines - 1; i++) {
                String u = urlsFile.ReadFromFile(i);
                String normalized = normalizer.normalizePreservedSemantics(u);
                normalized = normalizer.normalizeSemantics(normalized);
                visitedLinks.add(normalized);
            }
            try {
                RobotChecker.loadRules(robot);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
            count = numLines;
            urlsFile.closeFis();
        }

    }

    public synchronized urlObj getQTop() {
        if (queue.size() > 0) {
            return queue.remove();
        }
        return null;
    }

    public synchronized void bfs(urlObj node) {
        System.out.println(Thread.currentThread().getName()+" is writing to the file");
        String normalized;
        if(robotChecker.isAllowed(node.url)) {
            normalized = normalizer.normalizePreservedSemantics(node.url);
            normalized = normalizer.normalizeSemantics(normalized);

            if (!visitedLinks.contains(normalized)) {
                visitedLinks.add(normalized);
                count++;
                if (count <= 5000)
                    urlsFile.WriteToFile(node.url);
                else
                    return;
                System.out.println("count of urls = " + count);
            }else {

            }
        }

        List<urlObj> neighbours = node.getNeighbours();
        for (urlObj urlo : neighbours) {
            if (count >= 5000)
                break;
            if (robotChecker.isAllowed(urlo.url)) {
                normalized = normalizer.normalizePreservedSemantics(urlo.url);
                normalized = normalizer.normalizeSemantics(normalized);

                if (!visitedLinks.contains(normalized)) {
                    queue.add(urlo);
                }
            }else{
            System.out.println("not allowed");
            }
        }
        System.out.println("OUT");
    }
}
