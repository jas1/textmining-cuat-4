package ar.com.juliospa.edu.textmining.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.json.JSONObject;
import org.json.XML;

import ar.com.juliospa.edu.textmining.domain.ExpectedResult;
import ar.com.juliospa.edu.textmining.domain.QueryString;
import ar.com.juliospa.edu.textmining.domain.QueryStringCollection;
import edu.stanford.nlp.process.Stemmer;

/** utilidades varias para text mining
 * 
 * @author julio
 *
 */
public class TextMiningUtils {
	public static final String TAB = "\t";
	public static final String ENTER = "\n";
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

}
