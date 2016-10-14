package ar.com.juliospa.edu.textmining.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="collection")
public class MeasuresContainer  {
	private List<Measures>  list = new ArrayList<>();

	@XmlElement(name="measures")
	public List<Measures> getList() {
		return list;
	}

	public void setList(List<Measures> list) {
		this.list = list;
	}
}
