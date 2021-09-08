package css.servlet.processor;

import css.manager.QueryManager;
import css.servlet.FUtils;

public class QueryProcessor extends Processor {

	@Override
	public void process() throws Exception {
		String type=req.getParameter("type");
		String sql=req.getParameter("sql");
		if(type==null)type="";
		if(type.equals("")){
			System.out.println("----query is null, do nothing");
		}
		if(sql!=null)
			sql=new String(sql.getBytes("iso8859-1"),"utf8");
		System.out.println("querytype is:"+type);
		String str=QueryManager.query(type,sql);
		FUtils.print(str,resp, out);
	}

}
