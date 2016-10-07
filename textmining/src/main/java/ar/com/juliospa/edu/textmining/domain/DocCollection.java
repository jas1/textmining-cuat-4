package ar.com.juliospa.edu.textmining.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="COLLECTION")
public class DocCollection {

	private List<Doc>  documents = new ArrayList<>();

	public List<Doc> getDocuments() {
		return documents;
	}
	@XmlElement(name="DOC")
	public void setDocuments(List<Doc> documents) {
		this.documents = documents;
	}
}
