package css.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import css.servlet.processor.InsertProcessor;
import css.servlet.processor.Processor;
import css.servlet.processor.QueryProcessor;
import css.servlet.processor.UpdateProcessor;

public class FinanceServlet extends HttpServlet {
	private HttpServletRequest req = null;
	private HttpServletResponse resp = null;
	private OutputStream out = null;
	
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
			String cmd = req.getPathInfo().substring(1);
			System.out.println("cmd:"+cmd);
			Processor p=null;
			if(cmd.equals("query")) p=new QueryProcessor();
			else if(cmd.equals("update")) p=new UpdateProcessor();
			else if(cmd.equals("insert")) p=new InsertProcessor();
			if(p!=null) p.process(req,resp,out);
		} catch (Exception e) {
			FUtils.print(e.getMessage(), resp, out);
			e.printStackTrace();
		}
	}

}
