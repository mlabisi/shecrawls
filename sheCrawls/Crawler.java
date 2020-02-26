/**
 * Crawler object class.
 */
package sheCrawls;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup; //import core public access point for jsoup
import org.jsoup.nodes.Document; //HTML document 
import org.jsoup.nodes.Element; //HTML element - extract data, manipulate HTML
import org.jsoup.select.Elements;
import org.jsoup.safety.Whitelist;

public class Crawler {
    private String language;
    private ArrayList<String> linksList = new ArrayList<String>();
    int linkCount = 0;

    //constructor takes URL and desired language
    public Crawler(String url, String lang) {
        try {
            this.language = lang;
            downloadContent(url);
        } catch (Exception e) {
            System.out.println("she can't crawl this URL.");
        }
    }

    private Boolean checkLang(Document doc) {
        Element language = doc.select("html").first();
        String docLang = language.attr("lang");
        System.out.println(docLang);

        return docLang.equals(this.language);
    }

    //get links, count links
    //take in a URL
    private void downloadContent(String url) {

        try {
            //convert to doc
            Document d = Jsoup.connect(url).get();

            //check language
            if (checkLang(d)) {
                //download ALL the content, place in repository directory
                exportContent(d);

                //search for all outlinks
                getPageLinks(url);

                //add URL + linkCount to report.csv
                addCSVEntry(this.language, d.location(), linkCount);
                //call downloadContent on all outlinks
                if (linkCount <= 109){
                    String webLink = linksList.get(linkCount);
                    linkCount = linkCount + 1;
                    System.out.println("Size of array: " + linksList.size());
                    System.out.println(linkCount);
                    downloadContent(webLink);
                }
            }
        } catch (Exception e) {
            System.out.println("Couldn't write to file");
        }
    }

    //Check if URL has already been crawled and/or if we've seen the same content before
    public void getPageLinks(String url) {
        if (!linksList.contains(url) && (linksList.size() <= 109)) {
            try {
                linksList.add(url);
                System.out.println("URL: " + url);
                System.out.println("Number of links: " + linksList.size());

                Document document = Jsoup.connect(url).get();
                Elements linksOnPage = document.select("a[href]");

                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println("For '" + url + "': " + e.getMessage());
            }
        }
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
            System.out.println("Couldn't write to " + fileName);
        }
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