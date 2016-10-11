package ar.com.juliospa.edu.textmining;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;

import ar.com.juliospa.edu.textmining.domain.Doc;
import ar.com.juliospa.edu.textmining.domain.DocCollection;
import ar.com.juliospa.edu.textmining.domain.QueryStringCollection;
import ar.com.juliospa.edu.textmining.utils.TextMiningUtils;
import ar.com.juliospa.edu.textmining.utils.Trec87ParserUtil;
import ar.com.juliospa.edu.textmining.utils.Trec87QueryNormalizer;

public class TextMiningUtilsTest {

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
		SolrClient client = getClientInstance("lau_normativa");

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

	private SolrClient getClientInstance(String indexName) {
		String urlString = "http://localhost:8983/solr/"+indexName;
		SolrClient client = new HttpSolrClient.Builder(urlString).build();
		return client;
	}
	
	/**
	 * para cargar docs
	 */
	@Test
	public void solrLoadDocs() {
		SolrClient client = getClientInstance("gettingstarted");

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
	
	public void oshumedXMLReader() {
		try {
			String path= "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
			String fileDb="ohsumed.87";

			 try {

				File file = new File(path+fileDb);
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
	 * la idea de esto es agarrar el formato del dataset de la pagina y migrarlo a XML
	 * @throws Exception 
	 */
	@Test
	public void oshumedFormatToXML() {
		try {
			String path= "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/site_dl/";
			String fileDb="ohsu-trec/trec9-train/ohsumed.87";
			String fileOut="ohsumed.87.output.xml";
			
			Trec87ParserUtil parser = new Trec87ParserUtil();
			DocCollection parsed = parser.parseDocCollectionFromFilePath(path+fileDb);
			
			File file = new File(path+fileOut);
			JAXBContext jaxbContext = JAXBContext.newInstance(DocCollection.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(parsed, file);
			//jaxbMarshaller.marshal(parsed, System.out);
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
		
		SolrClient client = getClientInstance("tp1");
		try {
			// esta comentado porque borra todo 
			//client.deleteByQuery("*:*");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertTrue(true);
	}
	
	/** aca la idea es directo del formato trec al indice solr
	 * pasar los datos al solr 
	 * **/
	@Test
	public void oshumedFormatToSolrIndex() {
		try {
			// path donde este el archivo original
			String path= "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/site_dl/";
			String fileDb="ohsu-trec/trec9-train/ohsumed.87";
			//String fileOut="ohsumed.87.output.xml";
			
			// parser
			Trec87ParserUtil parser = new Trec87ParserUtil();
			DocCollection parsed = parser.parseDocCollectionFromFilePath(path+fileDb);
			// conexion con el solR ( tiene que estar levantado ) 
			SolrClient client = getClientInstance("tp1");

			// para cada doc , vamos a crear un solr 
			for (Doc doc : parsed.getDocuments()) {
				SolrInputDocument document = new SolrInputDocument();
				// recorro todos los fields de la clase y los agrego al documento solr
				// antes de hacer esto hay que dar de alta los campos
				// agregar los campos que uso para indexar a mano en el solR admin
				// solar admin > seleccionar la coleccion > schema > add field
				document.addField(getFieldNameForXMl("Author",String.class,Doc.class), doc.getAuthor());
				document.addField(getFieldNameForXMl("DocAbstract",String.class,Doc.class), doc.getDocAbstract());
				document.addField(getFieldNameForXMl("Docno",Integer.class,Doc.class), doc.getDocno());
				document.addField(getFieldNameForXMl("Id",Integer.class,Doc.class), doc.getId());
				document.addField(getFieldNameForXMl("Mesh",String.class,Doc.class), doc.getMesh());
				document.addField(getFieldNameForXMl("PublicationType",String.class,Doc.class), doc.getPublicationType());
				document.addField(getFieldNameForXMl("Source",String.class,Doc.class), doc.getSource());
				document.addField(getFieldNameForXMl("Title",String.class,Doc.class), doc.getTitle());
				
				// lo agrego al response
				UpdateResponse response = client.add(document);
//				System.out.println(response.getStatus());
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
	
	public String getFieldNameForXMl(String domainName,Class<?> tipo,Class<?> clazz) throws NoSuchMethodException, SecurityException {
		String fieldSetterMethod = "set"+domainName;
		Method setterField = clazz.getMethod(fieldSetterMethod, tipo );
		XmlElement xmElem = setterField.getAnnotation(XmlElement.class);
		return xmElem.name();
	}
	
	@Test
	public void standarizeQueriesToXml(){
		String path= "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
		String fileQueries="query.ohsu.1-63.xml";
		String fileQueriesNorm="query.ohsu.1-63.norm.v2.xml";
		
		Trec87QueryNormalizer normalizer = new Trec87QueryNormalizer();
		try {
			QueryStringCollection parsed = normalizer.parseQueryColFromFilePath(path+fileQueries);
			File file = new File(path+fileQueriesNorm);
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
	
	public void runQueriesAgainstSolrAndSaveResults(){
		String path= "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/site_dl/";
		String fileDb="ohsu-trec/trec9-train/ohsumed.87";		
	}

}
