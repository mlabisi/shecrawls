/**
 * Crawler object class.
 */
package sheCrawls;

import java.io.*;
import java.util.*;

//import Jsoup library
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.safety.Whitelist;

//import language detection library
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class Crawler {
    private String seed;
    private String language;
    private ArrayList<String> links; //extracted links
    private Map<String, Integer> dictionary; // words and their frequencies
    private int exportedCt; //number of URLs exported
    private boolean debugMode;

    // characters to be ignored when couning frequency
    private final List<String> TO_IGNORE = Arrays.asList("!", "©", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-",
            "+", "=", "{", "[", "}", "]", "|", ";", ":", "\"", "<", ">", "?", "/", ".", ",", " ");

    //constructor takes in seed URL, desired language and debug mode
    public Crawler(String url, String lang, boolean debug) {
        this.seed = url;
        this.language = lang;

        this.links = new ArrayList<>();
        this.dictionary = new HashMap<>();

        this.exportedCt = 0;
        this.debugMode = debug;

        //add seed URL to links arraylist
        this.links.add(this.seed);
    }

    void crawl() {
        crawl(this.seed, 0);
        exportAnalytics();
    }

    private void crawl(String url, int crawlCt) {
        try {
            Document d = Jsoup.connect(url).get();
            ArrayList<String> outlinks = getPageLinks(d);

            if (debugMode) {
                System.out.println();
            }

            //export URL to report file with the number of outlinks
            if (checkLang(d)) {
                exportContent(d);
                addCSVEntry(this.language, url, outlinks.size());
                collectWords(d.text());

                crawlCt++;
                this.exportedCt++;
            }

            //crawl 100 outlinks
            for (int i = 0; (i < Math.min(outlinks.size(), 5)) && (exportedCt < 100); i++) {
                String outlink = outlinks.get(i);

                if(!isLink(outlink)) {
                    continue;
                }

                if (debugMode) {
                    for (int j = 0; j < crawlCt; j++) {
                        System.out.print("  ");
                    }

                    System.out.print(">  (Outlink #" + (i + 1) + " of " + outlinks.size() + "):\t" + outlink);
                }

                crawl(outlink, crawlCt);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //check the language of the document
    private Boolean checkLang(Document doc) {
        //detect the language with detect method
        String text = doc.text();
        String docLang = detect(text);

        //detect the language with Jsoup attribute extraction method
        Element language = doc.select("html").first();
        String encoding = language.attr("lang");

        //return true if both language detector and Jsoup return the same language
        return docLang.equalsIgnoreCase(this.language) && docLang.equalsIgnoreCase(encoding);
    }

    //create detector object using imported language detection library
    private String detect(String text) {
        try {
            Detector detector = DetectorFactory.create();
            detector.append(text);
            return detector.detect();
        } catch (LangDetectException e) {
            System.out.println(e.getMessage());
        }
        return "fail";
    }

    //export content of the doc into Repository
    private void exportContent(Document d) {
        String dirName = System.getProperty("user.dir") + "/repository";
        String fileName = d.title()
                .replace(" ", "")
                .replace("/", "");
        String fileExt = ".txt";

        //remove images
        String docHTML = Jsoup.clean(d.html(), Whitelist.basic());

        //make new file to export to repository
        File dir = new File(dirName);
        dir.mkdir();

        //write HTML content to file in repository
        File file = new File(dirName + File.separator + fileName + fileExt);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {
            bw.write(docHTML);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //extract page links and return outlinks arraylist
    private ArrayList<String> getPageLinks(Document d) {
        ArrayList<String> outlinks = new ArrayList<>();
        try {
            Elements urls = d.select("a[href]");

            for (Element url : urls) {
                String href = url.attr("href");
                if (!links.contains(href) && !outlinks.contains(href) && isLink(href)) {
                    links.add(href);
                    outlinks.add(href);
                }

            }
        } catch (Exception e) {
            System.out.print("For '" + d.location() + "': " + e.getMessage());
        }
        Collections.sort(outlinks);

        return outlinks;
    }

    //check if URL is actually a link
    private boolean isLink(String href) {
        return href.startsWith("http");
    }

    //add URL and stats to CSV file
    private void addCSVEntry(String lang, String url, int ct) {
        String fileName = System.getProperty("user.dir") + "/report.csv";
        String[] entry = {lang, url, ct + ""};

        File file = new File(fileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true))) {
            bw.write(String.join(",", entry) + "\n");
        } catch (IOException e) {
            System.out.println("Couldn't write to " + fileName);
        }
    }

    // build vocabulary by collecting words and their frequencies
    private void collectWords(String text) {
        String[] tokens = text.toLowerCase().split(" ");
        for (String token : tokens) {
            if (TO_IGNORE.contains(token) || !token.matches("^[a-zA-Z]*$") || token.equals("")) {
                continue;
            }

            if (!this.dictionary.containsKey(token)) {
                this.dictionary.put(token, 1);
            } else {
                int ct = this.dictionary.get(token);
                this.dictionary.put(token, ct + 1);
            }
        }
    }

    // export all words and their frequencies
    private void exportAnalytics() {
        String fileName = System.getProperty("user.dir") + "/analytics_" + this.language + ".csv";
        File file = new File(fileName);

        dictionary.forEach((word, freq) -> {
            String[] entry = {word.replace(",", ""), freq + ""};

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true))) {
                bw.write(String.join(",", entry) + "\n");
            } catch (IOException e) {
                System.out.println("Couldn't write to " + fileName);
            }
        });
    }
} //end Crawler