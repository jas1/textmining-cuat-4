package ar.com.juliospa.edu.textmining;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;

import ar.com.juliospa.edu.textmining.domain.tp1.Doc;
import ar.com.juliospa.edu.textmining.domain.tp1.DocCollection;
import ar.com.juliospa.edu.textmining.domain.tp1.ExpectedResult;
import ar.com.juliospa.edu.textmining.domain.tp1.Measures;
import ar.com.juliospa.edu.textmining.domain.tp1.MeasuresContainer;
import ar.com.juliospa.edu.textmining.domain.tp1.QueryString;
import ar.com.juliospa.edu.textmining.domain.tp1.QueryStringCollection;
import ar.com.juliospa.edu.textmining.utils.SolRUtils;
import ar.com.juliospa.edu.textmining.utils.TextMiningUtils;
import ar.com.juliospa.edu.textmining.utils.Trec87ParserUtil;
import ar.com.juliospa.edu.textmining.utils.Trec87QueryNormalizer;
import ar.com.juliospa.edu.textmining.utils.Trec87ResultParser;

/**
 * utilidades vairas para ir probando todo lo que se fue relaizando
 * @author julio
 *
 */
public class TextMiningUtilsTest {
	
	/**
	 * buscando en un dataset de leyes
	 */
	@Test
	public void scanForFilesTestLauNormativa() {
		String carpeta = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/dataset/raw/lau-normativa/20150430_BASE_NORMATIVA";
		List<Path> result = TextMiningUtils.scanForFiles(carpeta);
		System.out.println(result.size());
		result.forEach(pa -> System.out.println(pa.toFile().getAbsolutePath()));
	}

	/**
	 * para probar tirar consultas
	 */
	@Test
	public void querySolrInstance() {
		SolrClient client = SolRUtils.getClientInstance("lau_normativa");

		String queryStr = "mendoza";

		SolrQuery query = new SolrQuery();
		query.setQuery(queryStr);
		// query.addFilterQuery("cat:electronics", "store:amazon.com");
		// query.setFields("id", "price", "merchant", "cat", "store");
		// query.setStart(0);
		// query.set("defType", "edismax");

		try {
			QueryResponse response = client.query(query);
			SolrDocumentList results = response.getResults();
			for (int i = 0; i < results.size(); ++i) {
				System.out.println(results.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * para cargar docs
	 */
	@Test
	public void solrLoadDocs() {
		SolrClient client = SolRUtils.getClientInstance("gettingstarted");

		try {
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", "552199");
			document.addField("name", "Gouda cheese wheel");
			document.addField("price", "49.99");
			UpdateResponse response = client.add(document);
			// Remember to commit your changes!
			client.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * leer el xml de ohsumed
	 */
	public void oshumedXMLReader() {
		try {
			String path = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
			String fileDb = "ohsumed.87";

			try {

				File file = new File(path + fileDb);
				JAXBContext jaxbContext = JAXBContext.newInstance(DocCollection.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				DocCollection docCol = (DocCollection) jaxbUnmarshaller.unmarshal(file);
				System.out.println(docCol);

			} catch (JAXBException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * la idea de esto es agarrar el formato del dataset de la pagina y migrarlo
	 * a XML
	 * 
	 * @throws Exception
	 */
	@Test
	public void oshumedFormatToXML() {
		try {
			String path = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/site_dl/";
			String fileDb = "ohsu-trec/trec9-train/ohsumed.87";
			String fileOut = "ohsumed.87.output.xml";

			DocCollection parsed = Trec87ParserUtil.parseDocCollectionFromFilePath(path + fileDb);

			File file = new File(path + fileOut);
			JAXBContext jaxbContext = JAXBContext.newInstance(DocCollection.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(parsed, file);
			// jaxbMarshaller.marshal(parsed, System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertTrue(true);

	}

	/**
	 * borrar todos los docuemtnos de la coleccion
	 */
	@Test
	public void deleteDocumentsFromCollection() {

		SolrClient client = SolRUtils.getClientInstance("tp1");
		try {
			// esta comentado porque borra todo
			// client.deleteByQuery("*:*");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertTrue(true);
	}

	/**
	 * aca la idea es directo del formato trec al indice solr pasar los datos al
	 * solr
	 **/
	@Test
	public void oshumedFormatToSolrIndex() {
		try {
			// path donde este el archivo original
			String path = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/site_dl/";
			String fileDb = "ohsu-trec/trec9-train/ohsumed.87";
			// String fileOut="ohsumed.87.output.xml";

			// parser
			DocCollection parsed = Trec87ParserUtil.parseDocCollectionFromFilePath(path + fileDb);
			// conexion con el solR ( tiene que estar levantado )
			SolrClient client = SolRUtils.getClientInstance("tp1");

			// para cada doc , vamos a crear un solr
			for (Doc doc : parsed.getDocuments()) {
				SolrInputDocument document = new SolrInputDocument();
				// recorro todos los fields de la clase y los agrego al
				// documento solr
				// antes de hacer esto hay que dar de alta los campos
				// agregar los campos que uso para indexar a mano en el solR
				// admin
				// solar admin > seleccionar la coleccion > schema > add field
				document.addField(Doc.getFieldNameForXMl("Author", String.class, Doc.class), doc.getAuthor());
				document.addField(Doc.getFieldNameForXMl("DocAbstract", String.class, Doc.class), doc.getDocAbstract());
				document.addField(Doc.getFieldNameForXMl("Docno", Integer.class, Doc.class), doc.getDocno());
				document.addField(Doc.getFieldNameForXMl("Id", Integer.class, Doc.class), doc.getId());
				document.addField(Doc.getFieldNameForXMl("Mesh", String.class, Doc.class), doc.getMesh());
				document.addField(Doc.getFieldNameForXMl("PublicationType", String.class, Doc.class),
						doc.getPublicationType());
				document.addField(Doc.getFieldNameForXMl("Source", String.class, Doc.class), doc.getSource());
				document.addField(Doc.getFieldNameForXMl("Title", String.class, Doc.class), doc.getTitle());

				// lo agrego al response
				UpdateResponse response = client.add(document);
				// System.out.println(response.getStatus());
				// comiteo al server
				client.commit();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertTrue(true);

	}

	/**
	 * 
	 * load with stemmer
	 */
	@Test
	public void oshumedFormatToSolrIndexStemmer() {
		try {
			// path donde este el archivo original
			String path = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/site_dl/";
			String fileDb = "ohsu-trec/trec9-train/ohsumed.87";
			// String fileOut="ohsumed.87.output.xml";

			// parser
			DocCollection parsed = Trec87ParserUtil.parseDocCollectionFromFilePath(path + fileDb);
			// conexion con el solR ( tiene que estar levantado )
			SolrClient client = SolRUtils.getClientInstance("tp1-stemmer");

			// para cada doc , vamos a crear un solr
			for (Doc doc : parsed.getDocuments()) {
				SolrInputDocument document = new SolrInputDocument();
				// recorro todos los fields de la clase y los agrego al
				// documento solr
				// antes de hacer esto hay que dar de alta los campos
				// agregar los campos que uso para indexar a mano en el solR
				// admin
				// solar admin > seleccionar la coleccion > schema > add field
				document.addField(Doc.getFieldNameForXMl("Author", String.class, Doc.class),doc.getAuthor() == null ? doc.getAuthor() : doc.getAuthor().toLowerCase());
				document.addField(Doc.getFieldNameForXMl("DocAbstract", String.class, Doc.class),doc.getDocAbstract() == null ? doc.getDocAbstract(): TextMiningUtils.stemSentence(doc.getDocAbstract().toLowerCase()));
				document.addField(Doc.getFieldNameForXMl("Docno", Integer.class, Doc.class), doc.getDocno());
				document.addField(Doc.getFieldNameForXMl("Id", Integer.class, Doc.class), doc.getId());
				document.addField(Doc.getFieldNameForXMl("Mesh", String.class, Doc.class), doc.getMesh() == null? doc.getMesh() : TextMiningUtils.stemSentence(doc.getMesh().toLowerCase()));
				document.addField(Doc.getFieldNameForXMl("PublicationType", String.class, Doc.class),doc.getPublicationType() == null ? doc.getPublicationType(): doc.getPublicationType().toLowerCase());
				document.addField(Doc.getFieldNameForXMl("Source", String.class, Doc.class),doc.getSource() == null ? doc.getSource() : doc.getSource().toLowerCase());
				document.addField(Doc.getFieldNameForXMl("Title", String.class, Doc.class), doc.getTitle() == null? doc.getTitle() : TextMiningUtils.stemSentence(doc.getTitle().toLowerCase()));

				// lo agrego al response
				UpdateResponse response = client.add(document);
				System.out.println(response.getStatus());
				// comiteo al server
				client.commit();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				// SolrClient client =
				// SolRUtils.getClientInstance("tp1-stemmer");
				// client.deleteByQuery("*:*");
			} catch (Exception e1) {
				e1.printStackTrace();
				Assert.fail();
			}
			Assert.fail();
		}
		Assert.assertTrue(true);
	}

	/**
	 * levantando el ohsumed a un indice con stopwords
	 */
	@Test
	public void oshumedFormatToSolrIndexStopWords() {
		try {
			// path donde este el archivo original
			String path = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/site_dl/";
			String fileDb = "ohsu-trec/trec9-train/ohsumed.87";
			// String fileOut="ohsumed.87.output.xml";

			String pathSW = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
			String stopwordsFile = "stopwords.txt";

			List<String> stopWordList = Files.lines(Paths.get(pathSW + stopwordsFile)).collect(Collectors.toList());

			// parser
			DocCollection parsed = Trec87ParserUtil.parseDocCollectionFromFilePath(path + fileDb);
			// conexion con el solR ( tiene que estar levantado )
			SolrClient client = SolRUtils.getClientInstance("tp1-sw");

			// para cada doc , vamos a crear un solr
			for (Doc doc : parsed.getDocuments()) {
				SolrInputDocument document = new SolrInputDocument();
				// recorro todos los fields de la clase y los agrego al
				// documento solr
				// antes de hacer esto hay que dar de alta los campos
				// agregar los campos que uso para indexar a mano en el solR
				// admin
				// solar admin > seleccionar la coleccion > schema > add field
				String author = doc.getAuthor() == null ? doc.getAuthor() : doc.getAuthor().toLowerCase();
				document.addField(Doc.getFieldNameForXMl("Author", String.class, Doc.class),author);
				String abstractDoc = doc.getDocAbstract() == null ? doc.getDocAbstract(): TextMiningUtils.removeStopWords(doc.getDocAbstract().toLowerCase(),stopWordList );
				document.addField(Doc.getFieldNameForXMl("DocAbstract", String.class, Doc.class),abstractDoc);
				document.addField(Doc.getFieldNameForXMl("Docno", Integer.class, Doc.class), doc.getDocno());
				document.addField(Doc.getFieldNameForXMl("Id", Integer.class, Doc.class), doc.getId());
				String mesh = doc.getMesh() == null? doc.getMesh() : TextMiningUtils.removeStopWords(doc.getMesh().toLowerCase(),stopWordList);
				document.addField(Doc.getFieldNameForXMl("Mesh", String.class, Doc.class), mesh);
				String publi = doc.getPublicationType() == null ? doc.getPublicationType(): doc.getPublicationType().toLowerCase();
				document.addField(Doc.getFieldNameForXMl("PublicationType", String.class, Doc.class),publi);
				Object source = doc.getSource() == null ? doc.getSource() : doc.getSource().toLowerCase();
				document.addField(Doc.getFieldNameForXMl("Source", String.class, Doc.class),source);
				Object title = doc.getTitle() == null? doc.getTitle() : TextMiningUtils.removeStopWords(doc.getTitle().toLowerCase(),stopWordList);
				document.addField(Doc.getFieldNameForXMl("Title", String.class, Doc.class), title);
				// lo agrego al response
				UpdateResponse response = client.add(document);
				// System.out.println(response.getStatus());
				// comiteo al server
				client.commit();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertTrue(true);

	}

	/**
	 * haciendo que las queries SEAN XML estricto
	 */
	@Test
	public void standarizeQueriesToXml() {
		String path = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileQueries = "query.ohsu.1-63.xml";
		String fileQueriesNorm = "query.ohsu.1-63.norm.v2.xml";

		Trec87QueryNormalizer normalizer = new Trec87QueryNormalizer();
		try {
			QueryStringCollection parsed = normalizer.parseQueryColFromFilePath(path + fileQueries);
			File file = new File(path + fileQueriesNorm);
			JAXBContext jaxbContext = JAXBContext.newInstance(QueryStringCollection.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(parsed, file);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	/**
	 * leyendo las queries del un XML estricto que fue realizado anteriormente.
	 */
	@Test
	public void readQueriesFromXmlTuneado() {
		String path = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileDb = "query.ohsu.1-63.norm.v2.xml";

		try {
			QueryStringCollection queryCol = Trec87QueryNormalizer.parseQueries(path, fileDb);
			System.out.println(queryCol.getTops().get(0).getTitle());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * levantar los resultados de las queries y ver cuantos resultoados son los esperables para cada 1.
	 */
	@Test
	public void queriesResultFile() {
		String path = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileResult = "qrels.ohsu.batch.87";
		try {
			List<ExpectedResult> result = Trec87ResultParser.parseExpectedResults(path + fileResult);
			System.out.println(result.size());

			Trec87ResultParser.expectedQuantities(result);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * este es para correr la 1ra query contra solR
	 */
	@Test
	public void runqQueryForTest() {
		String path = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileDb = "query.ohsu.1-63.norm.v2.xml";

		try {
			QueryStringCollection queryCol = Trec87QueryNormalizer.parseQueries(path, fileDb);

			// armo el termino de la query
			String testQuery = queryCol.getTops().get(0).getTitle() + " " + queryCol.getTops().get(0).getDescription();
			System.out.println(testQuery);

			// levanto instancia solR
			SolrClient client = SolRUtils.getClientInstance("tp1");
			// ejecuto query solr

			SolrQuery query = new SolrQuery();
			query.setQuery(testQuery);
			// query.addFilterQuery("cat:electronics", "store:amazon.com");
			// query.setFields("id", "price", "merchant", "cat", "store");
			// query.setStart(0);
			// query.set("defType", "edismax");

			try {
				QueryResponse response = client.query(query);
				SolrDocumentList results = response.getResults();
				System.out.println("se encontraron: " + results.getNumFound());
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * para ejecutar la query analizar el resultado vs los resultados esperados
	 */
	@Test
	public void compareQueryResultVsQueryExpected() {
		String pathQueries = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileQueries = "query.ohsu.1-63.norm.v2.xml";
		String pathExpected = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileExpected = "qrels.ohsu.batch.87";

		String pathOutMeasures = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileOutMeasures = "jaspa.query1.measures.xml";

		int queryNumber = 0;

		try {
			QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries, fileQueries);
			List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected + fileExpected);

			QueryResponse response = SolRUtils.executeQueryAgainstSolR(queries, queryNumber, "tp1");
			SolrDocumentList results = response.getResults();

			List<ExpectedResult> expectedResultForQuery = TextMiningUtils.getExpectedResultsForQueryNumber(queries,
					expectedResult, queryNumber);

			Measures measures = new Measures(queries.getTops().get(queryNumber), results, expectedResultForQuery);
			measures.showMeasures();
			Class<MeasuresContainer> classToMarshal = MeasuresContainer.class;
			MeasuresContainer container = new MeasuresContainer();
			container.getList().add(measures);

			TextMiningUtils.objectsToXml(pathOutMeasures, fileOutMeasures, container, classToMarshal);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * para crear las mediciones para una query especifica
	 */
	@Test
	public void allQueryDatasetEval() {
		String pathQueries = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileQueries = "query.ohsu.1-63.norm.v2.xml";
		String pathExpected = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileExpected = "qrels.ohsu.batch.87";

		String pathOutMeasures = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileOutMeasures = "jaspa.queries.measures.xml";
		String indexName = "tp1";

		try {
			QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries, fileQueries);
			List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected + fileExpected);

			MeasuresContainer container = new MeasuresContainer();

			for (QueryString query : queries.getTops()) {

				QueryResponse response = SolRUtils.executeQueryAgainstSolR(query, indexName);
				SolrDocumentList results = response.getResults();
				List<ExpectedResult> expectedResultForQuery = TextMiningUtils.getExpectedResultsForQueryNumber(query,
						expectedResult);
				Measures measures = new Measures(query, results, expectedResultForQuery);
				container.getList().add(measures);
			}

			Class<MeasuresContainer> classToMarshal = MeasuresContainer.class;
			TextMiningUtils.objectsToXml(pathOutMeasures, fileOutMeasures, container, classToMarshal);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	/** para ver que tal dio el coso con stemming */
	// http://stackoverflow.com/questions/32372647/solr-stemming-words-using-solr
	@Test
	public void stemmer() {
		String pathQueries = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileQueries = "query.ohsu.1-63.norm.v2.xml";
		String pathExpected = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileExpected = "qrels.ohsu.batch.87";

		String pathOutMeasures = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileOutMeasures = "jaspa.query1.measures.xml";

		int queryNumber = 0;

		try {
			QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries, fileQueries);
			List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected + fileExpected);

			QueryResponse response = SolRUtils.executeQueryAgainstSolRWithStemmer(queries.getTops().get(queryNumber),
					"tp1");
			SolrDocumentList results = response.getResults();

			List<ExpectedResult> expectedResultForQuery = TextMiningUtils.getExpectedResultsForQueryNumber(queries,
					expectedResult, queryNumber);

			Measures measures = new Measures(queries.getTops().get(queryNumber), results, expectedResultForQuery);
			measures.showMeasures();

			// TextMiningUtils.objectsToXml(pathOutMeasures, fileOutMeasures,
			// container, classToMarshal);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** para ver si el stemmer realiza stemming
	 * 
	 */
	@Test
	public void testStemmer() {
		String tmp = "60 year old menopausal woman without hormone replacement therapy anticardiolipin and lupus anticoagulants, pathophysiology, epidemiology, complications";

		TextMiningUtils.stemSentence(tmp);
	}

	/**para borrar todo de la coleccion stemeada
	 * 
	 */
	@Test
	public void deleteAllFromStemmerTp1() {
		try {
			SolrClient client = SolRUtils.getClientInstance("tp1-stemmer");
			client.deleteByQuery("*:*");
		} catch (Exception e) {
			Assert.fail();
		}
	}
	/** para borrar todo de la coleccion stopwords
	 * 
	 */
	@Test
	public void deleteAllFromSwTp1() {
		try {
			SolrClient client = SolRUtils.getClientInstance("tp1-sw");
			client.deleteByQuery("*:*");
		} catch (Exception e) {
			Assert.fail();
		}
	}

	/**para ejecutar sobre la coleccion stemeada
	 * 
	 */
	@Test
	public void allQueryDatasetEvalOnStemmed() {
		String pathQueries = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/arreglados/";
		String fileQueries = "query.ohsu.1-63.norm.v2.xml";
		String pathExpected = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileExpected = "qrels.ohsu.batch.87";

		String pathOutMeasures = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileOutMeasures = "jaspa.queries.stemmed.measures.xml";
		String indexName = "tp1-stemmer";

		try {
			QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries, fileQueries);
			List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected + fileExpected);

			MeasuresContainer container = new MeasuresContainer();

			for (QueryString query : queries.getTops()) {

				QueryResponse response = SolRUtils.executeQueryAgainstSolRWithStemmer(query, indexName);
				SolrDocumentList results = response.getResults();
				List<ExpectedResult> expectedResultForQuery = TextMiningUtils.getExpectedResultsForQueryNumber(query,
						expectedResult);
				Measures measures = new Measures(query, results, expectedResultForQuery);
				container.getList().add(measures);
			}

			Class<MeasuresContainer> classToMarshal = MeasuresContainer.class;
			TextMiningUtils.objectsToXml(pathOutMeasures, fileOutMeasures, container, classToMarshal);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	/** para probar si funciona el sacador de stopwords */
	@Test
	public void testStopWordMethod() {
		String pathSW = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String stopwordsFile = "stopwords.txt";
		String tmp = "60 year old menopausal woman without hormone replacement therapy anticardiolipin and lupus anticoagulants, pathophysiology, epidemiology, complications";
		try {
			Stream<String> stream = Files.lines(Paths.get(pathSW + stopwordsFile));

			System.out.println(tmp);
			System.out.println(TextMiningUtils.removeStopWords(tmp, stream.collect(Collectors.toList())));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Test
	public void testToJson(){
		String pathOutMeasures="/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/entregable/";
		String outAggregated = "jaspa.final.aggregated.xml";
		try {
			System.out.println(TextMiningUtils.xmlToJson(pathOutMeasures+outAggregated));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
