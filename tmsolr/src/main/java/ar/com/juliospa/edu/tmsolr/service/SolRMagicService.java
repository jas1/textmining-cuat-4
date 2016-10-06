package ar.com.juliospa.edu.tmsolr.service;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

public class SolRMagicService {
	// http://wiki.apache.org/solr/Solrj#Maven

	
	/**
	 * to get the client in the specified URL
	 * @param urlString
	 * @return
	 */
	//String urlString = "http://localhost:8983/solr/techproducts";
	public SolrClient getSolrClient(String urlString) {
		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		return solr;
	}
	
	public QueryResponse executeQueryForClient(String queryStr, SolrClient client) throws SolrServerException, IOException {
			
			SolrQuery query = new SolrQuery();
			query.setQuery(queryStr);
			// query.setRequestHandler("/spellCheckCompRH");
//			query.set("fl", "category,title,price");
//			query.setFields("category", "title", "price");
//			query.set("q", "category:books");
			QueryResponse response = client.query(query);
			return response;
//			SolrDocumentList list = response.getResults();

	}
}
