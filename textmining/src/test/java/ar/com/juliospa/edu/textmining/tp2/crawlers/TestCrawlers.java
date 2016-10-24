package ar.com.juliospa.edu.textmining.tp2.crawlers;

import org.junit.Test;

public class TestCrawlers {

	@Test
	public void testSimpleScraper() {
		String url = "http://www.wikipedia.org/wiki/Python";
		SimpleCrawler.scrapeTopic(url);
	}
	
}
