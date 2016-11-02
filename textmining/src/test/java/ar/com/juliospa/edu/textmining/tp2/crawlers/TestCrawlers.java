package ar.com.juliospa.edu.textmining.tp2.crawlers;

import java.util.Map;

import org.junit.Test;

import ar.com.juliospa.edu.textmining.utils.TextMiningUtils;

public class TestCrawlers {

	@Test
	public void testSimpleScraper() {
		String url = "http://www.wikipedia.org/wiki/Python";
		SimpleCrawler.scrapeTopic(url);
	}
	
	/**
	 * buscando en un dataset de leyes
	 */
	@Test
	public void resumenDeCrawling() {
		String carpeta = "/home/julio/dev/text_mining/nc_eventos_pasados_wget/www.nightclubber.com.ar";
		Map<String, Long> result = TextMiningUtils.resumenForFiles(carpeta);
		result.entrySet().forEach(entry -> TextMiningUtils.mostrarEntryResumenCrawling(entry));
	}
	
}
