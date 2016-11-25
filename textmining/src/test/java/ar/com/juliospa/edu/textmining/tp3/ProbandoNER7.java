package ar.com.juliospa.edu.textmining.tp3;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.parser.html.IdentityHtmlMapper;
import org.apache.tika.parser.ner.NamedEntityParser;
import org.apache.tika.parser.ner.regex.RegexNERecogniser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 * hasta ahora:
 * identificar las NER por NLP > problema de dominio especifico > los modelos existentes no satisfacen
 * 
 * hay que extraer segun patrones a definir 
 * 	> xpath dado que es xml/xhtml > tika se quedo en el camino implementacion pobre de xpath. 
 *  > regex 
 *  	> puedo darle un enfoque como el de nlp de definir muchos peque;os patrones que tengan para lo que quiero.
 *  	> desventaja > queda muy especifico para este caso. 
 *  	> como podria hacerse para generalizar esto ? se podria ? 
 * 
 * > luego de leer un poco mas porque no usar REGEX en tika ? ( si , todavia me niego a tirarlo al tacho ) 
 * 	> buscando un poco justo cai en la ficha de que existe> org.apache.tika.parser.ner.NERecogniser
 * 
 * eso es lo que se va a tratar aca. y armar un custom Ner recognizer , segun lo que dice
 * https://wiki.apache.org/tika/TikaAndNER#Using_Regular_Expressions
 * 
 * @author julio
 *
 */
public class ProbandoNER7 {
	Logger log = LoggerFactory.getLogger(ProbandoNER7.class);
	static{
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yy-MM-dd HH:mm:ss.SSS");
	}

//	
//    @Test
//    public void testGetEntityTypes() throws Exception {
//
//        String text = "Hey, Lets meet on this Sunday or MONDAY because i am busy on Saturday";
//        System.setProperty(NamedEntityParser.SYS_PROP_NER_IMPL, RegexNERecogniser.class.getName());
//
//        Tika tika = new Tika(new TikaConfig(NamedEntityParser.class.getResourceAsStream("tika-config.xml")));
//        Metadata md = new Metadata();
//        tika.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), md);
//
//        Set<String> days = new HashSet<>(Arrays.asList(md.getValues("NER_WEEK_DAY")));
//        assertTrue(days.contains("Sunday"));
//        assertTrue(days.contains("MONDAY"));
//        assertTrue(days.contains("Saturday"));
//        assertTrue(days.size() == 3); //and nothing else
//
//    }
	
	/**
	 * primera corrida de solo parser de a partes
	 * https://tika.apache.org/1.8/examples.html#Fetching_just_certain_bits_of_the_XHTML
	 * 
	 * https://wiki.apache.org/tika/TikaAndNER
	 * 
	 * https://fossies.org/linux/tika/tika-parsers/src/test/java/org/apache/tika/parser/ner/regex/RegexNERecogniserTest.java
	 * 
	 * problema ... otra vez ... hay que meter mano bajo nivel opensource> 
	 * NamedEntityParser > initialize >         //TODO: read class name from context or config
	 * RegexNERecogniser > 
	 */
	@Test
	public void probandoConRegexNERecogniser() {
		String dBoxUrl = "/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp3/";
		String filesUrl = dBoxUrl + "NER/archivoPrueba";
		String sampleFile = filesUrl + "/viernes-23-05-14-alan-fitzpatrick-gala-cordoba.html";

		try {
			// RegexNERecogniser, NamedEntityParser
			
			// detecting the file type
			DefaultHandler handler = new ToXMLContentHandler();
			Metadata metadata = new Metadata();
			FileInputStream inputstream = new FileInputStream(new File(sampleFile));
			ParseContext pcontext = new ParseContext();
			// esto 
			System.setProperty(NamedEntityParser.SYS_PROP_NER_IMPL, RegexNERecogniserCustomFix.class.getName());
			
			pcontext.set(HtmlMapper.class, new IdentityHtmlMapper());
			NamedEntityParser parser = new NamedEntityParser();
			
	        parser.parse(inputstream, handler, metadata,pcontext);

	        System.out.println(handler.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	
}
