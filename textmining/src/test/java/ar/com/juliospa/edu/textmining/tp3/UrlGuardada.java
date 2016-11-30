package ar.com.juliospa.edu.textmining.tp3;

import java.util.Date;

public class UrlGuardada {

//	destino: foro/subforo/thread
	private TipoUrl tipo;
//	fuente extraccion	
	private String fuenteOriginal;
//	url: url a la que apunta
	private String url;
//	nombre original: nombre del tema o foro
	private String nombreOriginal;
//	observacion: "ej: pagina 5" , " no estandarizado " > si no matchea con ninguna regex
	private String observacion;
//	fecha: viernes-19-11-04 > parseado date  > regex en el nombre original
	private Date fecha;
//	nombre evento: get out, residents night, > regex en el nombre original
	private String nombreEvento;
//	lugar evento: hurlingam , pacha , mint	> regex en el nombre original
	private String lugarEvento;
	
	private Integer cantSubForos=0;
	private Integer cantThreads=0;
	
	public UrlGuardada() {}
	
	public UrlGuardada(TipoUrl tipo,String nombreOriginal,String fuente,String url,String observacion) {
		this.tipo=tipo;
		this.url=url;
		this.fuenteOriginal=fuente;
		this.nombreOriginal=nombreOriginal;
		this.observacion=observacion;
	}
	
	@Override
		public String toString() {
			
			return "["+super.toString()+"]"+tipo+" - "+nombreOriginal+" - ["+ cantThreads +"/"+cantSubForos+"] - "+observacion;
		}
	
	public TipoUrl getTipo() {
		return tipo;
	}
	public String getUrl() {
		return url;
	}
	public String getNombreOriginal() {
		return nombreOriginal;
	}
	public String getObservacion() {
		return observacion;
	}
	public Date getFecha() {
		return fecha;
	}
	public String getNombreEvento() {
		return nombreEvento;
	}
	public String getLugarEvento() {
		return lugarEvento;
	}
	public void setTipo(TipoUrl tipo) {
		this.tipo = tipo;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setNombreOriginal(String nombreOriginal) {
		this.nombreOriginal = nombreOriginal;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public void setNombreEvento(String nombreEvento) {
		this.nombreEvento = nombreEvento;
	}
	public void setLugarEvento(String lugarEvento) {
		this.lugarEvento = lugarEvento;
	}

	public String getFuenteOriginal() {
		return fuenteOriginal;
	}

	public void setFuenteOriginal(String fuenteOriginal) {
		this.fuenteOriginal = fuenteOriginal;
	}

	public Integer getCantSubForos() {
		return cantSubForos;
	}

	public Integer getCantThreads() {
		return cantThreads;
	}

	public void setCantSubForos(Integer cantSubForos) {
		this.cantSubForos = cantSubForos;
	}

	public void setCantThreads(Integer cantThreads) {
		this.cantThreads = cantThreads;
	}
}