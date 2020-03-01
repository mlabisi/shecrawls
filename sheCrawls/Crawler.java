/**
 * Crawler object class.
 */
package sheCrawls;

import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.safety.Whitelist;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class Crawler {
    private String seed;
    private String language;
    private ArrayList<String> links;
    private Map<String, Integer> dictionary;
    private int exportedCt;
    private boolean debugMode;

    private final List<String> TO_IGNORE = Arrays.asList("!", "©", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-",
            "+", "=", "{", "[", "}", "]", "|", ";", ":", "\"", "<", ">", "?", "/", ".", ",", " ");

    public Crawler(String url, String lang, boolean debug) {
        this.seed = url;
        this.language = lang;

        this.links = new ArrayList<>();
        this.dictionary = new HashMap<>();

        this.exportedCt = 0;
        this.debugMode = debug;

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

            if (checkLang(d)) {
                exportContent(d);
                addCSVEntry(this.language, url, outlinks.size());
                collectWords(d.text());

                crawlCt++;
                this.exportedCt++;
            }

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

    private Boolean checkLang(Document doc) {
        String text = doc.text();
        String docLang = detect(text);

        Element language = doc.select("html").first();
        String encoding = language.attr("lang");

        return docLang.equalsIgnoreCase(this.language) && docLang.equalsIgnoreCase(encoding);
    }

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

    private void exportContent(Document d) {
        String dirName = System.getProperty("user.dir") + "/repository";
        String fileName = d.title()
                .replace(" ", "")
                .replace("/", "");
        String fileExt = ".txt";
        String docHTML = Jsoup.clean(d.html(), Whitelist.basic());

        File dir = new File(dirName);
        dir.mkdir();

        File file = new File(dirName + File.separator + fileName + fileExt);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {
            bw.write(docHTML);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

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

    private boolean isLink(String href) {
        return href.startsWith("http");
    }

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