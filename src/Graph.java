import java.io.IOException;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.Chart;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;

public class Graph {
	static void plot(String chartTitle, String xTitle, String yTitle, String seriesNames, double[] x, double[] y, String filename) {
	    // Create Chart
	    Chart chart = QuickChart.getChart(chartTitle, xTitle, yTitle, seriesNames, x, y);
	    // Show it
	    new SwingWrapper(chart).displayChart();
	    // Save it
	    try {
	    	if(filename != null){
				BitmapEncoder.saveBitmap(chart, filename, BitmapFormat.PNG);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		double[] x = {1, 2, 3};
		double[] y = {4, 5, 6};
		Graph.plot("Sample", "X", "Y", null, x, y, null);
	}
}