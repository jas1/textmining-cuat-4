package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

/**
 * El ebfoque de esta prueba es diferente, en vez de seguir probando parsear
 * vimos en prueba 2 que la metadata es muy util, 
 * tambien vimos que para alimentar el NER , es mejor en strings concisos, 
 * tipo oraciones.
 * Otra cosa que vimos en tutorial NER es que podemos pasar diferentes tipos de NER, 
 * para reeconocer diferentes tipos de entidades
 * 
 * Aca lo que se va a hacer es probar varios archivos, 
 * agarrar la metadata de esos archivos, 
 * pasar cada item de metadata por diferentes NER, 
 * para reconocer diferentes personas.
 * 
 * guardar los resultados en alguna clase, sabiendo que cada html es un evento.
 * las NER que se identificaron en la metadata de cada evento.
 * 
 * 
 * @author julio
 *
 */
public class ProbandoNER6 {
	Logger log = LoggerFactory.getLogger(ProbandoNER6.class);
	static{
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yy-MM-dd HH:mm:ss.SSS");
	}

	/**
	 * primera corrida de solo parser de a partes
	 * https://tika.apache.org/1.8/examples.html#Fetching_just_certain_bits_of_the_XHTML
	 */
	@Test
	public void probandoConParser() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		String sampleFile = filesUrl + "/viernes-23-05-14-alan-fitzpatrick-gala-cordoba.html";

		try {
			// detecting the file type
			BodyContentHandler handler = new BodyContentHandler(new ToHTMLContentHandler());
			Metadata metadata = new Metadata();
			FileInputStream inputstream = new FileInputStream(new File(sampleFile));
			ParseContext pcontext = new ParseContext();
		    AutoDetectParser parser = new AutoDetectParser();
	        parser.parse(inputstream, handler, metadata,pcontext);

	        System.out.println(handler.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
	@Test
	public void probandoConParser2() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		String sampleFile = filesUrl + "/viernes-23-05-14-alan-fitzpatrick-gala-cordoba.html";

		try {
			// detecting the file type
			XPathParser xhtmlParser = new XPathParser("xhtml", XHTMLContentHandler.XHTML);
	    	Matcher divContentMatcher = xhtmlParser.parse("//*[@class='posttitle icon']");
	    	MatchingContentHandler handler = new MatchingContentHandler(new ToHTMLContentHandler(), divContentMatcher);
	    	
	    	Metadata metadata = new Metadata();
			FileInputStream inputstream = new FileInputStream(new File(sampleFile));
			ParseContext pcontext = new ParseContext();
		    AutoDetectParser parser = new AutoDetectParser();

	        parser.parse(inputstream, handler, metadata,pcontext);
     
	        System.out.println(handler.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
}
