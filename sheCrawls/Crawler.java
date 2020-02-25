/**
 * Crawler object class.
 */
package sheCrawls;

import java.io.*;
import java.util.ArrayList;

import org.jsoup.Jsoup; //import core public access point for jsoup
import org.jsoup.nodes.Document; //HTML document 
import org.jsoup.nodes.Element; //HTML element - extract data, manipulate HTML


public class Crawler {
    private String language;
    private ArrayList<String> links = new ArrayList<String>();

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
        int linkCount = 0;

        try {
            //convert to doc
            Document d = Jsoup.connect(url).get();

            //check language
            if (checkLang(d)) {
                //download ALL the content, place in repository directory
                exportContent(d);

                //search for all outlinks

                //add URL + linkCount to report.csv
                addCSVEntry(this.language, d.location(), linkCount);

                //call downloadContent on all outlinks
            }
        } catch (Exception e) {
            System.out.println("Couldn't write to file");
        }
    }

    private void getLinks(String URL) {
        //arraylist
    }

    private void exportContent(Document d) {
        String dirName = System.getProperty("user.dir") + "/repository";
        String fileName = d.title().replace(" ", "");
        String fileExt = ".txt";
        String docHTML = d.outerHtml();

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