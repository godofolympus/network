import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.Series;
import org.knowm.xchart.SeriesLineStyle;
import org.knowm.xchart.SeriesMarker;
import org.knowm.xchart.StyleManager.ChartType;
import org.knowm.xchart.Chart;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;

public class Graph {
	static Chart plot(String chartTitle, String xTitle, String yTitle,
			String seriesName, ArrayList<Double> x, ArrayList<Double> y,
			String filename, Chart ch) {
		// Create Chart
		double[] xArray = new double[x.size()];
		double[] yArray = new double[y.size()];

		for (int i = 0; i < x.size(); i++) {
			xArray[i] = x.get(i);
			yArray[i] = y.get(i);
		}

		if(ch == null)
		 ch = QuickChart.getChart(chartTitle, xTitle, yTitle,
				seriesName, xArray, yArray);
		else{
			Series s = ch.addSeries(seriesName, xArray, yArray);
			s.setMarker(SeriesMarker.NONE);
			
		}
		// Show it
		//new SwingWrapper(chart).displayChart();
		// Save it
		try {
			if (filename != null) {

				BitmapEncoder.saveBitmap(ch, "plots/" + filename,
						BitmapFormat.PNG);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ch;
	}

	static void plotScatter(String chartTitle, String xTitle, String yTitle,
			String seriesName, ArrayList<Double> x, ArrayList<Double> y,
			String filename) {

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
			if (filename != null) {

				BitmapEncoder.saveBitmap(chart, "plots/" + filename,
						BitmapFormat.PNG);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void multiplot(String chartTitle, String xTitle, String yTitle,
			Set<String> seriesNames, ArrayList<Double> x,
			ArrayList<ArrayList<Double>> yList, String filename) {
		
		String[] series = new String[seriesNames.size()];
		double[] xArray = new double[x.size()];
		double[][] yArray = new double[yList.size()][yList.get(0).size()];
		
		
		int k = 0;
		for (String s : seriesNames) {
			series[k] = s;
			k++;
		}
		
		for (int i = 0; i < yList.size(); i++) {
			for (int j = 0; j < yList.get(0).size(); j++) {
				yArray[i][j] = yList.get(i).get(j);
			}
		}
		
		Chart chart = QuickChart.getChart(chartTitle, xTitle, yTitle,
				series[0], xArray, yArray[0]);
		
//		for (int i = 1; i < seriesNames.size(); i++) {
//			chart.addSeries(series[i], xArray, yArray[i]);
//		}
		
		// Show it
		new SwingWrapper(chart).displayChart();
		// Save it
		try {
			if (filename != null) {

				BitmapEncoder.saveBitmap(chart, "plots/" + filename,
						BitmapFormat.PNG);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// double[] x = {1, 2, 3};
		// double[] y = {4, 5, 6};
		// Graph.plot("Sample", "X", "Y", null, x, y, "test");
	}
}