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

import org.junit.Test;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
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

	/**
	 * aca saco data de como leer desde aca:
	 */
	@Test
	public void tutoMain() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String modelUrl = dBoxUrl + "NER/models/es-ner-person.bin";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		

		System.out.println("getting data");
		List<String> docs = getMyDocsFromSomewhere(filesUrl);
		System.out.println("\tdone getting data");

//		for (String string : docs) {
//			System.out.println(string);
//		}
		
//		for (String docu : docs) {
//			// you could also use the runnable here and launch in a diff thread
//			// new OpenNLPNER(docu,
//			// new SentenceDetectorME(new SentenceModel(new FileInputStream(new
//			// File(modelPath + "en-sent.zip")))),
//			// new NameFinderME(locModel), new TokenizerME(tm)).run();
//
//		}

		System.out.println("done");

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

}
