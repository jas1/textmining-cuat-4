package ar.com.juliospa.edu.textmining;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;

import ar.com.juliospa.edu.textmining.domain.Doc;
import ar.com.juliospa.edu.textmining.domain.DocCollection;
import ar.com.juliospa.edu.textmining.domain.ExpectedResult;
import ar.com.juliospa.edu.textmining.domain.QueryStringCollection;
import ar.com.juliospa.edu.textmining.utils.SolRUtils;
import ar.com.juliospa.edu.textmining.utils.Trec87ParserUtil;
import ar.com.juliospa.edu.textmining.utils.Trec87QueryNormalizer;
import ar.com.juliospa.edu.textmining.utils.Trec87ResultParser;

public class TextMiningEntregable {

//	prerequisitos
//	levantar documentos
	/** aca la idea es directo del formato trec al indice solr
	 * pasar los datos al solr 
	 * **/
//	@Test  //ya esta ejecutado
	public void oshumedFormatToSolrIndex() {
		try {
			// path donde este el archivo original
			String path= "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/site_dl/";
			String fileDb="ohsu-trec/trec9-train/ohsumed.87";
			//String fileOut="ohsumed.87.output.xml";
			
			// parser
			DocCollection parsed = Trec87ParserUtil.parseDocCollectionFromFilePath(path+fileDb);
			// conexion con el solR ( tiene que estar levantado ) 
			SolrClient client = SolRUtils.getClientInstance("tp1");

			// para cada doc , vamos a crear un solr 
			for (Doc doc : parsed.getDocuments()) {
				SolrInputDocument document = new SolrInputDocument();
				// recorro todos los fields de la clase y los agrego al documento solr
				// antes de hacer esto hay que dar de alta los campos
				// agregar los campos que uso para indexar a mano en el solR admin
				// solar admin > seleccionar la coleccion > schema > add field
				document.addField(Doc.getFieldNameForXMl("Author",String.class,Doc.class), doc.getAuthor());
				document.addField(Doc.getFieldNameForXMl("DocAbstract",String.class,Doc.class), doc.getDocAbstract());
				document.addField(Doc.getFieldNameForXMl("Docno",Integer.class,Doc.class), doc.getDocno());
				document.addField(Doc.getFieldNameForXMl("Id",Integer.class,Doc.class), doc.getId());
				document.addField(Doc.getFieldNameForXMl("Mesh",String.class,Doc.class), doc.getMesh());
				document.addField(Doc.getFieldNameForXMl("PublicationType",String.class,Doc.class), doc.getPublicationType());
				document.addField(Doc.getFieldNameForXMl("Source",String.class,Doc.class), doc.getSource());
				document.addField(Doc.getFieldNameForXMl("Title",String.class,Doc.class), doc.getTitle());
				
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
	
//	levantar levantar queries
private QueryStringCollection getParseQueries() throws JAXBException {
	String pathQueries= "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
	String fileQueries="query.ohsu.1-63.norm.v2.xml";
	QueryStringCollection queries = Trec87QueryNormalizer.parseQueries(pathQueries,fileQueries);
	return queries;
}
//	levantar testing
private List<ExpectedResult> getExpectedResult()  throws Exception {
	String pathExpected= "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp1/";
	String fileExpected="qrels.ohsu.batch.87";
	List<ExpectedResult> expectedResult = Trec87ResultParser.parseExpectedResults(pathExpected+fileExpected);
	return expectedResult;
}
	
//	a) Calcular Precision, Recall, Precisin-R del sistema en el corpus

//	b) En este ejercicio se espera que explore las distintas posibilidades del
//	sistema que usa y que verifique como afectan la precision-R del sistema.

//	• c) Verificar cual es la “scoring function” y si el sistema permite usar fun-
//	ciones alternativas, usarlas. Crear tambi ́en una funci ́on nueva que no se
//	encuentre entre las alternativas de acuerdo a las que se encuentran en el
//	manual. (parametros a modificar son tf, idf,normalizaci ́on de longitud).


//	• d) Verificar el analizador de texto (parser) y si es posible mejorarlo. Veri-
//	ficar estrategias para Mayusculas/minusculas (case folding), stemming. 
//	Si no esta disponible agregar el Porter stemmer. 
//	Si tiene lista de stop-words verificar cual es la lista. 
//	Ver si encuentra otra lista de stop-words mas adecuada.
//	Importante: el analizador de texto debe funcionar igual para la query y los documentos.
	
	
	
}
