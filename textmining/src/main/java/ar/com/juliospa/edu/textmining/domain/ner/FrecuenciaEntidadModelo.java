package ar.com.juliospa.edu.textmining.tp3;

public class FrecuenciaEntidadModelo {

	private String entidad;
	private String model;
	private Integer freq=0;
	public FrecuenciaEntidadModelo(String entidad,String model) {
		this.entidad=entidad;
		this.model=model;
	}
	
	public void addOneToFreq(){
		freq++;
	}
	
	public String getEntidad() {
		return entidad;
	}
	public String getModel() {
		return model;
	}
	public Integer getFreq() {
		return freq;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public void setFreq(Integer freq) {
		this.freq = freq;
	}
}
