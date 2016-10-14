package ar.com.juliospa.edu.textmining.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import edu.stanford.nlp.process.Stemmer;

public class TextMiningUtils {

	/**
	 * escanea la carpeta para ver los archivos que hay 
	 * @return
	 */
	public static List<Path> scanForFiles(String aPath){
		Path path= Paths.get(aPath);
		 final List<Path> files=new ArrayList<>();
		 try {
		    Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
		     @Override
		     public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		          if(!attrs.isDirectory()){
		               files.add(file);
		          }
		          return FileVisitResult.CONTINUE;
		      }
		     });
		 } catch (IOException e) {
		      e.printStackTrace();
		 }
		return files;
	}

	public static void objectsToXml(String pathOutMeasures, String fileOutMeasures, Object measures,
			Class<?> classToMarshal) throws JAXBException, PropertyException {
		// guardar measures en xml
		File file = new File(pathOutMeasures+fileOutMeasures);
	
		JAXBContext jaxbContext = JAXBContext.newInstance(classToMarshal);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	
		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	
		jaxbMarshaller.marshal(measures, file);
		System.out.println(file.getAbsoluteFile());
	}

	public static String stemSentence(String tmp) {
		if (tmp!=null && tmp.length()>0) {
			Stemmer asd = new Stemmer();		
			String[] tmplist = tmp.split(" ");
			List<String> acum = new ArrayList<>();
			Arrays.asList(tmplist).stream().forEach(str -> acum.add(asd.stem(str)));
			return String.join(" ", acum);	
		}
		return tmp;
	}
	
}
