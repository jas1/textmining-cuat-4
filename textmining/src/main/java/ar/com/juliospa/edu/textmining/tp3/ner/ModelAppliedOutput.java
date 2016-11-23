package ar.com.juliospa.edu.textmining.tp3.ner;

import java.util.ArrayList;
import java.util.List;
/**
 * para vacilitar ver los resultados del modelo aplicado
 * path al modelo
 * entities resultantes
 * @author julio
 *
 */
public class ModelAppliedOutput {
	private String modelName;
	private String modelFullPath;
	private List<String> entities = new ArrayList<>();
	private Integer entitiesRecognized = 0;
	public String getModelFullPath() {
		return modelFullPath;
	}
	public List<String> getEntities() {
		return entities;
	}
	public void setModelFullPath(String modelFullPath) {
		this.modelFullPath = modelFullPath;
	}
	public void setEntities(List<String> entities) {
		this.entities = entities;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public Integer getEntitiesRecognized() {
		return entitiesRecognized;
	}
	public void setEntitiesRecognized(Integer entitiesRecognized) {
		this.entitiesRecognized = entitiesRecognized;
	}
}
