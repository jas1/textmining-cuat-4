package ar.com.juliospa.edu.textmining.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.json.JSONObject;
import org.json.XML;

import ar.com.juliospa.edu.textmining.domain.tp1.ExpectedResult;
import ar.com.juliospa.edu.textmining.domain.tp1.QueryString;
import ar.com.juliospa.edu.textmining.domain.tp1.QueryStringCollection;
import edu.stanford.nlp.process.Stemmer;

/** utilidades varias para text mining
 * 
 * @author julio
 *
 */
public class TextMiningUtils {
	public static final String TAB = "\t";
	public static final String ENTER = "\n";
	public static String RES_KEY_TOTAL_SIZE = "totalSize";
	public static String RES_KEY_HTML = "html";
	public static String RES_KEY_PHP = "php";
	public static String RES_KEY_OTHER = "otros";
	public static String RES_KEY_HTML_SIZE = "htmlSize";
	public static String RES_KEY_PHP_SIZE = "phpSize";
	public static String RES_KEY_OTHER_SIZE = "otrosSize";
	/**
	 * escanea la carpeta para ver los archivos que hay
	 * 
	 * @return
	 */
	public static Map<String, Long> resumenForFiles(String aPath) {
		Path path = Paths.get(aPath);
		Map<String, Long> mapa = new HashMap<String, Long>();
		

		mapa.put(RES_KEY_TOTAL_SIZE, 0l);
		mapa.put(RES_KEY_HTML, 0l);
		mapa.put(RES_KEY_PHP, 0l);
		mapa.put(RES_KEY_OTHER, 0l);
		mapa.put(RES_KEY_HTML_SIZE, 0l);
		mapa.put(RES_KEY_PHP_SIZE, 0l);
		mapa.put(RES_KEY_OTHER_SIZE, 0l);
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (!attrs.isDirectory()) {
						if (file.toFile().getName().contains(RES_KEY_HTML) ){
							mapa.put(RES_KEY_HTML, mapa.get(RES_KEY_HTML)+1);
							mapa.put(RES_KEY_TOTAL_SIZE, mapa.get(RES_KEY_TOTAL_SIZE)+file.toFile().length());
							mapa.put(RES_KEY_HTML_SIZE, mapa.get(RES_KEY_HTML_SIZE)+file.toFile().length());
						}else
						if (file.toFile().getName().contains(RES_KEY_PHP)) {
							mapa.put(RES_KEY_PHP, mapa.get(RES_KEY_PHP)+1);
							mapa.put(RES_KEY_TOTAL_SIZE, mapa.get(RES_KEY_TOTAL_SIZE)+file.toFile().length());
							mapa.put(RES_KEY_PHP_SIZE, mapa.get(RES_KEY_PHP_SIZE)+file.toFile().length());
						}else {
							mapa.put(RES_KEY_OTHER, mapa.get(RES_KEY_OTHER)+1);
							mapa.put(RES_KEY_TOTAL_SIZE, mapa.get(RES_KEY_TOTAL_SIZE)+file.toFile().length());
							mapa.put(RES_KEY_OTHER_SIZE, mapa.get(RES_KEY_OTHER_SIZE)+file.toFile().length());
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapa;
	}
	/**
	 * escanea la carpeta para ver los archivos que hay
	 * 
	 * @return
	 */
	public static List<Path> scanForFiles(String aPath) {
		Path path = Paths.get(aPath);
		final List<Path> files = new ArrayList<>();
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (!attrs.isDirectory()) {
						files.add(file);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}
	/**
	 * para transformar los objetos en XML
	 * @param pathOutMeasures path donde va a ir el xml resultante
	 * @param fileOutMeasures nombre que va a tener el archivo vinal
	 * @param measures objeto a transformar
	 * @param classToMarshal clase del objeto a transformar
	 * @throws JAXBException revienta en el procesamiento XML
	 * @throws PropertyException revienta por otra cosa 
	 */
	public static void objectsToXml(String pathOutMeasures, String fileOutMeasures, Object measures,
			Class<?> classToMarshal) throws JAXBException, PropertyException {
		// guardar measures en xml
		File file = new File(pathOutMeasures + fileOutMeasures);

		JAXBContext jaxbContext = JAXBContext.newInstance(classToMarshal);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(measures, file);
		System.out.println(file.getAbsoluteFile());
	}

	/**
	 * para hacerle el stemming a una oracion pseudo a mano.
	 * @param tmp
	 * @return
	 */
	public static String stemSentence(String tmp) {
		if (tmp != null && tmp.length() > 0) {
			Stemmer asd = new Stemmer();
			String[] tmplist = tmp.split(" ");
			List<String> acum = new ArrayList<>();
			Arrays.asList(tmplist).stream().forEach(str -> acum.add(asd.stem(str)));
			return String.join(" ", acum);
		}
		return tmp;
	}

	/**
	 * para sacar las stopwords de una oracion
	 * @param tmp oracion
	 * @param stopwords listado de stopwords
	 * @return oracion sin stopwords
	 */
	public static String removeStopWords(String tmp, List<String> stopwords) {
		if (tmp != null && tmp.length() > 0) {

			List<String> acum = new ArrayList<>();
			// 1ro necesito llevarlo a minusculas
			String[] currentWords = tmp.toLowerCase().split(" ");

			for (String cw : currentWords) {
				// le saco todos los stopwords.
				boolean flagRemove = false;
				for (String sw : stopwords) {
					// necesito remover palabras enteras
					if (!flagRemove) {
						if (cw.trim().equals(sw.trim())) {
							flagRemove= true;
						}
					}
				}
				// si no se remueve se agrega al final 
				if (!flagRemove) {
					acum.add(cw.trim());
				}
			}
			// junto todas las palabras.
			return String.join(" ", acum);
		}
		return tmp;
	}

	/**
	 *  devuleve la lista de esperados para la query especifica
	 * @param query
	 * @param expectedResult
	 * @return
	 */
	public static List<ExpectedResult> getExpectedResultsForQueryNumber(QueryString query,
			List<ExpectedResult> expectedResult) {
		List<ExpectedResult> expectedResultForQuery = expectedResult.stream()
			.filter(er -> er.getQueryId().equals(query.getNumber()))
			.collect(Collectors.toList());
		return expectedResultForQuery;
	}

	/**
	 * devuleve la lista de esperados para la query especifica
	 * @param queries
	 * @param expectedResult
	 * @param queryNumber
	 * @return
	 */
	public static List<ExpectedResult> getExpectedResultsForQueryNumber(QueryStringCollection queries,
			List<ExpectedResult> expectedResult, int queryNumber) {
		return getExpectedResultsForQueryNumber(queries.getTops().get(queryNumber),expectedResult);
	}
	/**
	 * xml file to json string
	 * @param link xml file url
	 * @return json string
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String xmlToJson(String link) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(link));
		String line = "";
		String str = "";
		while ((line = br.readLine()) != null) 
		{   
		    str+=line;  
		}
		JSONObject jsondata = XML.toJSONObject(str);
		return jsondata.toString();
	}
	public static void writeBytesToFile(String pathFileName, byte[] bytesToWrite) {
		File fileOut = new File(pathFileName);
		
		try (FileOutputStream fos = new FileOutputStream(fileOut)) {
			fos.write(bytesToWrite);
			fos.flush();
			fos.close();
			System.out.println(fileOut.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void mostrarEntryResumenCrawling(Entry<String, Long> entry) {
		if (entry.getKey().contains("Size")) {
			double bytes = entry.getValue();
			double kilobytes = (bytes / 1024);
			double megabytes = (kilobytes / 1024);
			double gigabytes = (megabytes / 1024);
			BigDecimal dec = new BigDecimal(bytes);
			System.out.println(entry.getKey()+ TAB+ dec.toPlainString() + TAB +kilobytes+ TAB+megabytes + TAB+gigabytes);
		}else{
			System.out.println(entry.getKey() + TAB +entry.getValue());
		}
	}

}
