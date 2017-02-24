package ar.com.juliospa.edu.textmining.domain;

import java.lang.reflect.Method;

import javax.xml.bind.annotation.XmlElement;

/**
 * representacion de un documento a indexar
 * @author julio
 *
 */
public class Doc {

	private Integer id;
	private Integer docno;
	private String source;
	private String mesh;
	private String title;
	private String publicationType;
	private String docAbstract;
	private String author;
	public Integer getId() {
		return id;
	}
	public Integer getDocno() {
		return docno;
	}
	public String getSource() {
		return source;
	}
	public String getMesh() {
		return mesh;
	}
	public String getTitle() {
		return title;
	}
	public String getPublicationType() {
		return publicationType;
	}
	public String getDocAbstract() {
		return docAbstract;
	}
	public String getAuthor() {
		return author;
	}
	@XmlElement(name="ID")
	public void setId(Integer id) {
		this.id = id;
	}
	@XmlElement(name="DOCNO")
	public void setDocno(Integer docno) {
		this.docno = docno;
	}
	@XmlElement(name="SOURCE")
	public void setSource(String source) {
		this.source = source;
	}
	@XmlElement(name="MESH")
	public void setMesh(String mesh) {
		this.mesh = mesh;
	}
	@XmlElement(name="TITLE")
	public void setTitle(String title) {
		this.title = title;
	}
	@XmlElement(name="PUBLICATIONTYPE")
	public void setPublicationType(String publicationType) {
		this.publicationType = publicationType;
	}
	@XmlElement(name="ABSTRACT")
	public void setDocAbstract(String docAbstract) {
		this.docAbstract = docAbstract;
	}
	@XmlElement(name="AUTHOR")
	public void setAuthor(String author) {
		this.author = author;
	}
	public static String getFieldNameForXMl(String domainName,Class<?> tipo,Class<?> clazz) throws NoSuchMethodException, SecurityException {
		String fieldSetterMethod = "set"+domainName;
		Method setterField = clazz.getMethod(fieldSetterMethod, tipo );
		XmlElement xmElem = setterField.getAnnotation(XmlElement.class);
		return xmElem.name();
	}

//	<COLLECTION>@XmlRootElement
//	<DOC>
//		<ID>
//			 1
//		</ID>
//		<DOCNO>
//			87049087
//		</DOCNO>
//		<SOURCE>
//			Am J Emerg Med 8703; 4(6):491-5
//		</SOURCE>
//		<MESH>
//			Allied Health Personnel/*; Electric Countershock/*; Emergencies; Emergency Medical Technicians/*; Human; Prognosis; Recurrence; Support, U.S. Gov't, P.H.S.; Time Factors; Transportation of Patients; Ventricular Fibrillation/*TH.
//		</MESH>
//		<TITLE>
//			Refibrillation managed by EMT-Ds: incidence and outcome without paramedic back-up.
//		</TITLE>
//		<PUBLICATIONTYPE>
//			JOURNAL ARTICLE.
//		</PUBLICATIONTYPE>
//		<ABSTRACT>
//			Some patients converted from ventricular fibrillation to organized rhythms by defibrillation-trained ambulance technicians (EMT-Ds) will refibrillate before hospital arrival. The authors analyzed 271 cases of ventricular fibrillation managed by EMT-Ds working without paramedic back-up. Of 111 patients initially converted to organized rhythms, 19 (17%) refibrillated, 11 (58%) of whom were reconverted to perfusing rhythms, including nine of 11 (82%) who had spontaneous pulses prior to refibrillation. Among patients initially converted to organized rhythms, hospital admission rates were lower for patients who refibrillated than for patients who did not (53% versus 76%, P = NS), although discharge rates were virtually identical (37% and 35%, respectively). Scene-to-hospital transport times were not predictively associated with either the frequency of refibrillation or patient outcome. Defibrillation-trained EMTs can effectively manage refibrillation with additional shocks and are not at a significant disadvantage when paramedic back-up is not available.
//		</ABSTRACT>
//		<AUTHOR>
//			Stults KR; Brown DD.
//		</AUTHOR>
//	</DOC>
//	</COLLECTION>
	
}
