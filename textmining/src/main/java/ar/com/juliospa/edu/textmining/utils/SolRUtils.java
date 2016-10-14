package ar.com.juliospa.edu.textmining.utils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import ar.com.juliospa.edu.textmining.domain.ExpectedResult;
import ar.com.juliospa.edu.textmining.domain.QueryString;
import ar.com.juliospa.edu.textmining.domain.QueryStringCollection;

public class SolRUtils {

	public static SolrClient getClientInstance(String indexName) {
		String urlString = "http://localhost:8983/solr/"+indexName;
		SolrClient client = new HttpSolrClient.Builder(urlString).build();
		return client;
	}

	public static List<ExpectedResult> getExpectedResultsForQueryNumber(QueryStringCollection queries,
			List<ExpectedResult> expectedResult, int queryNumber) {
		List<ExpectedResult> expectedResultForQuery = expectedResult.stream()
			.filter(er -> er.getQueryId().equals(queries.getTops().get(queryNumber).getNumber()))
			.collect(Collectors.toList());
		return expectedResultForQuery;
	}

	public static List<ExpectedResult> getExpectedResultsForQueryNumber(QueryString query,
			List<ExpectedResult> expectedResult) {
		List<ExpectedResult> expectedResultForQuery = expectedResult.stream()
			.filter(er -> er.getQueryId().equals(query.getNumber()))
			.collect(Collectors.toList());
		return expectedResultForQuery;
	}
	
	public static QueryResponse executeQueryAgainstSolR(QueryStringCollection queries, int queryNumber)
			throws SolrServerException, IOException {
		
		return executeQueryAgainstSolR(queries.getTops().get(queryNumber));
	}

	public static QueryResponse executeQueryAgainstSolR(QueryString query) throws SolrServerException, IOException {
		String testQuery = query.getTitle() +" "+ query.getDescription();
		SolrClient client = getClientInstance("tp1");
		SolrQuery solRquery = new SolrQuery();
		solRquery.setQuery(testQuery);
		QueryResponse response = client.query(solRquery);
		
		return response;
	}

}
