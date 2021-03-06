package sheCrawls;

import java.io.*;
public class Driver {

	public static void main(String[] args) throws IOException {
		String URL = "https://en.wikipedia.org/wiki/Eiffel_Tower";
		Crawler enC = new Crawler (URL, "en", false);
		Crawler frC = new Crawler (URL, "fr", false);
		Crawler deC = new Crawler (URL, "de", false);

//		System.out.println("Now running the English crawler");
//		enC.crawl();
//
//		System.out.println("\n\nNow running the French crawler");
//		frC.crawl();

		System.out.println("\n\nNow running the German crawler");
		deC.crawl();
	}
}
