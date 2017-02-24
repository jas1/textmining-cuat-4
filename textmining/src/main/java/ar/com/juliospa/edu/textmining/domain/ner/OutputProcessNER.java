package ar.com.juliospa.edu.textmining.domain.ner;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ar.com.juliospa.edu.textmining.tp3.ner.NerOnDoc;

public class OutputProcessNER {
	private Set<UrlGuardada> setResultNers;
	private Set<UrlGuardada> noClasificaFechas = new HashSet<>();
	private Set<UrlGuardada> noClasificaUbicacion = new HashSet<>();
	private Set<UrlGuardada> noClasificaEvento = new HashSet<>();
	private Set<UrlGuardada> casosRaros = new HashSet<>();
	
	private Map<String, NerOnDoc> resultNERopenNLP;
	
	public OutputProcessNER() {
	}
	
	public String showResumen(){
		StringBuilder build = new StringBuilder();
		build.append("procesados: ").append(setResultNers.size()).append("\n");
		build.append("noClasificaFechas: ").append(noClasificaFechas.size()).append("\n");
		build.append("noClasificaUbicacion: ").append(noClasificaUbicacion.size()).append("\n");
		build.append("noClasificaEvento: ").append(noClasificaEvento.size()).append("\n");
		build.append("casosRaros: ").append(casosRaros.size()).append("\n");
		return build.toString();
	}
	
	public Set<UrlGuardada> getSetResultNers() {
		return setResultNers;
	}

	public Set<UrlGuardada> getNoClasificaFechas() {
		return noClasificaFechas;
	}

	public Set<UrlGuardada> getNoClasificaUbicacion() {
		return noClasificaUbicacion;
	}

	public Set<UrlGuardada> getNoClasificaEvento() {
		return noClasificaEvento;
	}

	public Set<UrlGuardada> getCasosRaros() {
		return casosRaros;
	}

	public void setSetResultNers(Set<UrlGuardada> setResultNers) {
		this.setResultNers = setResultNers;
	}

	public void setNoClasificaFechas(Set<UrlGuardada> noClasificaFechas) {
		this.noClasificaFechas = noClasificaFechas;
	}

	public void setNoClasificaUbicacion(Set<UrlGuardada> noClasificaUbicacion) {
		this.noClasificaUbicacion = noClasificaUbicacion;
	}

	public void setNoClasificaEvento(Set<UrlGuardada> noClasificaEvento) {
		this.noClasificaEvento = noClasificaEvento;
	}

	public void setCasosRaros(Set<UrlGuardada> casosRaros) {
		this.casosRaros = casosRaros;
	}

	public Map<String, NerOnDoc> getResultNERopenNLP() {
		return resultNERopenNLP;
	}

	public void setResultNERopenNLP(Map<String, NerOnDoc> resultNERopenNLP) {
		this.resultNERopenNLP = resultNERopenNLP;
	}
}
