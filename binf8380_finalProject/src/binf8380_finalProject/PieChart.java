package binf8380_finalProject;

import java.math.BigDecimal;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.SortOrder;

public class PieChart {
	private final AbundanceSample data;
	//private final ChartPanel piePanel;
	private final JFreeChart pieChart;
	
	public PieChart(AbundanceSample data) {
		this.data = data;
		//this.piePanel = makePie();
		this.pieChart = makePie();
	}
	
	
	private /*ChartPanel*/ JFreeChart makePie() {
		// set title using current data ID
		String title = "Composition of " + data.getID() + " by Proportion";
		
		// set data
		HashMap<String, BigDecimal> propData = data.getAbundanceProportions();
		DefaultPieDataset pieData = new DefaultPieDataset(); // fill with corresponding data
		
		for (String key : propData.keySet()) {
			pieData.setValue(key, propData.get(key));
		}
				
		// sort pieData
		pieData.sortByValues(SortOrder.DESCENDING);

		// make chart & panel
		JFreeChart chart = ChartFactory.createPieChart(title, pieData, 
				true, //legend 
				false, // tool tips 
				false); // URLsfalse
		
		//ChartPanel piePanel = new ChartPanel(chart);
		//return piePanel;
		return chart;
	}
	
	public /*ChartPanel*/ JFreeChart getPie() {
		//return piePanel;
		return pieChart;
	}
	
	public AbundanceSample getData() {
		return data;
	}
}
