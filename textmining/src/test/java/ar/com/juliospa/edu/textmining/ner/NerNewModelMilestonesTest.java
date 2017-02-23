package ar.com.juliospa.edu.textmining.ner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.FMeasure;

/**
 * la idea es hacer tests para llegar a mis milestones
 * 
 * <p>Milestone 1: Entrenar un modelo a partir de texto tageado y tener el archivo ( Hola mundo ) 
 * <p>Milestone 2: Aplicar el modelo creado , ver que identifique lo especificado. ( txt con varias palabras y hola mundo ) 
 * <p>Milestone 3: Crear modelo a partir de texto tageado en grande y obtener modelo en archivo ( modelo con nombres extraidos en lo de NER anterior realizado ) 
 * <p>Milestone 4: Aplicar modelo 2 a más de 1 archivo ( la web de NC bajada y ver si levanta esas cosas ) 
 * <p>Milestone 5: Aplicar modelo a web ( A la web de NC no bajada )
 * <p>Milestone 6: Probar en redes tipo twitter, FB
 * 
 * @author jspairani
 *
 */
public class NerNewModelMilestonesTest {
	Logger log = LoggerFactory.getLogger(NerNewModelMilestonesTest.class);
	String modelOutFileName = "C:/Users/jspairani/Dropbox/julio_box/educacion/autodidacta/textmining/NER/modelTest.bin";
	String modelOutFileNameStr = "C:/Users/jspairani/Dropbox/julio_box/educacion/autodidacta/textmining/NER/modelTestStr.bin";
	String trainFile = "C:/Users/jspairani/git/textmining-cuat-4/textmining/src/test/java/ar/com/juliospa/edu/textmining/ner/en-ner-person.train";
	String testFile = "C:/Users/jspairani/git/textmining-cuat-4/textmining/src/test/java/ar/com/juliospa/edu/textmining/ner/docTest.txt";
	String defaultString = "Pierre Vinken , 61 years old , "
			+ "will join the board as a nonexecutive director Nov. 29 .\n"+
			"Mr . Vinken is chairman of Elsevier N.V. , the Dutch publishing group .\n";
	
	String taggedString = "<START:person> Pierre Vinken <END> , 61 years old , "
			+ "will join the board as a nonexecutive director Nov. 29 .\n"
			+ "Mr . <START:person> Vinken <END> is chairman of Elsevier N.V. , the Dutch publishing group .\n";
	
	@Test
	public void m1CrearModeloBaseFile() {
		try {
			
			String charsetName = "UTF-8";
			Charset charset = Charset.forName(charsetName);
			
			// aca me invente yo el string input stream factory. porque no queria archivos.
			// ObjectStream<String> lineStream = new PlainTextByLineStream(new StringInputStreamFactory(taggedString, charset), charset);
			// ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);

			// vuelvo al default porque no me funciono
			//InputStream stream = new ByteArrayInputStream(taggedString.getBytes(StandardCharsets.UTF_8));
			//InputStream fileStream = new FileInputStream("en-ner-person.train");
			File file = new File(trainFile);
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), charset);
			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
			
			//http://www.javased.com/?api=opennlp.tools.namefind.NameFinderME
			TokenNameFinderModel model;
			try {
			  model = NameFinderME.train("en", "person", sampleStream, TrainingParameters.defaultParams(),new TokenNameFinderFactory());
			} finally {
			  sampleStream.close();
			}
			OutputStream modelOut=null;
			try {
			  modelOut = new BufferedOutputStream(new FileOutputStream(new File(modelOutFileName)));
			  model.serialize(modelOut);
			} finally {
			  if (modelOut != null)
				  modelOut.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
	}

	@Test
	public void m2AplicarModeloBaseFile() {
		String charsetName = "UTF-8";
		Charset charset = Charset.forName(charsetName);
		try {
			//ObjectStream<String> lineStream = new PlainTextByLineStream(new StringInputStreamFactory(defaultString, charset), charset);
			File file = new File(testFile);
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), charset);
			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
			TokenNameFinderModel model = new TokenNameFinderModel(new File(modelOutFileName));
			TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(new NameFinderME(model));
			evaluator.evaluate(sampleStream);
			FMeasure result = evaluator.getFMeasure();

			log.info(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		try {
			File file = new File(testFile);
			String doc = new String(Files.readAllBytes(file.toPath()));
			
			TokenNameFinderModel model = new TokenNameFinderModel(new File(modelOutFileName));
			NameFinderME finder = new NameFinderME(model);
			Tokenizer tokenizer = SimpleTokenizer.INSTANCE;

			String[] tokens = tokenizer.tokenize(doc);
			Span[] nameSpans = finder.find(tokens);
			log.info(Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
			log.info("done");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
	@Test
	public void m1CrearModeloBaseString() {
		try {
			
			String charsetName = "UTF-8";
			Charset charset = Charset.forName(charsetName);
			
			// aca me invente yo el string input stream factory. porque no queria archivos.
			ObjectStream<String> lineStream = new PlainTextByLineStream(new StringInputStreamFactory(taggedString, charset), charset);
			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);

			// vuelvo al default porque no me funciono
			//InputStream stream = new ByteArrayInputStream(taggedString.getBytes(StandardCharsets.UTF_8));
			//InputStream fileStream = new FileInputStream("en-ner-person.train");
//			File file = new File(trainFile);
//			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), charset);
//			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
			
			//http://www.javased.com/?api=opennlp.tools.namefind.NameFinderME
			TokenNameFinderModel model;
			try {
			  model = NameFinderME.train("en", "person", sampleStream, TrainingParameters.defaultParams(),new TokenNameFinderFactory());
			} finally {
			  sampleStream.close();
			}
			OutputStream modelOut=null;
			try {
			  modelOut = new BufferedOutputStream(new FileOutputStream(new File(modelOutFileNameStr)));
			  model.serialize(modelOut);
			} finally {
			  if (modelOut != null)
				  modelOut.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
	}
	@Test
	public void m2AplicarModeloBaseString() {
		String charsetName = "UTF-8";
		Charset charset = Charset.forName(charsetName);
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new StringInputStreamFactory(defaultString, charset), charset);
			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
			TokenNameFinderModel model = new TokenNameFinderModel(new File(modelOutFileNameStr));
			TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(new NameFinderME(model));
			evaluator.evaluate(sampleStream);
			FMeasure result = evaluator.getFMeasure();

			log.info(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		try {
			
			TokenNameFinderModel model = new TokenNameFinderModel(new File(modelOutFileNameStr));
			NameFinderME finder = new NameFinderME(model);
			Tokenizer tokenizer = SimpleTokenizer.INSTANCE;

			String[] tokens = tokenizer.tokenize(defaultString);
			Span[] nameSpans = finder.find(tokens);
			log.info(Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
			log.info("done");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
}
