package ar.com.juliospa.edu.textmining.domain.tp1;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import ar.com.juliospa.edu.textmining.utils.TextMiningUtils;

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
	
	public String measuresAllToString(){
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

//		System.out.println(df.format(myValue));
		
		StringBuilder build = new StringBuilder();
		build.append("run_ID").append(TextMiningUtils.TAB);
		build.append("index_comments").append(TextMiningUtils.TAB);
		build.append("query_comments").append(TextMiningUtils.TAB);
		
		build.append("query_id").append(TextMiningUtils.TAB);
		build.append("total_obtenidos").append(TextMiningUtils.TAB);
		build.append("total_relevantes").append(TextMiningUtils.TAB);
		build.append("relevantes_obtenidos").append(TextMiningUtils.TAB);
		build.append("top_r_precision_cant").append(TextMiningUtils.TAB);
		
		build.append("precision").append(TextMiningUtils.TAB);
		build.append("recall").append(TextMiningUtils.TAB);
		build.append("r_precision").append(TextMiningUtils.TAB);
		build.append("f_Measure").append(TextMiningUtils.TAB);
		build.append(TextMiningUtils.ENTER);
		for (MeasuresContainer measuresContainer : list) {

			for (Measures measure : measuresContainer.getList()) {
				build.append(measuresContainer.getRunId()).append(TextMiningUtils.TAB);
				build.append(measuresContainer.getIndexComents()).append(TextMiningUtils.TAB);
				build.append(measuresContainer.getQueryComments()).append(TextMiningUtils.TAB);
				
				build.append(measure.getQueryId()).append(TextMiningUtils.TAB);
				build.append(df.format(measure.getTotalObtenidos())).append(TextMiningUtils.TAB);
				build.append(df.format(measure.getTotalRelevantes())).append(TextMiningUtils.TAB);
				build.append(df.format(measure.getRelevantesObtenidos())).append(TextMiningUtils.TAB);
				build.append(df.format(measure.getTopRPrecisionCant())).append(TextMiningUtils.TAB);
				
				build.append(df.format(measure.getPrecision())).append(TextMiningUtils.TAB);
				build.append(df.format(measure.getRecall())).append(TextMiningUtils.TAB);
				build.append(df.format(measure.getrPrecision())).append(TextMiningUtils.TAB);
				build.append(df.format(measure.getfMeasure())).append(TextMiningUtils.TAB);
				build.append(TextMiningUtils.ENTER);
			}
		}
		return build.toString();
	}
	
}
