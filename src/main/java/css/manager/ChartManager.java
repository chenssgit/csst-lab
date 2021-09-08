package css.manager;

import java.sql.ResultSet;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.jfree.util.SortOrder;

import css.servlet.DBUtils;
import css.servlet.FUtils;

public class ChartManager {

public JFreeChart createChart(String type,String postsql) {
	PieDataset dataset = createDataset(type);
        JFreeChart chart = ChartFactory.createPieChart3D(
            type,  				// chart title
            dataset,                // data
            false,                   // include legend
            true,
            false
        );

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        return chart;
        
    }

private  PieDataset createDataset(String type) {
	DefaultPieDataset result = new DefaultPieDataset();
	if(type.equals("balance")) {
		List<String[]> data=DBUtils.queryData("select category,sum(famount) as famount from vf_balance where famount!=0 group by category");
	    for(int i=1,n=data.size();i<n;i++) 
	    	result.setValue(data.get(i)[0]+":"+data.get(i)[1], Double.parseDouble(data.get(i)[1]));
	}else if(type.equals("monthlyReport")) {
		List<String[]> data=DBUtils.queryData("SELECT name,amount FROM vf_tran3_m v where category='expense-expense' and cmonth='201205'");
	    for(int i=1,n=data.size();i<n;i++) 
	    	result.setValue(data.get(i)[0]+":"+data.get(i)[1], Double.parseDouble(data.get(i)[1]));
	}
	result.sortByValues(SortOrder.DESCENDING);
	return result;
}
}
