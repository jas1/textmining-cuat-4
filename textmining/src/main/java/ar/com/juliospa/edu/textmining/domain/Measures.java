package ar.com.juliospa.edu.textmining.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class Measures {

	private String queryId;
	private double precision;
	private double recall;
	private double rPrecision;
	private double fMeasure;
	
	private Integer totalRelevantes;
	private Long relevantesObtenidos;
	private Integer totalObtenidos;
	
	/**
	 * mapa de relevantes  y su valor de relevancia o 0 si no encontrado.
	 */
	private Map<Integer, Integer> relevantsReport;
	private List<SolrDocument> topRPrecisionDocs;
	private Map<Integer, Integer> topRRelevantsReport;
	private Long topRRelevantesObtenidos;
	private Integer topRPrecisionCant;
	
	public Measures(QueryString query ,SolrDocumentList results, List<ExpectedResult> expectedResultForQuery) {
		queryId =  query.getNumber();
		relevantsReport = mapForRelevanceBuild(results, expectedResultForQuery);
		// filtro por los que dejaron diferente de 0
		relevantesObtenidos = relevantsReport.entrySet().stream().filter(ent-> ent.getValue()!=0).count();
		totalRelevantes = expectedResultForQuery.size();
		totalObtenidos = results.size();
		//precision
//		P = RELEVANTES OBTENIDOS vs TOTAL OBTENIDOS
		precision = ((double)relevantesObtenidos) / totalObtenidos;
		
		// recall 
//		R = relevantes obtenidos vs TOTAL RELAVANTES PARA LA QUERY
		recall = ((double)relevantesObtenidos) / totalRelevantes;
		
//		precisionR 
/*		An alternative, which alleviates this problem, is R-precision. ...
 * libro pagina: 
 * 
en el libro : An Introduction to Information Retrieval
capitulo: 8.4 Evaluation of ranked retrieval results
en la pagina: 198 del pdf
https://en.wikipedia.org/wiki/Information_retrieval#R-Precision
	es la precision evaluada en cantidad de documentos = total de R existentes para la query, 
	o sea para el caso de query 1 ohsumed , 6 son relevantes, entonces precision top 6
	*/	
		Integer minValue = results.size();
		if (minValue < totalRelevantes) {
			totalRelevantes = minValue;
		}
		topRPrecisionDocs = results.subList(0,totalRelevantes );
		topRRelevantsReport = mapForRelevanceBuild(topRPrecisionDocs, expectedResultForQuery);
		topRRelevantesObtenidos = topRRelevantsReport.entrySet().stream().filter(ent-> ent.getValue()!=0).count();
		topRPrecisionCant = topRPrecisionDocs.size();
		rPrecision = ((double)topRRelevantesObtenidos) / topRPrecisionCant;
		
		/* Fmeasure = 2*precision*recall / (precision +recall) */
		fMeasure = 2*precision*recall / (precision+recall);
	}


	public void showRelevanceMap(Map<Integer, Integer> relMap) {
		System.out.println("relevant id"+" - "+"relevance match ( 0 , no match) ");
		relevantsReport.entrySet().forEach(ent -> System.out.println(ent.getKey()+" - "+ent.getValue()));
	}

	public void showMeasures() {
		System.out.println("total Obtenidos ="+totalObtenidos);
		System.out.println("total relevantes="+totalRelevantes);
		System.out.println("difference(obt-rel)=" + (totalObtenidos - totalRelevantes ));
		System.out.println("relevantes obtenidos="+relevantesObtenidos);

		
		// precision
//		P = RELEVANTES OBTENIDOS vs TOTAL OBTENIDOS
		System.out.println("P = RELEVANTES OBTENIDOS / TOTAL OBTENIDOS");
        System.out.printf("%.9f", precision);
        System.out.println();

		// recall 
//		R = relevantes obtenidos vs TOTAL RELAVANTES PARA LA QUERY 
		System.out.println("R = relevantes obtenidos / TOTAL RELAVANTES PARA LA QUERY ");
        System.out.printf("%.9f", recall);
        System.out.println();

        // Fmeasure = 2*precision*recall / (precision +recall)
		System.out.println("Fmeasure = 2*precision*recall / (precision +recall) ");
        System.out.printf("%.9f", fMeasure);
        System.out.println();
		
		// precisionR 
//		P = RELEVANTES OBTENIDOS vs TOTAL OBTENIDOS
        System.out.println();
		System.out.println("R-Precision = RELEVANTES OBTENIDOS@OBTENIDOS(0..TOTAL RELAVANTES) / OBTENIDOS(0..TOTAL RELAVANTES)");
		System.out.println("TOTAL RELAVANTES)  ="+totalRelevantes);
		System.out.println("OBTENIDOS(0..TOTAL RELAVANTES))  ="+topRPrecisionCant);
		System.out.println("RELEVANTES OBTENIDOS@OBTENIDOS(0..TOTAL RELAVANTES)  ="+topRRelevantesObtenidos);
		System.out.println("precision evaluada en cantidad de documentos = total de R existentes para la query,");
        System.out.printf("%.9f", rPrecision);
        System.out.println();
        System.out.println();
        
        System.out.println("mapa relevantes para Recall");
        showRelevanceMap(relevantsReport);
        
        System.out.println("mapa relevantes para R-precision");
        showRelevanceMap(relevantsReport);
        
	}
	
	private Map<Integer, Integer> mapForRelevanceBuild(List<SolrDocument> docSublist,
			List<ExpectedResult> expectedResultForQuery) {
		Map<Integer,Integer> contieneDocumento = new HashMap<>();
		// recorro expected, y me fijo por cada 1, si encuentro el resultado en los resultados.
		expectedResultForQuery.forEach(exp -> docSublist.forEach(resu -> populateMapContainsDocs(exp,resu,contieneDocumento)));
		return contieneDocumento;
	}
	
	private Map<Integer, Integer> mapForRelevanceBuild(SolrDocumentList results,
			List<ExpectedResult> expectedResultForQuery) {
		Map<Integer,Integer> contieneDocumento = new HashMap<>();
		// recorro expected, y me fijo por cada 1, si encuentro el resultado en los resultados.
		expectedResultForQuery.forEach(exp -> results.forEach(resu -> populateMapContainsDocs(exp,resu,contieneDocumento)));
		return contieneDocumento;
	}
	// funcion del loop : recorro expected, y me fijo por cada 1, si encuentro el resultado en los resultados.
	private void populateMapContainsDocs (ExpectedResult exp ,SolrDocument resu,Map<Integer,Integer> resultMap){
		Integer currentKey = (Integer)resu.get("DOCNO");
		
		Integer currentValue = resultMap.get(currentKey);
		// si venia null lo incializo, sino me quedo con el valor que venia.
		if (currentValue == null ) {
			currentValue=0;
		}
		// si los docs son iguales actualizo que se encontro, con el valor de la relevancia esperada
		if (resu.get("DOCNO").equals(exp.getDocumentId())) {
			// actualizo current value
			currentValue= exp.getRelevance();
		}
		// guardo el valor actualizado
		resultMap.put(currentKey,currentValue);
	}

	@XmlElement(name="precision")
	public double getPrecision() {
		return precision;
	}
	@XmlElement(name="recall")
	public double getRecall() {
		return recall;
	}
	@XmlElement(name="fMeasure")
	public double getfMeasure() {
		return fMeasure;
	}
	@XmlElement(name="totalRelevantes")
	public Integer getTotalRelevantes() {
		return totalRelevantes;
	}
	@XmlElement(name="relevantesObtenidos")
	public Long getRelevantesObtenidos() {
		return relevantesObtenidos;
	}
	@XmlElement(name="totalObtenidos")
	public Integer getTotalObtenidos() {
		return totalObtenidos;
	}


	@XmlElement(name="rPrecision")
	public double getrPrecision() {
		return rPrecision;
	}
	
	public List<SolrDocument> getTopRPrecisionDocs() {
		return topRPrecisionDocs;
	}

	@XmlElement(name="topRRelevantesObtenidos")
	public Long getTopRRelevantesObtenidos() {
		return topRRelevantesObtenidos;
	}
	@XmlElement(name="queryId")
	public String getQueryId() {
		return queryId;
	}
	@XmlElement(name="topRPrecisionCant")
	public Integer getTopRPrecisionCant() {
		return topRPrecisionCant;
	}
	public Map<Integer, Integer> getRelevantsReport() {
		return relevantsReport;
	}
	public Map<Integer, Integer> getTopRRelevantsReport() {
		return topRRelevantsReport;
	}	
}