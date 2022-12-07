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
	private final JFreeChart pieChart;
	
	public PieChart(AbundanceSample data) {
		this.data = data;
		this.pieChart = makePie();
	}
	
	
	private JFreeChart makePie() {
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

		// make chart
		JFreeChart chart = ChartFactory.createPieChart(title, pieData, 
				true, //legend 
				false, // tool tips 
				false); // URLsfalse
		return chart;
	}
	
	// return the pie chart object (NOT a panel)
	public JFreeChart getPie() {
		return pieChart;
	}
	
	// return data used in making the pie chart... just in case
	public AbundanceSample getData() {
		return data;
	}
}
