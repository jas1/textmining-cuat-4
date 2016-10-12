package ar.com.juliospa.edu.textmining.domain;

import javax.xml.bind.annotation.XmlElement;

public class QueryString {

//	<top>
//	<num> Number: OHSU10
//	<title> endocarditis
//	<desc> Description:
//	endocarditis, duration of antimicrobial therapy
//	</top>
	private String number;
	private String title;
	private String description;
	public String getNumber() {
		return number;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	@XmlElement(name="num")
	public void setNumber(String number) {
		this.number = number;
	}
	@XmlElement(name="title")
	public void setTitle(String title) {
		this.title = title;
	}
	@XmlElement(name="desc")
	public void setDescription(String description) {
		this.description = description;
	}
}
