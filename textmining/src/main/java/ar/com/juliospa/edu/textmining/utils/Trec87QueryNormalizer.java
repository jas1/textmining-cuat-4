package ar.com.juliospa.edu.textmining.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.tidy.Tidy;

import ar.com.juliospa.edu.textmining.domain.QueryString;
import ar.com.juliospa.edu.textmining.domain.QueryStringCollection;

/**
 * primer problema es que las queries no estan normalizadas.
 * le ejecuto un pretty html para que me cierre los tags asi despues puedo levantar las cosas.
 * @author julio
 *
 */
public class Trec87QueryNormalizer {

	
//	<top>
//	<num> Number: OHSU10
//	<title> endocarditis
//	<desc> Description:
//	endocarditis, duration of antimicrobial therapy
//	</top>
	
	private final static String TOP_INI ="<top>";
	private final static String NUM ="<num>";
	private final static String TITLE ="<title>";
	private final static String DESC ="<desc>";
	private final static String TOP_END ="</top>";
	
	
	/**
	 * no funciono.
	 * @param filePaht
	 * @param output
	 * @throws FileNotFoundException
	 */
	@Deprecated
	public void normalizeXhtml(String filePaht , String output) throws FileNotFoundException{
		File file = new File(filePaht);
		
		// in & out
		FileOutputStream fos = new FileOutputStream(output);
		FileInputStream is = new FileInputStream(file);
		
		// para custom tags
		Properties oProps = new Properties();
		oProps.setProperty("new-blocklevel-tags", "top num title desc");
//		oProps.setProperty("input-xml", "1");
		
		
        Tidy tidy = new Tidy();
        // para que abra y cierre
        tidy.setXHTML(true);
        // para setear los custom tags que definimos antes
        tidy.setConfigurationFromProps(oProps);
        
        // parsea el IN y escribe en OUT , aplicandole la configuracion que hicimos
        tidy.parse(is, fos);
	}
	
	/**
	 * para tener el listado de queries directo desde el xml 
	 * @param path
	 * @param fileDb
	 * @return
	 * @throws JAXBException
	 */
	public static QueryStringCollection parseQueries(String path, String fileDb) throws JAXBException {
		File file = new File(path+fileDb);
		JAXBContext jaxbContext = JAXBContext.newInstance(QueryStringCollection.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		QueryStringCollection queryCol = (QueryStringCollection) jaxbUnmarshaller.unmarshal(file);
		return queryCol;
	}

	public static QueryStringCollection parseQueryColFromFilePath(String filePaht) throws Exception {
		File file = new File(filePaht);
		Scanner input = new Scanner(file);
		QueryStringCollection result = new QueryStringCollection();

		String currentBlock = "";
		String currentBlockAcum = "";
		int cantDocsprocessed=0;
		QueryString currentDoc = null;
		while(input.hasNext()) {
		    //String nextToken = input.next();
		    //or to process line by line			
			String line = input.nextLine().trim();
			if (line.length()>0) {
				if(line.startsWith(TOP_INI)){
					currentBlock=TOP_INI;
					currentDoc = new QueryString();
				}else if(line.startsWith(TOP_END)){
					
					// cierra la descripcion
					if (currentBlock.equals(DESC)) {
						currentDoc.setDescription(currentBlockAcum.trim());
					}
					
					currentBlock=TOP_END;
					//si existia current doc, entonces hay que guardarlo en la coleccion
					if (currentDoc!=null) {
						result.getTops().add(currentDoc);
						cantDocsprocessed++;
						//System.out.println(cantDocsprocessed);
					}
				}else if(line.startsWith(NUM)){
					currentBlock=NUM;
					String[] tmp = line.split(":");
					currentDoc.setNumber(tmp[1].trim());
				}else if(line.startsWith(TITLE)){
					currentBlock=TITLE;
					String tmp = line.replace(TITLE, "");
					currentDoc.setTitle(tmp.trim());					
				}else if(line.startsWith(DESC)){
					currentBlock=DESC;
					currentBlockAcum="";
				}else{
					if (currentBlock.equals(DESC)) {
						currentBlockAcum+=line.trim();
					}// si no es desc, salta, porque todos son oneliners excepto este.
				}
			}else{
				//	no hace nada, saltea la linea
			}
		}

		// cierro el input
		input.close();
		System.out.println(cantDocsprocessed);
		return result;
	}
}
