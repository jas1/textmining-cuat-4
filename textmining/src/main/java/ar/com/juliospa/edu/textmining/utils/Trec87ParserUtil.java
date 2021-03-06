package ar.com.juliospa.edu.textmining.utils;

import java.io.File;
import java.util.Scanner;

import ar.com.juliospa.edu.textmining.domain.tp1.Doc;
import ar.com.juliospa.edu.textmining.domain.tp1.DocCollection;

/**
 * proceso para transformar el archivo TREC a objetos
 * @author julio
 *
 */
public class Trec87ParserUtil {

	private static final String I = ".I";
	private static final String U = ".U";
	private static final String M = ".M";
	private static final String T = ".T";
	private static final String P = ".P";
	private static final String W = ".W";
	private static final String A = ".A";
	private static final String S = ".S";
	
	// ENTRA: 
//	.I      sequential identifier (important note: documents should be processed in this order)
//	.U      MEDLINE identifier (UI) (<DOCNO> used for relevance judgements)
//	.M      Human-assigned MeSH terms (MH)
//	.T      Title (TI)
//	.P      Publication type (PT)
//	.W      Abstract (AB)
//	.A      Author (AU)
//	.S      Source (SO)
//SALE
//<DOC>
//	<ID></ID>
//	<DOCNO>	</DOCNO>
//	<SOURCE></SOURCE>
//	<MESH></MESH>
//	<TITLE></TITLE>
//	<PUBLICATIONTYPE></PUBLICATIONTYPE>
//	<ABSTRACT></ABSTRACT>
//	<AUTHOR></AUTHOR>
//</DOC>
//como lo lograria : parsear > popular el entity > marshalear a xml
	
	
/**
 * devuelve una coleccion de Documentos en modo objeto
 * @param filePaht
 * @return
 * @throws Exception
 */
	public static DocCollection parseDocCollectionFromFilePath(String filePaht) throws Exception {
		File file = new File(filePaht);
		Scanner input = new Scanner(file);
		DocCollection result = new DocCollection();

		String currentBlock = "";
		String currentBlockAcum = "";
		int cantDocsprocessed = 0;
		Doc currentDoc = null;
		while(input.hasNext()) {
		    //String nextToken = input.next();
		    //or to process line by line			
			String line = input.nextLine();
			if(line.startsWith(I)){
				// no acumula porque es inline
				currentBlock = I;
				//si existia current doc, entonces hay que guardarlo en la coleccion
				if (currentDoc!=null) {
					result.getDocuments().add(currentDoc);
					cantDocsprocessed++;
					//System.out.println(cantDocsprocessed);
				}
				//actualiza current doc
				currentDoc = parseI(line);
				// salta linea
			}else{
			    
			    // .I.U.M.T.P.W.A.S
				if(line.startsWith(U)){
					closeEndingBLock(currentBlock,currentDoc,currentBlockAcum);
					currentBlock = U;	
					currentBlockAcum = "";
				}else if(line.startsWith(M)){
					closeEndingBLock(currentBlock,currentDoc,currentBlockAcum);
					currentBlock = M;
					currentBlockAcum = "";
					
				}else if(line.startsWith(T)){
					closeEndingBLock(currentBlock,currentDoc,currentBlockAcum);
					currentBlock = T;
					currentBlockAcum = "";
				}else if(line.startsWith(P)){
					closeEndingBLock(currentBlock,currentDoc,currentBlockAcum);
					currentBlock = P;
					currentBlockAcum = "";
				}else if(line.startsWith(W)){
					closeEndingBLock(currentBlock,currentDoc,currentBlockAcum);
					currentBlock = W;
					currentBlockAcum = "";
				}else if(line.startsWith(A)){
					closeEndingBLock(currentBlock,currentDoc,currentBlockAcum);
					currentBlock = A;
					currentBlockAcum = "";
				}else if(line.startsWith(S)){
					closeEndingBLock(currentBlock,currentDoc,currentBlockAcum);
					currentBlock = S;
					currentBlockAcum = "";
				}else{
					// es contenido intermedio, va al acumulador
					currentBlockAcum+=line;
				}
			}
			if (cantDocsprocessed % 100 == 0) {
				System.out.println(cantDocsprocessed);
			}
		}
		System.out.println(cantDocsprocessed);
		// cierro el ultimo bloque
		closeEndingBLock(currentBlock,currentDoc,currentBlockAcum);
		// agrego el ultimo doc.
		result.getDocuments().add(currentDoc);
		// cierro el input
		input.close();

		return result;
	}

	/**
	 * para cerrar el bloque.
	 * hay que saber que bloque cierra, hay que asignarle al campo correspondiente, lo acumulado en el bloque.
	 * actualiza el objeto DOC.
	 * @param currentBlock el bloque que cierra
	 * @param currentDoc el doc que se popula
	 * @param currentBlockAcum lo acumulado hasta el momento que va para el closing block
	 * @throws Exception en case its not a value from the inputs
	 */
	private static void closeEndingBLock(String currentBlock, Doc currentDoc, String currentBlockAcum) throws Exception {
//		.U      MEDLINE identifier (UI) (<DOCNO> used for relevance judgements)
//		.M      Human-assigned MeSH terms (MH)
//		.T      Title (TI)
//		.P      Publication type (PT)
//		.W      Abstract (AB)
//		.A      Author (AU)
//		.S      Source (SO)
		if(currentBlock.startsWith(I)){
			// no hago nada porque ya lo hice antes porque era inline
		}else if(currentBlock.equals(U)){
			currentDoc.setDocno(Integer.parseInt(currentBlockAcum));
		}else if(currentBlock.equals(M)){
			currentDoc.setMesh(currentBlockAcum);
		}else if(currentBlock.equals(T)){
			currentDoc.setTitle(currentBlockAcum);
		}else if(currentBlock.equals(P)){
			currentDoc.setPublicationType(currentBlockAcum);
		}else if(currentBlock.equals(W)){
			currentDoc.setDocAbstract(currentBlockAcum);
		}else if(currentBlock.equals(A)){
			currentDoc.setAuthor(currentBlockAcum);
		}else if(currentBlock.equals(S)){
			currentDoc.setSource(currentBlockAcum);
		}else{
			throw new Exception("malformedBlockException:"+currentBlock + " , value:"+currentBlockAcum);
		}
	}

	/**
	 * para parsear linea de I, que es un caos especial.
	 * @param line
	 * @return
	 */
	private static Doc parseI(String line) {
		Doc ret = new Doc();
		ret.setId(Integer.parseInt(line.split(" ")[1]));
		return ret;
	}
	
}
