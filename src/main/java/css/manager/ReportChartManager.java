package css.manager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.jfree.util.SortOrder;

public class ReportChartManager {

	public static JFreeChart createMemoryChart(List<String[]> data, String cat) {
		if(cat==null||cat.equalsIgnoreCase("pie")) return createPieChart(data);
		else return createBarChart(data);
	}
	
	private static JFreeChart createPieChart(List<String[]> data) {
		ArrayList<Double> sum = new ArrayList<Double>();
		PieDataset dataset = createDataset(data, sum);
		JFreeChart chart = ChartFactory.createPieChart3D(new DecimalFormat(
				"#.##").format(sum.get(0)), // chart title
				dataset, // data
				false, // include legend
				true, false);

		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		return chart;
	}

	private static PieDataset createDataset(List<String[]> data,
			ArrayList<Double> sum) {
		DefaultPieDataset result = new DefaultPieDataset();
		double d = 0;
		for (int i = 1, n = data.size(); i < n; i++) {
			double v = Double.parseDouble(data.get(i)[1]);
			d += v;
			result.setValue(data.get(i)[0] + ":" + data.get(i)[1], v);
		}
		result.sortByValues(SortOrder.DESCENDING);
		sum.add(d);
		return result;
	}

	private static JFreeChart createBarChart(List<String[]> data) {
		ArrayList<Double> sum = new ArrayList<Double>();
		String rowKey = data.get(0)[0];String columnKey = data.get(0)[1];
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		double d = 0;
		for (int i = 1, n = data.size(); i < n; i++) {
			double v = Double.parseDouble(data.get(i)[1]);
			d += v;
			dataset.setValue(v, rowKey, data.get(i)[0]);
		}
		sum.add(d);
		
		JFreeChart chart = ChartFactory.createBarChart(
				new DecimalFormat("#.##").format(sum.get(0)), rowKey,
				columnKey, dataset, PlotOrientation.HORIZONTAL, true,
				true, true);
		BarRenderer renderer = new BarRenderer();
		renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setItemLabelsVisible(true);
		chart.getCategoryPlot().setRenderer(renderer);
		return chart;
	}
}
