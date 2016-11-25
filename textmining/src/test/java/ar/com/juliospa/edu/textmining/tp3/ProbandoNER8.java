package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * regex con jsoup al estilo jquery
 * @author julio
 *
 */
public class ProbandoNER8 {
	Logger log = LoggerFactory.getLogger(ProbandoNER8.class);
	static{
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yy-MM-dd HH:mm:ss.SSS");
	}
	/**
	 * con esto ya por lo menos puedo depurar todos los titulos de todos los archivos
	 * luego puedo atratar de aplicar ner o regex, pero por lo menos asi ya tengo las cosas especificas a depurar.
	 * 
	 *  igual aca faltaria depurar las carpetas que me interesa explorar porque sino se va a hacer bardo mal.
	 */
	@Test
	public void probandoConParser() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String filesUrl = dBoxUrl + "NER/archivosPrueba";
		
		Map<String, List<String>> docs = getMyDocsParsed(filesUrl);
		docs.entrySet().stream().forEach(entry -> showEntry(entry));

	}
	private void showEntry(Entry<String, List<String>> entry) {
		final String tab = "  ";
		log.info(entry.getKey());
		entry.getValue().stream().forEach(val -> log.info(tab+val));
	}
	
	
	private Map<String,List<String>> getMyDocsParsed(String aPath) {
		Map<String,List<String>> ret = new HashMap<>();
		try {
			// lee todos los archivos de la URL
			File startFileUrl = new File(aPath);
			File[] files = startFileUrl.listFiles();
			for (File file : files) {

				// si no es directorio se analiza
				if (!file.isDirectory()) {
					List<String> currentFileSentnces = searchTitlesInThread(file);

					// cuando termino el archivo , guardo en el mapa
					ret.put(file.getAbsolutePath(), currentFileSentnces);
				}else{
					log.warn("no es archivo:" + file.getAbsolutePath());				
				}

			}
		} catch (Exception e) {
			log.error("--error: " + aPath, e);
		}

		return ret;
	}
	

	/**
	 * probando con 1 archvo de prueba
	 */
	@Test
	public void probandoRegexJqueryLikeJsoup() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		String sampleFile = filesUrl + "/viernes-23-05-14-alan-fitzpatrick-gala-cordoba.html";

		try {
			File input = new File(sampleFile);
			List<String> tmp = searchTitlesInThread(input);
			tmp.forEach(el ->System.out.println(el) );

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	private List<String> searchTitlesInThread(File input) throws IOException {
		List<String> ret = new ArrayList<>();
		
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

		// https://jsoup.org/cookbook/extracting-data/selector-syntax
		// http://stackoverflow.com/questions/6152671/jsoup-select-div-having-multiple-classes
		Elements titulosThread = doc.select("h2.posttitle.icon");// los h2. con class="posttitle icon"  
		// de lo filtrado tomo el resultado , lo agarro como stream, le filtro los que sean respuestas, los que quedan los imprimo
		titulosThread.stream()
			.filter(el -> !el.text().contains("Re:"))
			//.forEach(el ->System.out.println(el.text()) );
			.forEach(el ->ret.add(el.text()) );
		return ret;
	}
	
	
	
}
