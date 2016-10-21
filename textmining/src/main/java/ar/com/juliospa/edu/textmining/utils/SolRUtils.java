package ar.com.juliospa.edu.textmining.utils;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import ar.com.juliospa.edu.textmining.domain.QueryString;
import ar.com.juliospa.edu.textmining.domain.QueryStringCollection;

/**
 * utilidades de solR
 * @author julio
 *
 */
public class SolRUtils {
	/**
	 * segun ver maximo de resultados esperados = 23.
	 * esto es para cuantos docs fetchea de 1.
	 */
	public static final int MAX_ROWS = 25;
	/**
	 * para conectarse al indice de solR
	 * el indice debe haber sido creado.
	 * @param indexName el nombre del indice
	 * @return un cliente para pdoer realizar operaciones
	 */
	public static SolrClient getClientInstance(String indexName) {
		String urlString = "http://localhost:8983/solr/"+indexName;
		SolrClient client = new HttpSolrClient.Builder(urlString).build();
		return client;
	}

	/**
	 * ejecuta la query en solR sin tunearle nada, mezclando title + description
	 * @param queries
	 * @param queryNumber
	 * @param indexName
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static QueryResponse executeQueryAgainstSolR(QueryStringCollection queries, int queryNumber, String indexName)
			throws SolrServerException, IOException {
		
		return executeQueryAgainstSolR(queries.getTops().get(queryNumber), indexName);
	}
	/**
	 * ejecuta la query en solR sin tunearle nada, mezclando title + description
	 * @param query
	 * @param indexName
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static QueryResponse executeQueryAgainstSolR(QueryString query, String indexName) throws SolrServerException, IOException {
		String testQuery = query.getTitle() +" "+ query.getDescription();
		SolrClient client = getClientInstance(indexName);
		SolrQuery solRquery = new SolrQuery();
		// segun ver max de resultados esperados
		solRquery.setRows(MAX_ROWS);
		solRquery.setQuery(testQuery);
		QueryResponse response = client.query(solRquery);
		
		return response;
	}
	/**
	 * ejecuta la query en solR, mezclando title + description , y luego hace un stemming en el title+desc
	 * @param query
	 * @param indexName
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */	
	public static QueryResponse executeQueryAgainstSolRWithStemmer(QueryString query, String indexName) throws SolrServerException, IOException {
		String testQuery = query.getTitle() +" "+ query.getDescription();
//		String stemedQUery = org.apache.lucene.analysis.en.PorterStemmer.stem(testQuery);
		SolrClient client = getClientInstance(indexName);
		SolrQuery solRquery = new SolrQuery();
		solRquery.setRows(MAX_ROWS);
		solRquery.setQuery(TextMiningUtils.stemSentence(testQuery));
		QueryResponse response = client.query(solRquery);
		
		return response;
	}
	

}
