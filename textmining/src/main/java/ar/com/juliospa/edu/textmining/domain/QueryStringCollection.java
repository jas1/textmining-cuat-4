package ar.com.juliospa.edu.textmining.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * wrapper de las queries a realizar
 * @author julio
 *
 */
@XmlRootElement(name="collection")
public class QueryStringCollection {
	//query.ohsu.1-63.xml
	private List<QueryString>  tops = new ArrayList<>();

	public List<QueryString> getTops() {
		return tops;
	}
	@XmlElement(name="top")
	public void setTops(List<QueryString> tops) {
		this.tops = tops;
	}
}
