package sheCrawls;

import java.io.*;
public class Driver {

	public static void main(String[] args) throws IOException {
		String URL = "https://en.wikipedia.org/wiki/Eiffel_Tower";
		Crawler enC = new Crawler (URL, "en", true);
		Crawler frC = new Crawler (URL, "fr", true);
		Crawler esC = new Crawler (URL, "es", true);

		System.out.println("Now running the English crawler");
		enC.crawl();

		System.out.println("\n\nNow running the French crawler");
		frC.crawl();

		System.out.println("\n\nNow running the Spanish crawler");
		esC.crawl();
	}
}
