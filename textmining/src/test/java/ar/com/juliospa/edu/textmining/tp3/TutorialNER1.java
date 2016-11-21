package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * esto es para seguir el tutorial de NER que se encuentra aca:
 * http://technobium.com/getting-started-with-apache-opennlp/
 * @author julio
 *
 */
public class TutorialNER1 {

	/**
	 * esto es para ejecutar lo que en el tutorial esta como MAIN, 
	 * pero para ejecutarlo como jUnit Test
	 * prerequisistos 
	 * - haber agregado las dependencias al POM de maven
	 * - haber bajado el modelo en-ner-person, de la pagina que dice el tuto ( tambien hay otros cuantos )
	 * 
	 * fuente de modelos: http://opennlp.sourceforge.net/models-1.5/
	 * 
	 */
	@Test
	public void tutoMain() {

		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String modelUrl = dBoxUrl+"NER/models/en-ner-person.bin";

		try {
			Logger log = LoggerFactory.getLogger(TutorialNER1.class);

			String[] sentences = {
					"If President John F. Kennedy, after visiting France in 1961 with his immensely popular wife,"
							+ " famously described himself as 'the man who had accompanied Jacqueline Kennedy to Paris,'"
							+ " Mr. Hollande has been most conspicuous on this state visit for traveling alone.",
					"Mr. Draghi spoke on the first day of an economic policy conference here organized by"
							+ " the E.C.B. as a sort of counterpart to the annual symposium held in Jackson"
							+ " Hole, Wyo., by the Federal Reserve Bank of Kansas City. " };

			// Load the model file downloaded from OpenNLP
			// http://opennlp.sourceforge.net/models-1.5/en-ner-person.bin
			procesarString(modelUrl, log, sentences);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("revento! por :" + e.getMessage());
		}
	}

	private void procesarString(String modelUrl, Logger log, String[] sentences)
			throws IOException, InvalidFormatException {
		TokenNameFinderModel model = new TokenNameFinderModel(new File(modelUrl));

		// Create a NameFinder using the model
		NameFinderME finder = new NameFinderME(model);

		Tokenizer tokenizer = SimpleTokenizer.INSTANCE;

		for (String sentence : sentences) {

			// Split the sentence into tokens
			String[] tokens = tokenizer.tokenize(sentence);

			// Find the names in the tokens and return Span objects
			Span[] nameSpans = finder.find(tokens);

			// Print the names extracted from the tokens using the Span data
			log.info(Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
		}
	}

}