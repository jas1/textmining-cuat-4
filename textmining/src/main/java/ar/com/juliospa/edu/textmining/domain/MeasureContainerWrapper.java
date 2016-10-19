package ar.com.juliospa.edu.textmining.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * contenedor de mediciones de ejecuciones.
 * @author julio
 *
 */
@XmlRootElement(name="root")
public class MeasureContainerWrapper {
	private List<MeasuresContainer>  list = new ArrayList<>();

	@XmlElement(name="measureRun")
	@XmlElementWrapper(name="measureRuns")
	public List<MeasuresContainer> getList() {
		return list;
	}
}
