package css.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;

import css.manager.ChartManager;
import css.manager.ReportChartManager;
import css.servlet.processor.InsertProcessor;
import css.servlet.processor.Processor;
import css.servlet.processor.QueryProcessor;
import css.servlet.processor.UpdateProcessor;

public class ChartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpServletRequest req = null;
	private HttpServletResponse resp = null;
	private OutputStream out = null;
	private ChartManager cm = null;
	
	
	public ChartServlet() {
		super();cm=new ChartManager();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.req=req;this.resp=resp;this.out=resp.getOutputStream();
		process();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.req=req;this.resp=resp;this.out=resp.getOutputStream();
		process();
	}

	private void process() {
		try {
			String type=req.getPathInfo().substring(1);
			if(type.equalsIgnoreCase("memory")){
				List<String[]> data=(List<String[]>)req.getSession().getAttribute("data");
				String cat=(String)req.getSession().getAttribute("cat");//chart category:pie,bar
				ChartUtilities.writeChartAsJPEG(out,
						ReportChartManager.createMemoryChart(data,cat),
					450,450);
				out.flush();out.close();
			}else{
				String sql=req.getParameter("sql");
				ChartUtilities.writeChartAsJPEG(out, cm.createChart(type,sql), 450, 450);
				out.flush();out.close();
			}
		} catch (Exception e) {
			FUtils.print(e.getMessage(), resp, out);
			e.printStackTrace();
		}
	}

}
