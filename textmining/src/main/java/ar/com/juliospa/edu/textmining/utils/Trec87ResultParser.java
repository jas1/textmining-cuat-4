package ar.com.juliospa.edu.textmining.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

import ar.com.juliospa.edu.textmining.domain.ExpectedResult;
/**
 * para levantar los resultados esperados
 * @author julio
 *
 */
public class Trec87ResultParser {
	
	/**
	 * levanta el archivo de esperados
	 * devuelve listado de objetos
	 * @param filePath
	 * @return listado de objetos representando lo esperado
	 * @throws Exception
	 */
	public static List<ExpectedResult> parseExpectedResults(String filePath) throws Exception {

		List<ExpectedResult> resultList = new ArrayList<>();

		File file = new File(filePath);
		Scanner input = new Scanner(file);
		
		String spliter = "\t";
		
		while(input.hasNext()) {
			String line = input.nextLine().trim();
			String[] splited = line.split(spliter);
			
			ExpectedResult exp = new ExpectedResult();
			exp.setQueryId(splited[0]);
			exp.setDocumentId(Integer.parseInt(splited[1]));
			exp.setRelevance(Integer.parseInt(splited[2]));
			
			resultList.add(exp);

		}
	
		return resultList;
	}

	/**
	 * para obtener un mapa ordenado por ohsumedkey de cuantos hay por cada key.
	 * @param result
	 * @return
	 */
	public static Map<String,Long> expectedQuantitiesMap(List<ExpectedResult> result){
		return result.stream()
		.collect(Collectors.groupingBy(exp -> exp.getQueryId(), Collectors.counting()))
		.entrySet().stream()
			.sorted((e1, e2) -> sortByOHSUMEDKey(e1, e2))
			.collect(Collectors.toMap(p -> (String)p.getKey(), p -> (Long)p.getValue()));
	}
	
	public static void expectedQuantities(List<ExpectedResult> result) {
		expectedQuantitiesMap(result).entrySet().stream()
			.sorted((e1, e2) -> sortByOHSUMEDKey(e1, e2))
			.forEach(System.out::println);
	}

	public static int sortByOHSUMEDKey(Entry<String, Long> e1, Entry<String, Long> e2) {
		Integer e1Int = Integer.parseInt(e1.getKey().replace("OHSU", "").trim());
		Integer e2Int = Integer.parseInt(e2.getKey().replace("OHSU", "").trim());
		
		return e1Int.compareTo(e2Int);
	}
	
}
