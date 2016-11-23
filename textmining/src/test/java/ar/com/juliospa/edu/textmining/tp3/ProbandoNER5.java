package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ar.com.juliospa.edu.textmining.tp3.ner.ModelAppliedOutput;
import ar.com.juliospa.edu.textmining.tp3.ner.NerOnDoc;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * El ebfoque de esta prueba es diferente, en vez de seguir probando parsear
 * vimos en prueba 2 que la metadata es muy util, 
 * tambien vimos que para alimentar el NER , es mejor en strings concisos, 
 * tipo oraciones.
 * Otra cosa que vimos en tutorial NER es que podemos pasar diferentes tipos de NER, 
 * para reeconocer diferentes tipos de entidades
 * 
 * Aca lo que se va a hacer es probar varios archivos, 
 * agarrar la metadata de esos archivos, 
 * pasar cada item de metadata por diferentes NER, 
 * para reconocer diferentes personas.
 * 
 * guardar los resultados en alguna clase, sabiendo que cada html es un evento.
 * las NER que se identificaron en la metadata de cada evento.
 * 
 * 
 * @author julio
 *
 */
public class ProbandoNER5 {
	Logger log = LoggerFactory.getLogger(ProbandoNER5.class);
	static{
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yy-MM-dd HH:mm:ss.SSS");
	}
	/**
	 * probando estructura de oraciones
	 */
	@Test
	public void probandoConParser() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String filesUrl = dBoxUrl + "NER/archivosPrueba";

		try {
			/*
			cambiamos el dame las fuentes a otra forma
			para que devuelva un mapa 
			key: fuente
			object : List<String> siendo las oraciones a analizar de esa fuente.
			*/
			Map<String, List<String>> docs = getMyDocsParsed(filesUrl);
			docs.entrySet().stream().forEach(entry -> showEntry(entry));
			
			Map<String, List<String>> docsAsValue = getDocAsValue(filesUrl);
			docsAsValue.entrySet().stream().forEach(entry -> showEntry(entry));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}

	}

	
	
	/** probando oraciones con diferentes ner y guardando en objeto
	 * 
	 */
	@Test
	public void probandoNERConParser() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String modelsUrl = dBoxUrl + "NER/models/";
		String filesUrl = dBoxUrl + "NER/archivosPrueba";

		try {
			log.info("start doc parsing");
			Map<String, List<String>> docs = getMyDocsParsed(filesUrl);
			Map<String, List<String>> docsAsValue = getDocAsValue(filesUrl);
			
			log.info("start getting models");
			Map<String, String> models = getModelsFromFolder(modelsUrl);
			log.info("start appling models to docs");
			Map<String, NerOnDoc> resultNER = applyModelsToDocs(docs,models,"docAllSentences");
			applyModelsToDocs(docsAsValue,models,"docAsValue",resultNER);
			log.info("show Results appling models to docs");
			showAllResults(resultNER);
			log.info("process end");

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void showAllResults(Map<String, NerOnDoc> resultNER) {
		Gson gson = new Gson();
		String jsonResult = gson.toJson(resultNER);
		
		log.info(jsonResult);
			
	}

	/**
	 * aplica los modelos a los documentos
	 * @param docs
	 * @param models
	 * @return
	 */
	private Map<String,NerOnDoc> applyModelsToDocs(Map<String, List<String>> docs, Map<String, String> models,String docProcessId) {
		Map<String,NerOnDoc> ret = new HashMap<>();
		applyModelsToDocs(docs, models, docProcessId, ret);	
		return ret;	
	}



	private void applyModelsToDocs(Map<String, List<String>> docs, Map<String, String> models, String docProcessId,
			Map<String, NerOnDoc> ret) {
		try {
			// para cada doc
			for (Entry<String, List<String>> docEntry : docs.entrySet()) {
				log.info("start processing: "+ docEntry.getKey() );

				NerOnDoc doc = ret.get(docEntry.getKey());
				if (doc==null) {
					doc = new NerOnDoc();
				}
				
				doc.getDocParsedSources().put(docProcessId, docEntry.getValue());
				
				doc.setDocName(docEntry.getKey());
				// aplico cada modelo
				applyModelsToDoc(models, docEntry, doc,docProcessId);
				// agrego el resultado de aplicar todo al documento en el resultado del proceso
				ret.put(docEntry.getKey(),doc);
			}

		} catch (Exception e) {
			log.error("--error: applyModelsToDocs", e);

			//Assert.fail();
		}
	}



	private void applyModelsToDoc(Map<String, String> models, Entry<String, List<String>> docEntry, NerOnDoc doc,String docProcessId)
			throws IOException, InvalidFormatException {
		for (Entry<String, String> modelEntry : models.entrySet()) {
			log.info("start model: "+ modelEntry.getKey() );
			ModelAppliedOutput out = new ModelAppliedOutput();
			out.setModelName(modelEntry.getKey());
			out.setModelFullPath(modelEntry.getValue());
			
			// declarando el modelo
			TokenNameFinderModel model = new TokenNameFinderModel(new File(modelEntry.getValue()));
			NameFinderME finder = new NameFinderME(model);
			Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
			// aplicando el modelo y guardandolo en output
			int count= 0;
			for (String value : docEntry.getValue()) {
				String[] tokens = tokenizer.tokenize(value);
				Span[] nameSpans = finder.find(tokens);
				// aca estaba tomando [] del array como string , entonces me daba cosas no vacias
				String[] tmpArray = Span.spansToStrings(nameSpans, tokens);
				String tmp = Arrays.toString(tmpArray);
				tmp = tmp.replace("[", "").replace("]", "");
				out.getEntities().add(tmp);
				count = count+tmpArray.length;
//				if (tmp.trim().length()>0) {
//					count++;
//				}
			}
			out.setEntitiesRecognized(count);
//					agrego el resultado de aplicar el modelo al output final del documento
			
			if (doc.getDocModelOutputs().get(docProcessId)==null) {
				doc.getDocModelOutputs().put(docProcessId,new ArrayList<>());
			}
			
			doc.getDocModelOutputs().get(docProcessId).add(out);
			
			log.info("end model: "+ modelEntry.getKey() + " - entity count: " + count);
		}
	}



	private Map<String, String> getModelsFromFolder(String modelsUrl) {
		Map<String,String> ret = new HashMap<>();
		try {
			// lee todos los archivos de la URL
			File startFileUrl = new File(modelsUrl);
			File[] files = startFileUrl.listFiles();
			for (File file : files) {
				// si no es directorio se analiza
				if (!file.isDirectory()) {
					if (file.getName().endsWith(".bin")) {
						ret.put(file.getName(), file.getAbsolutePath());
					}else{
						log.warn("no es archivo modelo:" + file.getAbsolutePath());				
					}
				}else{
					log.warn("no es archivo:" + file.getAbsolutePath());				
				}

			}
		} catch (Exception e) {
			log.error("--error: " + modelsUrl, e);
		}

		return ret;
	}



	/**
	cambiamos el dame las fuentes a otra forma
	para que devuelva un mapa 
	key: fuente
	object : List<String> siendo las oraciones a analizar de esa fuente.
	*/
	private Map<String,List<String>> getMyDocsParsed(String aPath) {
		Map<String,List<String>> ret = new HashMap<>();
		try {
			// lee todos los archivos de la URL
			File startFileUrl = new File(aPath);
			File[] files = startFileUrl.listFiles();
			for (File file : files) {

				// si no es directorio se analiza
				if (!file.isDirectory()) {
					List<String> currentFileSentnces = new ArrayList<>();					
					BodyContentHandler handler = new BodyContentHandler();
					Metadata metadata = new Metadata();
					FileInputStream inputstream = new FileInputStream(file);
					ParseContext pcontext = new ParseContext();

					// Html parser
					HtmlParser htmlparser = new HtmlParser();
					htmlparser.parse(inputstream, handler, metadata, pcontext);

					// solo me quedo con la metadata
					String[] metadataNames = metadata.names();

					// me guardo cada item de metadata como oracion
					for (String name : metadataNames) {
						currentFileSentnces.add(metadata.get(name));
					}
					// cuando termino el archivo , guardo en el mapa
					ret.put(file.getAbsolutePath(), currentFileSentnces);
				}else{
					log.warn("no es archivo:" + file.getAbsolutePath());				
				}

			}
		} catch (Exception e) {
			log.error("--error: " + aPath, e);
		}

		return ret;
	}
	
	private Map<String,List<String>> getDocAsValue(String aPath) {
		Map<String,List<String>> ret = new HashMap<>();
		try {
			// lee todos los archivos de la URL
			File startFileUrl = new File(aPath);
			File[] files = startFileUrl.listFiles();
			for (File file : files) {

				// si no es directorio se analiza
				if (!file.isDirectory()) {
					List<String> currentFileSentnces = new ArrayList<>();					
					BodyContentHandler handler = new BodyContentHandler();
					Metadata metadata = new Metadata();
					FileInputStream inputstream = new FileInputStream(file);
					ParseContext pcontext = new ParseContext();

					// Html parser
					HtmlParser htmlparser = new HtmlParser();
					htmlparser.parse(inputstream, handler, metadata, pcontext);

					// solo me quedo con la metadata
					String[] metadataNames = metadata.names();

					// para levantar los metadatos como una parte del doc
					StringBuilder build =  new StringBuilder();
					for (String name : metadataNames) {
						build.append(metadata.get(name)).append("\n");
					}
					// doy los metadatos y el documento entero como valor
					currentFileSentnces.add(handler.toString());
					currentFileSentnces.add(build.toString());
					
					// cuando termino el archivo , guardo en el mapa
					ret.put(file.getAbsolutePath(), currentFileSentnces);
				}else{
					log.warn("no es archivo:" + file.getAbsolutePath());				
				}

			}
		} catch (Exception e) {
			log.error("--error: " + aPath, e);
		}

		return ret;
	}


	private void showEntry(Entry<String, List<String>> entry) {
		final String tab = "  ";
		log.info(entry.getKey());
		entry.getValue().stream().forEach(val -> log.info(tab+val));
	}
	
}
