import java.io.IOException;
import java.util.ArrayList;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.Series;
import org.knowm.xchart.SeriesLineStyle;
import org.knowm.xchart.StyleManager.ChartType;
import org.knowm.xchart.Chart;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;

public class Graph {
	static void plot(String chartTitle, String xTitle, String yTitle, String seriesNames, ArrayList<Double> x, ArrayList<Double> y, String filename) {
	    // Create Chart
		double[] xArray = new double[x.size()];
		double[] yArray = new double[y.size()];
		
		for (int i = 0; i < x.size(); i++) {
			xArray[i] = x.get(i);
			yArray[i] = y.get(i);
		}
		
	    Chart chart = QuickChart.getChart(chartTitle, xTitle, yTitle, seriesNames, xArray,yArray);
	    // Show it
	    new SwingWrapper(chart).displayChart();
	    // Save it
	    try {
	    	if(filename != null){
	    		
				BitmapEncoder.saveBitmap(chart, "plots/" + filename, BitmapFormat.PNG);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void plotScatter(String chartTitle, String xTitle, String yTitle, String seriesName, ArrayList<Double> x, ArrayList<Double> y, String filename) {
		
		// Create Chart
		Chart chart = new Chart(800, 600);
		chart.getStyleManager().setChartType(ChartType.Scatter);
		
		// Customize Chart
		chart.setChartTitle(chartTitle);
		chart.setXAxisTitle(xTitle);
		chart.setYAxisTitle(yTitle);
		chart.getStyleManager().setMarkerSize(2);
		
		// Add series
		Series series = chart.addSeries(seriesName, x, y);
		series.setLineStyle(SeriesLineStyle.DASH_DASH);
		new SwingWrapper(chart).displayChart();
		
		// Save it
	    try {
	    	if(filename != null){
	    		
				BitmapEncoder.saveBitmap(chart, "plots/" + filename, BitmapFormat.PNG);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
//		double[] x = {1, 2, 3};
//		double[] y = {4, 5, 6};
//		Graph.plot("Sample", "X", "Y", null, x, y, "test");
	}
}