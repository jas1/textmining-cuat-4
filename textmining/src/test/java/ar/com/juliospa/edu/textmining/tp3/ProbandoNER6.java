package ar.com.juliospa.edu.textmining.tp3;

import java.io.File;
import java.io.FileInputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.html.IdentityHtmlMapper;
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
import org.xml.sax.helpers.DefaultHandler;

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
	/**
	 * aca el tema es la busqueda de poder filtrar
	 * por loq ue venia viendo el tema no era el xpath especifico sino que documento se terminaba levantando sin los tags
	 * o sin las clases
	 * entonces 
	 * 1) descubri que como levante el doc depende del content handler, gracias al tika in action>  5.5.2 Custom document handling
	 * 2) si le mandas de handler el body , te morfa la gran mayoria de tags , y los estilos y ids.
	 * 3) le mando XML asi levanta asi no lo levanta de 1 de esa forma
	 * 4) luego el parser es como interpreta ese documento levantado, si no lo levantas bien tambien vas a malinterpretar las csoas
	 * 5) el parser se mueve en un contexto , si no le especificas nada, esto toma por default que queres madnar todo a texto
	 * 6) hay que configurar el contexto de parseo , con identity html mapper, que levanta el html tal cual vino, lo cual puede traer cosas malformadas.
	 * 7) ya con estos filtros pude levantar el html , con todos los chiches, hago otro metodo para probar el filtro.
	 * si uso 
	 */
	@Test
	public void probandoConParser3() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		String sampleFile = filesUrl + "/viernes-23-05-14-alan-fitzpatrick-gala-cordoba.html";

		try {
			// detecting the file type
			DefaultHandler handler = new ToXMLContentHandler();
			Metadata metadata = new Metadata();
			FileInputStream inputstream = new FileInputStream(new File(sampleFile));
			ParseContext pcontext = new ParseContext();
			// esto 
			pcontext.set(HtmlMapper.class, new IdentityHtmlMapper());
			Parser parser = new HtmlParser();
	        parser.parse(inputstream, handler, metadata,pcontext);

	        System.out.println(handler.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	/** luego de probar bastante, cai en que lo que pasa es el problema de reuirle a la info de la api.
	 * hace falta leerlo pro mas que generalmente apeste en este caso estaba ahi la respuesta: 
	 *  " Parser for a very simple XPath subset. Only the following XPath constructs (with namespaces) are supported: " 
	 *     " .../node() , .../text(), .../@*, .../@name, .../name... , ...//*... , ...//name... "
	 *     como tika es muy groso , quiero ver si encuentro una implementacion mas completa ...
	 *     
	 *     dado el poco tiempo que hay > queda para futuros trabajos.
	 *     voy a probar con regex sin usar tika.
	 */
	@Test
	public void probandoConParser2() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		String sampleFile = filesUrl + "/viernes-23-05-14-alan-fitzpatrick-gala-cordoba.html";

		try {
			// detecting the file type
			XPathParser xhtmlParser = new XPathParser("xhtml", XHTMLContentHandler.XHTML);
//			XPathParser xhtmlParser = new XPathParser("xhtml", XHTMLContentHandler.XHTML);
//	    	Matcher divContentMatcher = xhtmlParser.parse("//*[@class='content']");
//			Matcher divContentMatcher = xhtmlParser.parse("//li");
			Matcher divContentMatcher = xhtmlParser.parse("//xhtml:li");
//			Matcher divContentMatcher = xhtmlParser.parse("/xhtml:html/xhtml:body/");
	    	MatchingContentHandler handler = new MatchingContentHandler(new ToXMLContentHandler(), divContentMatcher);
	    	
	    	Metadata metadata = new Metadata();
			FileInputStream inputstream = new FileInputStream(new File(sampleFile));
			ParseContext pcontext = new ParseContext();
			pcontext.set(HtmlMapper.class, new IdentityHtmlMapper());
			
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
