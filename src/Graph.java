import java.io.IOException;
import java.util.ArrayList;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.Series;
import org.knowm.xchart.SeriesMarker;
import org.knowm.xchart.Chart;
import org.knowm.xchart.QuickChart;

/**
 * A class used to graph the data collected during the simulation of the network.
 * We use the xChart API to facilitate graphing
 */
public class Graph {
	static Chart plot(String chartTitle, String xTitle, String yTitle,
			String seriesName, ArrayList<Double> x, ArrayList<Double> y,
			String filename, Chart chart) {

		// Organize data as arrays
		double[] xArray = new double[x.size()];
		double[] yArray = new double[y.size()];
		for (int i = 0; i < x.size(); i++) {
			xArray[i] = x.get(i);
			yArray[i] = y.get(i);
		}

		if (chart == null)
			// Create a new chart
			chart = QuickChart.getChart(chartTitle, xTitle, yTitle, seriesName,
					xArray, yArray);
		else {
			// Add a new series to the existing chart
			Series s = chart.addSeries(seriesName, xArray, yArray);
			s.setMarker(SeriesMarker.NONE);

		}

		// Save it if the filename is not null
		try {
			if (filename != null) {

				BitmapEncoder.saveBitmap(chart, "plots/" + filename,
						BitmapFormat.PNG);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return chart;
	}
}