package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * la idea de este es probar NER para mi caso especifico html en espaniol.
 * 
 * prerequisito: - lo mismo que para el tutorial pero probando con otro modelo -
 * tener archivo html en espaniol para probar
 * 
 * @author julio
 *
 */
public class ProbandoNER2 {
	Logger log = LoggerFactory.getLogger(ProbandoNER2.class);
	/**
	 * aca saco data de como leer desde aca:
	 * http://stackoverflow.com/questions/19293425/how-to-read-document-for-named-entity-recognition-in-opennlp
	 * 
	 * adaptado a lo que me interesa a mi.
	 * 
	 * resultado: trae demasiada mugre que no me interesa, que son cosas de WEB.
	 * lo que me hace pensar que hay que tomar los datos del thread y posts no me interesan los menues y esas cosas
	 * 
	 * ver si con esto puedo limitarlo con alguna otra herramienta
	 */
	@Test
	public void tutoMain() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String modelUrl = dBoxUrl + "NER/models/es-ner-person.bin";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		


		try {
			System.out.println("getting data");
			List<String> docs = getMyDocsFromSomewhere(filesUrl);
			System.out.println("\tdone getting data");

			TokenNameFinderModel model = new TokenNameFinderModel(new File(modelUrl));
			NameFinderME finder = new NameFinderME(model);
			Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
			
			for (String doc : docs) {
				String[] tokens = tokenizer.tokenize(doc);
				Span[] nameSpans = finder.find(tokens);
				log.info(Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
			}

			System.out.println("done");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	private List<String> getMyDocsFromSomewhere(String aPath){
		List<String> ret = new ArrayList<>(); 
		try {
			File startFileUrl = new File(aPath);
			File[] files = startFileUrl.listFiles();
			for (File file : files) {
				ret.add(new String(Files.readAllBytes(Paths.get(file.getPath()))));
			}
		} catch (IOException e) {
			System.out.println("--error: "+ aPath);
			System.out.println("--error: "+ e.getMessage());
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private void procesarString(String modelUrl, String[] sentences)
			throws IOException, InvalidFormatException {


		for (String sentence : sentences) {


		}
	}

}
