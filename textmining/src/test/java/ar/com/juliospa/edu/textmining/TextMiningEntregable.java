package ar.com.juliospa.edu.textmining;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Assert;
import org.junit.Test;

import ar.com.juliospa.edu.textmining.domain.tp1.ExpectedResult;
import ar.com.juliospa.edu.textmining.domain.tp1.MeasureContainerWrapper;
import ar.com.juliospa.edu.textmining.domain.tp1.Measures;
import ar.com.juliospa.edu.textmining.domain.tp1.MeasuresContainer;
import ar.com.juliospa.edu.textmining.domain.tp1.QueryString;
import ar.com.juliospa.edu.textmining.domain.tp1.QueryStringCollection;
import ar.com.juliospa.edu.textmining.utils.SolRUtils;
import ar.com.juliospa.edu.textmining.utils.TextMiningUtils;
import ar.com.juliospa.edu.textmining.utils.Trec87QueryNormalizer;
import ar.com.juliospa.edu.textmining.utils.Trec87ResultParser;

/**
 * para tener todo el codigo de las cosas que irian al entregable aca se
 * realizan todas las ejecuciones dado que todo ya esta cargado
 * 
 * @author julio
 *
 */
public class TextMiningEntregable {

	private String pathOutMeasures = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/entregable/";
	private String outR1 = "jaspa.final.normal.measures.xml";
	private String outR2 = "jaspa.final.edismaxconfig.measures.xml";
	private String outR3 = "jaspa.final.stemmed.measures.xml";
	private String outR4 = "jaspa.final.stopwords.measures.xml";
	private String outR5 = "jaspa.final.filterField.measures.xml";
	private String outAggregated = "jaspa.final.aggregated.xml";
	private String outAggregatedAsTxt = "jaspa.final.aggregated.txt";

	/**
	 * ejecucion 1: index normal query normal
	 */
	public void run1IdxNormalQueryNormal() {
		String pathQueries = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/arreglados/";
		String fileQueries = "query.ohsu.1-63.norm.v2.xml";
		String pathExpected = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileExpected = "qrels.ohsu.batch.87";

		String indexName = "tp1";

		String runId = "[1] " + indexName;
		String indexComment = "default";
		String queryComment = "title+desc ; no filter ";
		MeasuresContainer container = new MeasuresContainer();
		container.setRunId(runId);
		container.setIndexComents(indexComment);
		container.setQueryComments(queryComment);

		try {
			QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries, fileQueries);
			List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected + fileExpected);

			for (QueryString query : queries.getTops()) {

				String testQuery = query.getTitle() + " " + query.getDescription();
				SolrClient client = SolRUtils.getClientInstance(indexName);
				SolrQuery solRquery = new SolrQuery();
				solRquery.setRows(SolRUtils.MAX_ROWS);
				// ver http://www.solrtutorial.com/solrj-tutorial.html
				solRquery.setQuery(testQuery);
				QueryResponse response = client.query(solRquery);

				SolrDocumentList results = response.getResults();
				List<ExpectedResult> expectedResultForQuery = TextMiningUtils.getExpectedResultsForQueryNumber(query,
						expectedResult);
				Measures measures = new Measures(query, results, expectedResultForQuery);
				container.getList().add(measures);
			}

			Class<MeasuresContainer> classToMarshal = MeasuresContainer.class;
			TextMiningUtils.objectsToXml(pathOutMeasures, outR1, container, classToMarshal);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * ejecucion 2: index normal query con edismax
	 */
	public void run2IdxNormalQueryEdismax() {
		String pathQueries = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/arreglados/";
		String fileQueries = "query.ohsu.1-63.norm.v2.xml";
		String pathExpected = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileExpected = "qrels.ohsu.batch.87";

		String indexName = "tp1";
		String runId = "[2] " + indexName;
		String indexComment = "default";
		String queryComment = "title+desc ; no filter ; edismax";
		MeasuresContainer container = new MeasuresContainer();
		container.setRunId(runId);
		container.setIndexComents(indexComment);
		container.setQueryComments(queryComment);

		try {
			QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries, fileQueries);
			List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected + fileExpected);

			for (QueryString query : queries.getTops()) {

				String testQuery = query.getTitle() + " " + query.getDescription();
				SolrClient client = SolRUtils.getClientInstance(indexName);
				SolrQuery solRquery = new SolrQuery();
				solRquery.setRows(SolRUtils.MAX_ROWS);

				// ver http://www.solrtutorial.com/solrj-tutorial.html
				solRquery.setQuery(testQuery);
				solRquery.set("defType", "edismax");
				QueryResponse response = client.query(solRquery);

				SolrDocumentList results = response.getResults();
				List<ExpectedResult> expectedResultForQuery = TextMiningUtils.getExpectedResultsForQueryNumber(query,
						expectedResult);
				Measures measures = new Measures(query, results, expectedResultForQuery);
				container.getList().add(measures);
			}

			Class<MeasuresContainer> classToMarshal = MeasuresContainer.class;
			TextMiningUtils.objectsToXml(pathOutMeasures, outR2, container, classToMarshal);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * ejecucion 3: index lower case y stemmed ,query lower case y stemmed
	 */
	public void run3IdxMinStemmedQueryMinStemmed() {

		String pathQueries = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/arreglados/";
		String fileQueries = "query.ohsu.1-63.norm.v2.xml";
		String pathExpected = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileExpected = "qrels.ohsu.batch.87";

		String indexName = "tp1-stemmer";
		String runId = "[3] " + indexName;
		String indexComment = "lower case ; stemmed ; title , abstract , mesh";
		String queryComment = "title+desc ; lower case ; stemmed ; no filter";
		MeasuresContainer container = new MeasuresContainer();
		container.setRunId(runId);
		container.setIndexComents(indexComment);
		container.setQueryComments(queryComment);

		try {
			QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries, fileQueries);
			List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected + fileExpected);

			for (QueryString query : queries.getTops()) {

				QueryResponse response = SolRUtils.executeQueryAgainstSolRWithStemmer(query, indexName);
				SolrDocumentList results = response.getResults();
				List<ExpectedResult> expectedResultForQuery = TextMiningUtils.getExpectedResultsForQueryNumber(query,
						expectedResult);
				Measures measures = new Measures(query, results, expectedResultForQuery);
				container.getList().add(measures);
			}

			Class<MeasuresContainer> classToMarshal = MeasuresContainer.class;
			TextMiningUtils.objectsToXml(pathOutMeasures, outR3, container, classToMarshal);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	/**
	 * ejecucion 4: index lower case y stopwords ,query lower case y stopwords
	 */
	public void run4IdxMinStopWordsQueryMinStopWords() {
		String pathQueries = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/arreglados/";
		String fileQueries = "query.ohsu.1-63.norm.v2.xml";
		String pathExpected = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileExpected = "qrels.ohsu.batch.87";

		String pathSW = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String stopwordsFile = "stopwords.txt";

		String indexName = "tp1-sw";
		String runId = "[4] " + indexName;
		String indexComment = "lower case ; stopwords ; title , abstract , mesh";
		String queryComment = "title+desc ; lower case ; stopwords ; no filter";
		MeasuresContainer container = new MeasuresContainer();
		container.setRunId(runId);
		container.setIndexComents(indexComment);
		container.setQueryComments(queryComment);

		try {
			QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries, fileQueries);
			List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected + fileExpected);
			List<String> stopWords = Files.lines(Paths.get(pathSW + stopwordsFile)).collect(Collectors.toList());

			for (QueryString query : queries.getTops()) {

				String testQuery = query.getTitle() + " " + query.getDescription();
				SolrClient client = SolRUtils.getClientInstance(indexName);
				SolrQuery solRquery = new SolrQuery();
				solRquery.setRows(SolRUtils.MAX_ROWS);
				// ver http://www.solrtutorial.com/solrj-tutorial.html
				solRquery.setQuery(TextMiningUtils.removeStopWords(testQuery, stopWords));
				QueryResponse response = client.query(solRquery);

				SolrDocumentList results = response.getResults();
				List<ExpectedResult> expectedResultForQuery = TextMiningUtils.getExpectedResultsForQueryNumber(query,
						expectedResult);
				Measures measures = new Measures(query, results, expectedResultForQuery);
				container.getList().add(measures);
			}

			Class<MeasuresContainer> classToMarshal = MeasuresContainer.class;
			TextMiningUtils.objectsToXml(pathOutMeasures, outR4, container, classToMarshal);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * ejecucion 5: index normal ,query filtrada por campos
	 */
	public void run5normalQueryFilterFields() {
		String pathQueries = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/arreglados/";
		String fileQueries = "query.ohsu.1-63.norm.v2.xml";
		String pathExpected = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileExpected = "qrels.ohsu.batch.87";

		String indexName = "tp1";

		String runId = "[5] " + indexName;
		String indexComment = "default";
		String queryComment = "title@title , desc@abstract , title+desc@mesh ; title , abstract , mesh";
		MeasuresContainer container = new MeasuresContainer();
		container.setRunId(runId);
		container.setIndexComents(indexComment);
		container.setQueryComments(queryComment);

		try {
			QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries, fileQueries);
			List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected + fileExpected);

			for (QueryString query : queries.getTops()) {

				String testQuery = query.getTitle() + " " + query.getDescription();
				SolrClient client = SolRUtils.getClientInstance(indexName);
				SolrQuery solRquery = new SolrQuery();
				solRquery.setRows(SolRUtils.MAX_ROWS);

				// http://stackoverflow.com/questions/10324969/boosting-fields-in-solr-using-solrj
				solRquery.setQuery(
						"TITLE:" + query.getTitle() + " ABSTRACT:" + query.getDescription() + " MESH:" + testQuery);
				// solRquery.setQuery(testQuery);

				QueryResponse response = client.query(solRquery);

				SolrDocumentList results = response.getResults();
				List<ExpectedResult> expectedResultForQuery = TextMiningUtils.getExpectedResultsForQueryNumber(query,
						expectedResult);
				Measures measures = new Measures(query, results, expectedResultForQuery);
				container.getList().add(measures);
			}

			Class<MeasuresContainer> classToMarshal = MeasuresContainer.class;
			TextMiningUtils.objectsToXml(pathOutMeasures, outR5, container, classToMarshal);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * ejecutar cada configuracion, levantar los archivos generados y agregarlos
	 * en un archivo final.
	 */
	@Test
	public void getAllRuns() {

		try {
			run1IdxNormalQueryNormal();
			run2IdxNormalQueryEdismax();
			run3IdxMinStemmedQueryMinStemmed();
			run4IdxMinStopWordsQueryMinStopWords();
			run5normalQueryFilterFields();

			MeasuresContainer mR1 = leerResultadosXMLaObjeto(outR1);
			MeasuresContainer mR2 = leerResultadosXMLaObjeto(outR2);
			MeasuresContainer mR3 = leerResultadosXMLaObjeto(outR3);
			MeasuresContainer mR4 = leerResultadosXMLaObjeto(outR4);
			MeasuresContainer mR5 = leerResultadosXMLaObjeto(outR5);

			MeasureContainerWrapper mcw = new MeasureContainerWrapper();
			mcw.getList().add(mR1);
			mcw.getList().add(mR2);
			mcw.getList().add(mR3);
			mcw.getList().add(mR4);
			mcw.getList().add(mR5);

			Class<MeasureContainerWrapper> classToMarshal = MeasureContainerWrapper.class;
			TextMiningUtils.objectsToXml(pathOutMeasures, outAggregated, mcw, classToMarshal);
			TextMiningUtils.xmlToJson(pathOutMeasures + outAggregated);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}

	}

	/**
	 * levanta el resultados agregado y lo transforma un txt para poer evaluar
	 * ams facil.
	 */
	@Test
	public void resultsToTxt() {

		try {

			File file = new File(pathOutMeasures + outAggregated);
			JAXBContext jaxbContext = JAXBContext.newInstance(MeasureContainerWrapper.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			MeasureContainerWrapper measuresAll = (MeasureContainerWrapper) jaxbUnmarshaller.unmarshal(file);

			String pathFileName = pathOutMeasures +outAggregatedAsTxt;
			byte[] bytesToWrite = measuresAll.measuresAllToString().getBytes();
			
			TextMiningUtils.writeBytesToFile(pathFileName, bytesToWrite);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	/**
	 * leer resultados del xml y levantarlos como objeto
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private MeasuresContainer leerResultadosXMLaObjeto(String file) throws Exception {
		File fileR1 = new File(pathOutMeasures + file);
		JAXBContext jaxbContext = JAXBContext.newInstance(MeasuresContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		MeasuresContainer mR1 = (MeasuresContainer) jaxbUnmarshaller.unmarshal(fileR1);
		return mR1;
	}

}
