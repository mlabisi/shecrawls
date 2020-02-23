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
	private String seedURL;
	private ArrayList<String> desiredLangs = new ArrayList<String>();
	
	//constructor takes URL and desired language 
	public Crawler(String url, String lang1, String lang2, String lang3){
		try {
			this.seedURL = url;
			this.doc = Jsoup.connect(seedURL).get();
			String title=doc.title();
			System.out.println(title);
			
			desiredLangs.add(lang1);
			desiredLangs.add(lang2);
			desiredLangs.add(lang3);
			//System.out.println(checkLang());
			if(checkLang()) {
				downloadContent();
			}
		}
		
		catch(Exception e) {
			System.out.println("she can't crawl this URL.");
		}
	}
	
	private Boolean checkLang() {
		Element language = doc.select("html").first();
		String docLang = language.attr("lang");
		System.out.println(docLang);
			
		if(desiredLangs.contains(docLang)) {
			return true;
			}
		return false;
	}
	
	private void downloadContent() {
		try {
			File file =new File("src/Repository/test.txt");
			FileWriter fw = new FileWriter(file);
			//select paragraph elements
			Elements content = doc.select("p");
				for(Element text: content) {
					String ptext= text.attr("p");
					String outerP=text.outerHtml();
					String innerP=text.outerHtml();
					fw.write(ptext + " " + outerP + " " + innerP);
					
				}
				fw.close();
		} 
		
		catch (Exception e) {
			System.out.println("Couldn't write to file");
		}	
	}
} //end Crawler 
	
	
	/**
		//print links from the whole page
		Elements links = doc.select("a[href]");
		/**for (Element link: links) {
			System.out.println("\nlink: " + link.attr("href"));
			System.out.println("text :" +  link.text());
			}
			
		//select links
		for(Element link: links) {
		String linkHref = link.attr("href");
		String linkText = link.text();
		String linkOuter = link.outerHtml();
		String linkInner = link.html();
		System.out.println(linkHref + "t" + linkText + "t" + linkOuter + "t" + linkInner);
			outlinks++;
		} 
			
		//select paragraph elements
		Elements content = doc.select("p");
			for(Element text: content) {
				String ptext= text.attr("p");
				String outerP=text.outerHtml();
				String innerP=text.outerHtml();
				System.out.println(ptext + " " + outerP + " " + innerP);
			}
			System.out.println(outlinks);
		}
	} **/
