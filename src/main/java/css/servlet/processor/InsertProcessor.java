package css.servlet.processor;

import css.manager.QueryManager;
import css.servlet.FUtils;

public class InsertProcessor extends Processor {

	@Override
	public void process() throws Exception {
		String sql=req.getParameter("sql");
		if(sql==null)sql="";
		if(sql.equals("")){
			System.out.println("----insert sql is null, do nothing");
		}
		sql=new String(sql.getBytes("iso8859-1"),"utf8");
		System.out.println("insert sql is:"+sql);
		String str=QueryManager.insert(sql);
		FUtils.print(str,resp, out);
	}

}
