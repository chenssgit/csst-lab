package css.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;

import css.manager.ChartManager;
import css.servlet.processor.InsertProcessor;
import css.servlet.processor.Processor;
import css.servlet.processor.QueryProcessor;
import css.servlet.processor.UpdateProcessor;

public class ReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpServletRequest req = null;
	private HttpServletResponse resp = null;
	private OutputStream out = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.req = req;
		this.resp = resp;
		this.out = resp.getOutputStream();
		process();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.req = req;
		this.resp = resp;
		this.out = resp.getOutputStream();
		process();
	}

	private void process() {
		System.out.println("ReportServlet");
		try {
			String cmd = req.getPathInfo().substring(1);
			String type = req.getParameter("type");
			String sql = req.getParameter("sql");
			String sql2 = req.getParameter("sql2");
			String month = req.getParameter("month");
			String cat = req.getParameter("cat");//chart category:pie, bar
			String sum = fetchSum(month);
			List<String[]> data = DBUtils.queryData(sql);
			List<String[]> data2 = DBUtils.queryData(sql2);
			req.getSession().setAttribute("data", data);
			req.getSession().setAttribute("cat", cat);
//			String records = DBUtils.toHTML(data);
			String records2 = DBUtils.toHTML(data2,true);

			String outString = "<table><tr><td colspan=3 style='font-size:40px'>"+sum+"</td></tr>"
					+ "<tr><td><img style='height:480px;width:460px;overflow:auto' src='chart/memory?ts="
					+ new Date().getTime()
					+ "'></td><td><div style='height:480px;width:650px;overflow:auto'>"
					+ records2 + "</div></td></tr></table>";
			out.write(outString.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			FUtils.print(e.getMessage(), resp, out);
			e.printStackTrace();
		}
	}

	private String fetchSum(String month){
		String sql="select sum(asset),sum(expense),-sum(liability),-sum(SE),-sum(revenue) from vf_balance_t ";
				sql+=" where tradeDate like '"+month+"%'";
		String[] sums=DBUtils.queryData(sql).get(1);
		String ret=sums[0]+"+"+sums[1]+"="+sums[2]+"+"+sums[3]+"+"+sums[4];
		return ret;
	}
}
