/**
 * Crawler object class.
 */
package sheCrawls;

import java.io.*;
import java.util.ArrayList;

import org.jsoup.Jsoup; //import core public access point for jsoup
import org.jsoup.nodes.Document; //HTML document
import org.jsoup.nodes.Element; //HTML element - extract data, manipulate HTML
import org.jsoup.select.Elements;
import org.jsoup.safety.Whitelist;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class Crawler {
    private String language;
    private ArrayList<String> links;


    public Crawler(String url, String lang) {
        this.language = lang;
        this.links = new ArrayList<>();

        this.links.add(url);

        crawl(url, 0);
    }

    private void crawl(String url, int crawlCt) {
        try {
            Document d = Jsoup.connect(url).get();
            ArrayList<String> outlinks;
            System.out.println();

            if (checkLang(d)) {
                crawlCt++;

                exportContent(d);
                outlinks = getPageLinks(d);
                addCSVEntry(this.language, url, outlinks.size());


                for (int i = 0; i < Math.min(outlinks.size(), 10) && links.size() < 150; i++) {
                    String outlink = outlinks.get(i);
                    for (int j = -1; j < crawlCt; j++) {
                        System.out.print("\t");
                    }
                    System.out.print(">  (Outlink #" + (i+1) + " of " + outlinks.size() + "):\t" + outlink );
                    crawl(outlink, crawlCt);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Boolean checkLang(Document doc) {
        String docLang = detect(doc.body().text());

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
        String fileName = d.title().replace(" ", "");
        String fileExt = ".txt";
        String docHTML = Jsoup.clean(d.html(), Whitelist.relaxed().removeTags("img"));

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
        String dUrl = d.location();
        ArrayList<String> outlinks = new ArrayList<>();
        try {
            Elements urls = d.select("a[href]");

            for (Element url : urls) {
                String href = url.attr("href");
                if (!links.contains(href) && !outlinks.contains(href) && isValid(href)) {
                    links.add(href);
                    outlinks.add(href);
                }

            }
        } catch (Exception e) {
            System.out.print("For '" + dUrl + "': " + e.getMessage());
        }

        return outlinks;
    }

    private boolean isValid(String href) {
        return !(href.startsWith("/") || href.isBlank() || href.startsWith("?") || href.startsWith("./") || href.startsWith("../") || href.startsWith("#") || href.startsWith("tel:"));
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

} //end Crawler