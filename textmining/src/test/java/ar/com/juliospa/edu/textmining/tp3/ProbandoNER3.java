package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

/**
 * problema anterior: levanta el HTML crudo > por lo tanto corre el NER sobre
 * demasiada metadata IDEA; es agregar un parser para que el problema que tengo
 * en ProbandoNER2 quede resuelto entonces parser html para que elimine toda la
 * mugre, luego lo que terimna siendo contenidos se lo paso al NER. ejemplo:
 * https://www.tutorialspoint.com/tika/tika_extracting_html_document.htm
 * 
 * @author julio
 *
 */
public class ProbandoNER3 {
	Logger log = LoggerFactory.getLogger(ProbandoNER3.class);

	/**
	 * primera corrida de solo parser: podemos ver que en la metadata esta la
	 * informacion necesaria, no hizo falta usar los datos !
	 * 
	 */
	@Test
	public void probandoConParser() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String modelUrl = dBoxUrl + "NER/models/es-ner-person.bin";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		String sampleFile = filesUrl + "/viernes-23-05-14-alan-fitzpatrick-gala-cordoba.html";
		List<String> docs = getMyDocsFromSomewhere(filesUrl);

		try {
			// detecting the file type
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			FileInputStream inputstream = new FileInputStream(new File(sampleFile));
			ParseContext pcontext = new ParseContext();

			// Html parser
			HtmlParser htmlparser = new HtmlParser();
			htmlparser.parse(inputstream, handler, metadata, pcontext);
			System.out.println("Contents of the document:" + handler.toString());
			System.out.println("Metadata of the document:");
			String[] metadataNames = metadata.names();

			for (String name : metadataNames) {
				System.out.println(name + ":   " + metadata.get(name));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}

	}

	@Test
	public void probandoNERConParser() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String modelUrl = dBoxUrl + "NER/models/es-ner-person.bin";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		List<String> docs = getMyDocsFromSomewhere(filesUrl);

		try {
			TokenNameFinderModel model = new TokenNameFinderModel(new File(modelUrl));
			NameFinderME finder = new NameFinderME(model);
			Tokenizer tokenizer = SimpleTokenizer.INSTANCE;

			for (String doc : docs) {
				String[] tokens = tokenizer.tokenize(doc);
				Span[] nameSpans = finder.find(tokens);
				log.info(Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	/** 
	 * agrego como documentos lo parseado
	 * contents y metadata
	 * 
	 * @param aPath
	 * @return
	 */
	private List<String> getMyDocsFromSomewhere(String aPath) {
		List<String> ret = new ArrayList<>();
		try {
			File startFileUrl = new File(aPath);
			File[] files = startFileUrl.listFiles();
			for (File file : files) {
				
				BodyContentHandler handler = new BodyContentHandler();
				Metadata metadata = new Metadata();
				FileInputStream inputstream = new FileInputStream(file);
				ParseContext pcontext = new ParseContext();

				// Html parser
				HtmlParser htmlparser = new HtmlParser();
				htmlparser.parse(inputstream, handler, metadata, pcontext);
				// System.out.println("Contents of the document:" +
				// handler.toString());
				// System.out.println("Metadata of the document:");
				String[] metadataNames = metadata.names();
				StringBuilder build =  new StringBuilder();
				for (String name : metadataNames) {
					build.append(metadata.get(name));
				}
				
				ret.add(handler.toString());
				ret.add(build.toString());
			}
		} catch (Exception e) {
			System.out.println("--error: " + aPath);
			System.out.println("--error: " + e.getMessage());
			e.printStackTrace();
		}

		return ret;
	}

}
