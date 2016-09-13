package ar.com.juliospa.edu.textmining;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

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
	
}
