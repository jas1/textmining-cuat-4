package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

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
	Gson gson = new Gson();
	String dbURl = "/home/julio/dev/text_mining/nc_eventos_pasados_wget/www.nightclubber.com.ar/";
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
		
		// ya tengo foros y subforos
		List<UrlGuardada> resultForos = filtrarCarpetasAescanear();
		
		// recorrer
		List<UrlGuardada> resultThreads = filtrarThredsURL(resultForos);
		
		resultThreads.size();

		
	}
	private List<UrlGuardada> filtrarThredsURL(List<UrlGuardada> resultForos) {
		List<UrlGuardada> res = new ArrayList<>();		
//		de cada foro tengo que recorrer el indice correspondiente : /index.html 
//		del indice  tengo que fijarme si tiene mas paginas: 
//		\index\pagina##,html > siendo ## la cantidad de paginas , igual hay que recorrerlas a todas. seria un foreach de las paginas en /index/
		try {

			for (UrlGuardada urlGuardada : resultForos) {

				// procesar subforos
				String currentUrl = dbURl+urlGuardada.getUrl()+"/index.html";
				List<UrlGuardada> tmpSubForos = procesarSubForos(urlGuardada,currentUrl,"consulta subforos");
				urlGuardada.setCantSubForos(tmpSubForos.size());
				
				// procesar pagina 1
				List<UrlGuardada> tmpRes = procesarPaginaIndice(urlGuardada,currentUrl,"index");
				
				// procesar resto paginas del foro
				List<UrlGuardada> tmpResto = procesarRestoPaginas(urlGuardada);
				
				urlGuardada.setCantThreads(tmpRes.size() + tmpResto.size());
				
				
				res.addAll(tmpRes);
			}
			
//			res.forEach(el ->System.out.println( el.getUrl() ));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		return res;
	}
	
	private List<UrlGuardada> procesarRestoPaginas(UrlGuardada urlGuardada) throws IOException {
		// aca levanta la carpeta index
		File input = new File(dbURl+urlGuardada.getUrl()+"/index/");
		List<UrlGuardada> result = new ArrayList<>();
		File[] tmpInputs = input.listFiles();
		if (tmpInputs!= null) {
			for (File paginaFile : tmpInputs) {
				List<UrlGuardada> tmpRes = procesarPaginaIndice(urlGuardada,paginaFile.getAbsolutePath(),paginaFile.getName());
				result.addAll(tmpRes);
			}
		}else{
			log.info("Inputs sin files: "+ input.getAbsolutePath());
		}


		return result;
	}
	private List<UrlGuardada> procesarSubForos(UrlGuardada parentUrl, String currentUrl,String comentario) throws IOException {
		List<UrlGuardada> threads = new ArrayList<>();
		File input = new File(currentUrl);
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

		// https://jsoup.org/cookbook/extracting-data/selector-syntax
		// http://stackoverflow.com/questions/6152671/jsoup-select-div-having-multiple-classes
		Elements indexThreads = doc.select("h2.forumtitle");// los h3. con class="threadtitle"  
//		System.out.println(doc.html());
		indexThreads = indexThreads.select("a");
		
		indexThreads.stream()
		.forEach(el ->threads.add(new UrlGuardada(TipoUrl.SUBFORO,el.html(),el.text(),el.attr("href").replace("https://www.nightclubber.com.ar/", "").replace("/?s=8655145fd7497833abd5c187fcbfacfb", ""))));
		
		
		return threads;
	}
	
	private List<UrlGuardada> procesarPaginaIndice(UrlGuardada parentUrl, String currentUrl,String comentario) throws IOException {
		List<UrlGuardada> threads = new ArrayList<>();
		File input = new File(currentUrl);
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

		// https://jsoup.org/cookbook/extracting-data/selector-syntax
		// http://stackoverflow.com/questions/6152671/jsoup-select-div-having-multiple-classes
		Elements indexThreads = doc.select("h3.threadtitle");// los h3. con class="threadtitle"  
//		System.out.println(doc.html());
		indexThreads = indexThreads.select("a");
		
		indexThreads.stream()
		.forEach(el ->threads.add(new UrlGuardada(TipoUrl.THREAD,el.html(),el.text(),el.attr("href").replace("https://www.nightclubber.com.ar/", "").replace("/?s=8655145fd7497833abd5c187fcbfacfb", ""))));
		
		
		return threads;
	}
	@Test
	public void filtrarArchivosTest(){
		
	}
	
	
	public List<UrlGuardada> filtrarCarpetasAescanear(){
		// arrancar en eventos pasados: 
		// /home/julio/dev/text_mining/nc_eventos_pasados_wget/www.nightclubber.com.ar/foro/48/eventos-pasados/index.html
		// buscar todos los links que tengan "Eventos Pasados" en el value
		// meterlo en un set para que no se repitan nos va a quedar un set de .../www.nightclubber.com.ar/foro/##/nombreSubForo/
		
		// una vez que se tienen las que se llaman eventos pasados, recorrer cad auna de esasa en busca de los temas 
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/index.html
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/##tema/nombretema.html
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/##tema/nombreTema/otros.html
		

//		String filesUrl = dbURl + "foro/48/eventos-pasados/"; > cierra mejor foro/222/eventos-pasados-argentina/
		// porque estan explotados los subforos
		String filesUrl = dbURl + "foro/222/eventos-pasados-argentina/";
		String sampleFile = filesUrl + "index.html";
		List<UrlGuardada> tmp = null;
		try {
			File input = new File(sampleFile);
			// buscar todos los links que tengan "Eventos Pasados" en el value
			
			
			tmp = searchEventosPasados(input);
//			tmp.forEach(el ->System.out.println( gson.toJson(el) ));
//			tmp.forEach(el ->System.out.println( el.getUrl() ));
//			System.out.println(tmp.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}

		return tmp;	
	}
	
	
	private List<UrlGuardada> searchEventosPasados(File input) throws IOException {
//		List<String> ret = new ArrayList<>();
		List<UrlGuardada> retObj = new ArrayList<>();
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

		// https://jsoup.org/cookbook/extracting-data/selector-syntax
		// http://stackoverflow.com/questions/6152671/jsoup-select-div-having-multiple-classes
		Elements titulosForo = doc.select("h2.forumtitle");// los h2. con class="posttitle icon"  
		titulosForo = titulosForo.select("a");
		
		Elements titulosSubforos = doc.select("li.subforum");
		titulosSubforos = titulosSubforos.select("a");
		
		// de los elementos filtrados saco la URL de la pagina y el id de sesion que se uso para explorar todo.
//		titulosForo.stream()
//		.forEach(el ->ret.add(el.text() +  "[ "+ el.attr("href").replace("https://www.nightclubber.com.ar/", "")  +" ]") );
		
		titulosForo.stream()
		.forEach(el ->retObj.add(new UrlGuardada(TipoUrl.FORO,el.html(),el.text(),el.attr("href").replace("https://www.nightclubber.com.ar/", "").replace("/?s=8655145fd7497833abd5c187fcbfacfb", ""))));
		
//		titulosSubforos.stream()
//		.forEach(el ->ret.add(el.text() +  "[ "+ el.attr("href").replace("https://www.nightclubber.com.ar/", "")  +" ]") );
		titulosSubforos.stream()
		.forEach(el ->retObj.add(new UrlGuardada(TipoUrl.SUBFORO,el.html(),el.text(),el.attr("href").replace("https://www.nightclubber.com.ar/", "").replace("/?s=8655145fd7497833abd5c187fcbfacfb", ""))));
		// de lo filtrado tomo el resultado , lo agarro como stream, le filtro los que sean respuestas, los que quedan los imprimo
//		titulosThread.stream()
//			.filter(el -> !el.text().contains("Re:"))
//			//.forEach(el ->System.out.println(el.text()) );
//			.forEach(el ->ret.add(el.text()) );
		return retObj;
	}
	public List<String> escanearArchivosInteres(){
		return null;
	}
	
}
