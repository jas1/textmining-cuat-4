package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import junit.framework.AssertionFailedError;

/**
 * regex con jsoup al estilo jquery
 * 
 * @author julio
 *
 */
public class ProbandoNER9 {
	Logger log = LoggerFactory.getLogger(ProbandoNER9.class);
	static {
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yy-MM-dd HH:mm:ss.SSS");
	}
	Gson gson = new Gson();
	Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
	String dbURl = "/home/julio/dev/text_mining/nc_eventos_pasados_wget/www.nightclubber.com.ar/";
	String outputFolder = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
	/*
	 * pipeline de trabajo 1) filtrar que carpetas me interesa escanear 2)
	 * escanar esas carpetas por los archivos que me interesan 3) procesar los
	 * archivos con jsoup para guardar base 4) de esa base generada > custom
	 * regex. 5) volver a probar ner > elaborar un modelo propio 6) conclusiones
	 */

	@Test
	public void filtrarForosTest() {
		// ya tengo foros y subforos
		List<UrlGuardada> resultForos = filtrarCarpetasAescanear();
		System.out.println(gson.toJson(resultForos));
	}

	@Test
	public void filtrarThreadsTest() {
		String json = "[{\"tipo\":\"FORO\",\"fuenteOriginal\":\"Eventos Pasados del 2004\",\"url\":\"foro/40/eventos-pasados-del-2004-a\",\"nombreOriginal\":\"Eventos Pasados del 2004\",\"observacion\":\"parseForo\",\"cantSubForos\":0,\"cantThreads\":0}]";

		List<UrlGuardada> resultForos = gson.fromJson(json, new TypeToken<List<UrlGuardada>>() {
		}.getType());
		// recorrer threads
		List<UrlGuardada> resultThreads = filtrarThredsURL(resultForos);
		System.out.println(gson.toJson(resultThreads));
	}

	@Test
	public void procesarThreadTestFecha1() {
		String jsonThread = "[{\"tipo\":\"THREAD\",\"fuenteOriginal\":\"Viernes 19.11.2014 - Resident\u0027s Night @ Mint\",\"url\":\"foro/40/eventos-pasados-del-2004-a/600/viernes-19-11-04-residents-night-mint.html\",\"nombreOriginal\":\"Viernes 19.11.2014 - Resident\u0027s Night @ Mint\",\"observacion\":\"parseThread|index\",\"cantSubForos\":0,\"cantThreads\":0}]";

		List<UrlGuardada> resultThreads = gson.fromJson(jsonThread, new TypeToken<List<UrlGuardada>>() {
		}.getType());
		// recorrer threads
		OutputProcessNER out = patternRegexNer(resultThreads);

		System.out.println(gsonPretty.toJson(out.getSetResultNers()));

	}

	@Test
	public void procesarThreadTestFecha2() {
		String jsonThread = "[{\"tipo\":\"THREAD\",\"fuenteOriginal\":\"Viernes 19.11.04 - Resident\u0027s Night @ Mint\",\"url\":\"foro/40/eventos-pasados-del-2004-a/600/viernes-19-11-04-residents-night-mint.html\",\"nombreOriginal\":\"Viernes 19.11.04 - Resident\u0027s Night @ Mint\",\"observacion\":\"parseThread|index\",\"cantSubForos\":0,\"cantThreads\":0}]";

		List<UrlGuardada> resultThreads = gson.fromJson(jsonThread, new TypeToken<List<UrlGuardada>>() {
		}.getType());
		// recorrer threads
		OutputProcessNER out = patternRegexNer(resultThreads);

		
		
		System.out.println(gsonPretty.toJson(out.getSetResultNers()));
	}

	@Test
	public void todoJuntoProcesar() {
		// TODO: ya estan los subforos, ahora buscar los threads / posts

		// ya tengo foros y subforos
		List<UrlGuardada> resultForos = filtrarCarpetasAescanear();

		// recorrer threads
		List<UrlGuardada> resultThreads = filtrarThredsURL(resultForos);

		OutputProcessNER out = patternRegexNer(resultThreads);

		StringBuilder build = new StringBuilder();
		build.append("Resumen\n");
		build.append(out.showResumen()).append("\n").append("\n");
		build.append(armarFrecuenciasNER(out));
		build.append("Raros\n");
		build.append(gsonPretty.toJson(out.getCasosRaros())).append("\n").append("\n");
		build.append("no Clasifica : getNoClasificaEvento\n");
		build.append(gsonPretty.toJson(out.getNoClasificaEvento())).append("\n").append("\n");	
		build.append("no Clasifica : getNoClasificaFechas\n");
		build.append(gsonPretty.toJson(out.getNoClasificaFechas())).append("\n").append("\n");	
		build.append("no Clasifica : getNoClasificaUbicacion\n");
		build.append(gsonPretty.toJson(out.getNoClasificaUbicacion())).append("\n").append("\n");			
//		System.out.println();
		try {
			String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
			Files.write(Paths.get(outputFolder+timeStamp+"_resumen_manual.txt"), build.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

		// System.out.println(gsonPretty.toJson(out.getSetResultNers()));
	}

	private String armarFrecuenciasNER(OutputProcessNER out){
		
		Map<String, Long> mapaLugar8 =
				out.getSetResultNers().stream()
						.collect(Collectors.groupingBy(UrlGuardada::getLugarEventoStr, Collectors.counting()));
		Map<String, Long> mapaEvento8 =
				out.getSetResultNers().stream()
						.collect(Collectors.groupingBy(UrlGuardada::getNombreEventoStr, Collectors.counting()));
		Map<String, Long> mapaFecha8 =
				out.getSetResultNers().stream()
						.collect(Collectors.groupingBy(UrlGuardada::getFechaStr, Collectors.counting()));
		
		
		StringBuilder build = new StringBuilder();
		build.append("Frecuencia entidades:").append("\n");
		build.append("Lugares:").append("\n");
		mapaLugar8.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
			.forEach(en->build.append("\t").append(en.getKey()).append(": ").append(en.getValue()).append("\n"));
		build.append("\n").append("Eventos:").append("\n");
		mapaEvento8.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
			.forEach(en->build.append("\t").append(en.getKey()).append(": ").append(en.getValue()).append("\n"));		
		build.append("\n").append("Fechas:").append("\n");
		mapaFecha8.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
			.forEach(en->build.append("\t").append(en.getKey()).append(": ").append(en.getValue()).append("\n"));				
		return build.toString();
	}

	/**
	 * devuelve set porque no deben repetirse los eventos.
	 * 
	 * @param resultThreads
	 * @return
	 */
	private OutputProcessNER patternRegexNer(List<UrlGuardada> resultThreads) {
		OutputProcessNER output = new OutputProcessNER();

		List<UrlGuardada> res = new ArrayList<>();

		// pattern 1 : fechas
		resultThreads.stream()
				.forEach(el -> patternFecha(el, res, output.getNoClasificaFechas(), output.getCasosRaros()));
		output.getNoClasificaFechas().stream()
				.forEach(notFecha -> log.warn("noClasificaFechas:\n" + gson.toJson(notFecha)));

		// pattern 2 : location @ al fin
		resultThreads.stream()
				.forEach(el -> patternUbicacion(el, res, output.getNoClasificaUbicacion(), output.getCasosRaros()));
		output.getNoClasificaUbicacion().stream()
				.forEach(notFecha -> log.warn("noClasificaUbicacion:\n" + gson.toJson(notFecha)));

		// pattern 3: que evento
		resultThreads.stream()
				.forEach(el -> patternEvento(el, res, output.getNoClasificaEvento(), output.getCasosRaros()));
		output.getNoClasificaEvento().stream()
				.forEach(notFecha -> log.warn("noClasificaEvento:\n" + gson.toJson(notFecha)));

		output.getCasosRaros().stream().forEach(notFecha -> log.warn("casosRaros:\n" + gson.toJson(notFecha)));

		output.setSetResultNers(new HashSet<>(res));

		return output;
	}

	private void patternEvento(UrlGuardada el, List<UrlGuardada> res, Set<UrlGuardada> noClasificaEvento,
			Set<UrlGuardada> casosRaros) {
		// ubicacion pattern 1: fecha - evento o quien @ ubicacion > - evento o
		// quien @
		String txtEval = el.getNombreOriginal();
		if (txtEval.contains("@") && txtEval.contains("-")) {
			int subIdx1 = txtEval.indexOf("-") + 1;
			int subIdx2 = txtEval.indexOf("@");
			if (subIdx1 < subIdx2) {
				el.setNombreEvento(txtEval.substring(subIdx1, subIdx2).trim());
				el.setObservacion(el.getObservacion() + "|eventoBy:" + "contains - AND @");
				res.add(el);
			} else {
				el.setObservacion(el.getObservacion() + "|error:eventoBy:" + "contains - AND @:" + "- despues de @ ");
				noClasificaEvento.add(el);
			}

		} else {
			noClasificaEvento.add(el);
		}
	}

	private void patternUbicacion(UrlGuardada el, List<UrlGuardada> res, Set<UrlGuardada> noClasificaUbicacion,
			Set<UrlGuardada> casosRaros) {

		// ubicacion pattern 1: fecha - juanito @ ubicacion
		String txtEval = el.getNombreOriginal();
		if (txtEval.contains("@")) {
			el.setLugarEvento(txtEval.substring(txtEval.indexOf("@") + 1).trim());
			el.setObservacion(el.getObservacion() + "|lugarBy:" + "contains @");
			res.add(el);
		} else {
			noClasificaUbicacion.add(el);
		}
	}

	private Date patternFechasMatching(String strToEval, String datePatternMatches, String regexPattern)
			throws MatcherTimesException, ParseException {

		Date ret = null;

		Pattern regexPat = Pattern.compile(regexPattern);
		Matcher matcher = regexPat.matcher(strToEval);

		SimpleDateFormat formatDate = new SimpleDateFormat(datePatternMatches);

		boolean masDeUna = false;

		while (matcher.find()) {
			if (masDeUna) {
				throw new MatcherTimesException(
						"|fecha matcher 2 veces o mas para:" + datePatternMatches + ":" + strToEval);
				// log.warn("|fecha matcher 2 veces o mas para:"+
				// datePatternMatches);
			} else {
				String tmp = strToEval.substring(matcher.start(), matcher.end());
				ret = formatDate.parse(tmp);
			}
			masDeUna = true;
		}
		return ret;
	}

	private void patternFecha(UrlGuardada el, List<UrlGuardada> res, Set<UrlGuardada> noClasificaFechas,
			Set<UrlGuardada> casosRaros) {
		// 1ro extraigo el patron que me interesa parsear, luego parseo a fecha
		// para que sea valida
		String datePatternMatches1 = "dd.MM.yy";
		String datePattern1Regex = "[0-9]{2}.[0-9]{2}.[0-9]{4}"; // dd.MM.yyyy
		boolean parsed = patternFechasResult(el, res, noClasificaFechas, casosRaros, datePatternMatches1,
				datePattern1Regex);

		if (!parsed) {
			// en este hace falta corregir los ultimos 2 string anteponiendo 20
			String datePatternMatches2 = "dd.MM.yy";
			String datePattern2Regex = "[0-9]{2}.[0-9]{2}.[0-9]{2}"; // dd.MM.yy

			parsed = patternFechasResult(el, res, noClasificaFechas, casosRaros, datePatternMatches2,
					datePattern2Regex);
		}

		if (!parsed) {
			noClasificaFechas.add(el);
		}

	}

	private boolean patternFechasResult(UrlGuardada el, List<UrlGuardada> res, Set<UrlGuardada> noClasificaFechas,
			Set<UrlGuardada> casosRaros, String datePatternMatches1, String datePattern1Regex) {
		boolean ret = false;
		Date tmp1 = null;
		try {
			tmp1 = patternFechasMatching(el.getNombreOriginal(), datePatternMatches1, datePattern1Regex);
		} catch (MatcherTimesException e) {
			el.setObservacion(el.getObservacion() + e.getMessage());
			casosRaros.add(el);
			e.printStackTrace();
		} catch (ParseException e) {
			el.setObservacion(el.getObservacion() + e.getMessage());
			e.printStackTrace();
		}

		if (tmp1 == null) {
			// noClasificaFechas.add(el);
			// porque todavia quedan evaluar diferentes patrones
		} else {
			el.setFecha(tmp1);
			el.setObservacion(el.getObservacion() + "|fechaBy:" + datePattern1Regex);
			res.add(el);
			ret = true;
		}
		return ret;
	}

	private List<UrlGuardada> filtrarThredsURL(List<UrlGuardada> resultForos) {
		List<UrlGuardada> res = new ArrayList<>();
		// de cada foro tengo que recorrer el indice correspondiente :
		// /index.html
		// del indice tengo que fijarme si tiene mas paginas:
		// \index\pagina##,html > siendo ## la cantidad de paginas , igual hay
		// que recorrerlas a todas. seria un foreach de las paginas en /index/
		try {

			for (UrlGuardada urlGuardada : resultForos) {

				// procesar subforos
				String currentUrl = dbURl + urlGuardada.getUrl() + "/index.html";
				List<UrlGuardada> tmpSubForos = procesarSubForos(urlGuardada, currentUrl, "consulta subforos");
				urlGuardada.setCantSubForos(tmpSubForos.size());

				// procesar pagina 1
				List<UrlGuardada> tmpRes = procesarPaginaIndice(urlGuardada, currentUrl, "index");

				// procesar resto paginas del foro
				List<UrlGuardada> tmpResto = procesarRestoPaginas(urlGuardada);

				urlGuardada.setCantThreads(tmpRes.size() + tmpResto.size());

				res.addAll(tmpRes);
			}

			// res.forEach(el ->System.out.println( el.getUrl() ));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		return res;
	}

	private List<UrlGuardada> procesarRestoPaginas(UrlGuardada urlGuardada) throws IOException {
		// aca levanta la carpeta index
		File input = new File(dbURl + urlGuardada.getUrl() + "/index/");
		List<UrlGuardada> result = new ArrayList<>();
		File[] tmpInputs = input.listFiles();
		if (tmpInputs != null) {
			for (File paginaFile : tmpInputs) {
				List<UrlGuardada> tmpRes = procesarPaginaIndice(urlGuardada, paginaFile.getAbsolutePath(),
						paginaFile.getName());
				result.addAll(tmpRes);
			}
		} else {
			log.info("Inputs sin files: " + input.getAbsolutePath());
		}

		return result;
	}

	private List<UrlGuardada> procesarSubForos(UrlGuardada parentUrl, String currentUrl, String comentario)
			throws IOException {
		List<UrlGuardada> threads = new ArrayList<>();
		File input = new File(currentUrl);
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

		// https://jsoup.org/cookbook/extracting-data/selector-syntax
		// http://stackoverflow.com/questions/6152671/jsoup-select-div-having-multiple-classes
		Elements indexThreads = doc.select("h2.forumtitle");// los h3. con
															// class="threadtitle"
		// System.out.println(doc.html());
		indexThreads = indexThreads.select("a");

		indexThreads.stream()
				.forEach(
						el -> threads.add(new UrlGuardada(TipoUrl.SUBFORO, el.html(), el.text(),
								el.attr("href").replace("https://www.nightclubber.com.ar/", "")
										.replace("/?s=8655145fd7497833abd5c187fcbfacfb", ""),
								"parseSubforoInForum|" + comentario)));

		return threads;
	}

	private List<UrlGuardada> procesarPaginaIndice(UrlGuardada parentUrl, String currentUrl, String comentario)
			throws IOException {
		List<UrlGuardada> threads = new ArrayList<>();
		File input = new File(currentUrl);
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

		// https://jsoup.org/cookbook/extracting-data/selector-syntax
		// http://stackoverflow.com/questions/6152671/jsoup-select-div-having-multiple-classes
		Elements indexThreads = doc.select("h3.threadtitle");// los h3. con
																// class="threadtitle"
		// System.out.println(doc.html());
		indexThreads = indexThreads.select("a");

		indexThreads.stream()
				.forEach(
						el -> threads.add(new UrlGuardada(TipoUrl.THREAD,
								el.html(), el.text(), el.attr("href").replace("https://www.nightclubber.com.ar/", "")
										.replace("/?s=8655145fd7497833abd5c187fcbfacfb", ""),
								"parseThread|" + comentario)));

		return threads;
	}

	@Test
	public void filtrarArchivosTest() {

	}

	public List<UrlGuardada> filtrarCarpetasAescanear() {
		// arrancar en eventos pasados:
		// /home/julio/dev/text_mining/nc_eventos_pasados_wget/www.nightclubber.com.ar/foro/48/eventos-pasados/index.html
		// buscar todos los links que tengan "Eventos Pasados" en el value
		// meterlo en un set para que no se repitan nos va a quedar un set de
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/

		// una vez que se tienen las que se llaman eventos pasados, recorrer cad
		// auna de esasa en busca de los temas
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/index.html
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/##tema/nombretema.html
		// .../www.nightclubber.com.ar/foro/##/nombreSubForo/##tema/nombreTema/otros.html

		// String filesUrl = dbURl + "foro/48/eventos-pasados/"; > cierra mejor
		// foro/222/eventos-pasados-argentina/
		// porque estan explotados los subforos
		String filesUrl = dbURl + "foro/222/eventos-pasados-argentina/";
		String sampleFile = filesUrl + "index.html";
		List<UrlGuardada> tmp = null;
		try {
			File input = new File(sampleFile);
			// buscar todos los links que tengan "Eventos Pasados" en el value

			tmp = searchEventosPasados(input);
			// tmp.forEach(el ->System.out.println( gson.toJson(el) ));
			// tmp.forEach(el ->System.out.println( el.getUrl() ));
			// System.out.println(tmp.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

		return tmp;
	}

	private List<UrlGuardada> searchEventosPasados(File input) throws IOException {
		// List<String> ret = new ArrayList<>();
		List<UrlGuardada> retObj = new ArrayList<>();
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

		// https://jsoup.org/cookbook/extracting-data/selector-syntax
		// http://stackoverflow.com/questions/6152671/jsoup-select-div-having-multiple-classes
		Elements titulosForo = doc.select("h2.forumtitle");// los h2. con
															// class="posttitle
															// icon"
		titulosForo = titulosForo.select("a");

		Elements titulosSubforos = doc.select("li.subforum");
		titulosSubforos = titulosSubforos.select("a");

		// de los elementos filtrados saco la URL de la pagina y el id de sesion
		// que se uso para explorar todo.
		// titulosForo.stream()
		// .forEach(el ->ret.add(el.text() + "[ "+
		// el.attr("href").replace("https://www.nightclubber.com.ar/", "") +"
		// ]") );

		titulosForo.stream()
				.forEach(el -> retObj.add(new UrlGuardada(TipoUrl.FORO, el.html(), el.text(),
						el.attr("href").replace("https://www.nightclubber.com.ar/", "")
								.replace("/?s=8655145fd7497833abd5c187fcbfacfb", ""),
						"parseForo")));

		// titulosSubforos.stream()
		// .forEach(el ->ret.add(el.text() + "[ "+
		// el.attr("href").replace("https://www.nightclubber.com.ar/", "") +"
		// ]") );
		titulosSubforos.stream()
				.forEach(el -> retObj.add(new UrlGuardada(TipoUrl.SUBFORO, el.html(), el.text(),
						el.attr("href").replace("https://www.nightclubber.com.ar/", "")
								.replace("/?s=8655145fd7497833abd5c187fcbfacfb", ""),
						"parseSubforo")));
		// de lo filtrado tomo el resultado , lo agarro como stream, le filtro
		// los que sean respuestas, los que quedan los imprimo
		// titulosThread.stream()
		// .filter(el -> !el.text().contains("Re:"))
		// //.forEach(el ->System.out.println(el.text()) );
		// .forEach(el ->ret.add(el.text()) );
		return retObj;
	}

	public List<String> escanearArchivosInteres() {
		return null;
	}

}
