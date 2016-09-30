package ar.com.juliospa.edu.textmining;

import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class TextMiningUtilsTest {

	@Test
	public void scanForFilesTestLauNormativa() {
		String carpeta="/home/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/dataset/raw/lau-normativa/20150430_BASE_NORMATIVA";
		List<Path> result = TextMiningUtils.scanForFiles(carpeta);
		System.out.println(result.size());
		result.forEach(pa -> System.out.println(pa.toFile().getAbsolutePath()));
	}
	
}
