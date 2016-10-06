package ar.com.juliospa.edu.textmining;

import java.nio.file.Path;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

public class TextMiningUtilsTest {

	@Test
	public void scanForFilesTestLauNormativa() {
		String carpeta = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/dataset/raw/lau-normativa/20150430_BASE_NORMATIVA";
		List<Path> result = TextMiningUtils.scanForFiles(carpeta);
		System.out.println(result.size());
		result.forEach(pa -> System.out.println(pa.toFile().getAbsolutePath()));
	}

	@Test
	public void querySolrInstance() {
		String urlString = "http://localhost:8983/solr/techproducts";
		SolrClient client = new HttpSolrClient.Builder(urlString).build();

		String queryStr ="mendoza";

		SolrQuery query = new SolrQuery();
		query.setQuery(queryStr);
		//query.addFilterQuery("cat:electronics", "store:amazon.com");
		//query.setFields("id", "price", "merchant", "cat", "store");
		//query.setStart(0);
		//query.set("defType", "edismax");

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

}
