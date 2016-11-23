package ar.com.juliospa.edu.textmining.tp3.ner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * para facilitar ver los resultados por archivo
 * doc parsed values es las sentencias
 * doc as value es tomar el document complet como input
 */
public class NerOnDoc {

	private String docName;
	private String docFullPath;
	private Map<String, List<String>> docParsedSources = new HashMap<>();
	private Map<String, List<ModelAppliedOutput>> docModelOutputs = new HashMap<>();
	public String getDocName() {
		return docName;
	}
	public String getDocFullPath() {
		return docFullPath;
	}
	public Map<String, List<ModelAppliedOutput>> getDocModelOutputs() {
		return docModelOutputs;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public void setDocFullPath(String docFullPath) {
		this.docFullPath = docFullPath;
	}
	public void setDocModelOutputs(Map<String, List<ModelAppliedOutput>> docModelOutputs) {
		this.docModelOutputs = docModelOutputs;
	}
	public Map<String, List<String>> getDocParsedSources() {
		return docParsedSources;
	}
	public void setDocParsedSources(Map<String, List<String>> docParsedSources) {
		this.docParsedSources = docParsedSources;
	}
}
