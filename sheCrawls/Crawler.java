/**
 * Crawler object class.
 */
package sheCrawls;

import java.io.*;
import java.util.ArrayList;

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
    private int exportedCt; //number of URLs exported
    private boolean debugMode; 

    //constructor takes in seed URL, desired language and debug mode
    public Crawler(String url, String lang, boolean db) {
        this.seed = url;
        this.language = lang;
        this.links = new ArrayList<>();

        this.exportedCt = 0; 
        this.Mode = db; 

        //add seed URL to links arraylist 
        this.links.add(this.seed);
    }
     
    void crawl() {
        crawl(this.seed, 0);
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
                crawlCt++;
                this.exportedCt++;
            }

            //crawl 100 outlinks 
            for (int i = 0; (i < outlinks.size()) && (exportedCt < 100); i++) {
                String outlink = outlinks.get(i);
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
        String docLang = detect(doc.body().text());
        
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
        String fileName = d.title().replace(" ", "");
        String fileExt = ".txt";
        
        //remove images 
        String docHTML = Jsoup.clean(d.html(), Whitelist.relaxed().removeTags("img"));

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
    
    //check if URL is valud
    private boolean isValid(String href) {
        return !(href.startsWith("/") || href.isBlank() || href.startsWith("?") || href.startsWith("./") || href.startsWith("../") || href.startsWith("#") || href.startsWith("tel:"));
    }

    //add URL to CSV file 
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
