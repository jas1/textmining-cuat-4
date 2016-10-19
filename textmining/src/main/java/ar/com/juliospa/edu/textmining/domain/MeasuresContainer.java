package ar.com.juliospa.edu.textmining.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * contenedor de las mediciones
 * ademas tiene otros valores del total de la corrida dado que las mediciones se calculan para cada query, 
 * esto seria un agregador de TODAS las mediciones de todas las querys con ciertos parametros
 * index comments: comentarios sobre el proceso de indexacion de esta corrida
 * query comments: comentarios sobre la configuracion de la query en esta corrida
 * @author julio
 *
 */
@XmlRootElement(name="measureRun")
public class MeasuresContainer  {
	private String runId; 
	private String indexComents;
	private String queryComments;
	private List<Measures>  list = new ArrayList<>();
	
	public MeasuresContainer() {
	}
	@XmlElement(name="measure")
	@XmlElementWrapper(name="measures")
	public List<Measures> getList() {
		return list;
	}

	public void setList(List<Measures> list) {
		this.list = list;
	}

	@XmlElement(name="runId")
	public String getRunId() {
		return runId;
	}
	@XmlElement(name="indexComments")
	public String getIndexComents() {
		return indexComents;
	}
	@XmlElement(name="queryComments")
	public String getQueryComments() {
		return queryComments;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public void setIndexComents(String indexComents) {
		this.indexComents = indexComents;
	}

	public void setQueryComments(String queryComments) {
		this.queryComments = queryComments;
	}
}
