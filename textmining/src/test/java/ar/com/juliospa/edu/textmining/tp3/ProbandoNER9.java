package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class ProbandoNER9 {
	Logger log = LoggerFactory.getLogger(ProbandoNER9.class);
	static{
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yy-MM-dd HH:mm:ss.SSS");
	}

	/* pipeline de trabajo
	 * 1) filtrar que carpetas me interesa escanear
	 * 2) escanar esas carpetas por los archivos que me interesan 
	 * 3) procesar los archivos con jsoup para guardar base 
	 * 4) de esa base generada > custom regex. 
	 * 5) volver a probar ner > elaborar un modelo propio 
	 * 6) conclusiones 
	 */
	
	
	
	@Test
	public void filrarCarpetasTest(){
		// TODO: ya estan los subforos, ahora buscar los threads / posts
		
		filtrarCarpetasAescanear();

		
	}
	@Test
	public void filtrarArchivosTest(){
		
	}
	
	
	public List<String> filtrarCarpetasAescanear(){
		// arrancar en eventos pasados: 
		// /home/julio/dev/text_mining/nc_eventos_pasados_wget/www.nightclubber.com.ar/foro/48/eventos-pasados/index.html
		// buscar todos los links que tengan "Eventos Pasados" en el value
		// meterlo en un set para que no se repitan nos va a quedar un set de .../www.nightclubber.com.ar/foro/##/nombreSubForo/
		
		// una vez que se tienen las que se llaman eventos pasados, recorrer cad auna de esasa en busca de los temas 
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/index.html
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/##tema/nombretema.html
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/##tema/nombreTema/otros.html
		
		String dbURl = "/home/julio/dev/text_mining/nc_eventos_pasados_wget/www.nightclubber.com.ar/";
//		String filesUrl = dbURl + "foro/48/eventos-pasados/"; > cierra mejor foro/222/eventos-pasados-argentina/
		// porque estan explotados los subforos
		String filesUrl = dbURl + "foro/222/eventos-pasados-argentina/";
		String sampleFile = filesUrl + "index.html";

		try {
			File input = new File(sampleFile);
			// buscar todos los links que tengan "Eventos Pasados" en el value

			List<String> tmp = searchEventosPasados(input);
			tmp.forEach(el ->System.out.println(el) );
			System.out.println(tmp.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		

		
		return null;	
	}
	
	
	private List<String> searchEventosPasados(File input) throws IOException {
		List<String> ret = new ArrayList<>();
		
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

		// https://jsoup.org/cookbook/extracting-data/selector-syntax
		// http://stackoverflow.com/questions/6152671/jsoup-select-div-having-multiple-classes
		Elements titulosForo = doc.select("h2.forumtitle");// los h2. con class="posttitle icon"  
		titulosForo = titulosForo.select("a");
		Elements titulosSubforos = doc.select("li.subforum");
		titulosSubforos = titulosSubforos.select("a");
		
		titulosForo.stream()
		.forEach(el ->ret.add(el.text() +  "[ "+ el.attr("href")  +" ]") );
		titulosSubforos.stream()
		.forEach(el ->ret.add(el.text() +  "[ "+ el.attr("href")  +" ]") );
		
		// de lo filtrado tomo el resultado , lo agarro como stream, le filtro los que sean respuestas, los que quedan los imprimo
//		titulosThread.stream()
//			.filter(el -> !el.text().contains("Re:"))
//			//.forEach(el ->System.out.println(el.text()) );
//			.forEach(el ->ret.add(el.text()) );
		return ret;
	}
	public List<String> escanearArchivosInteres(){
		return null;
	}
	
}
