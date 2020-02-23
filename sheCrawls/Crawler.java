/**
 * Crawler object class.
 */
package sheCrawls;
import java.io.*;
import java.util.ArrayList;
import org.jsoup.Jsoup; //import core public access point for jsoup
import org.jsoup.nodes.Document; //HTML document 
import org.jsoup.nodes.Element; //HTML element - extract data, manipulate HTML
import org.jsoup.select.Elements; //list of elements 

public class Crawler {
	private Document doc;
	private String language;
	private ArrayList<String> links = new ArrayList<String>();
	
	//constructor takes URL and desired language 
	public Crawler(String url, String lang){
		try {
			this.doc = Jsoup.connect(url).get();
			this.language = lang;
			downloadContent(url);
		}
		
		catch(Exception e) {
			System.out.println("she can't crawl this URL.");
		}
	}
	
	private Boolean checkLang(Document d) {
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
				//download ALL the content, place in Repo folder
				extractContent(d);

				//search for all outlinks

				//add URL + linkCount to report.csv
				addCSVEntry("");

				//call downloadContent on all outlinks
			}
		}
		
		catch (Exception e) {
			System.out.println("Couldn't write to file");
		}	
	}
	
	private void getLinks(String URL) {
		//arraylist
	}

	private void extractContent(Document doc) {

	}

	private void addCSVEntry(String entry) {

	}
} //end Crawler