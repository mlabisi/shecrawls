package sheCrawls;

import java.io.*;
public class Driver {

	public static void main(String[] args) throws IOException {
		String URL = "https://en.wikipedia.org/wiki/Eiffel_Tower";
		Crawler c = new Crawler (URL, "en", "sp", "fr");
		//c.getReport 
	}
}
