package css.servlet.processor;

import css.manager.QueryManager;
import css.servlet.FUtils;

public class UpdateProcessor extends Processor {

	@Override
	public void process() throws Exception {
		String sql=req.getParameter("sql");
		if(sql==null)sql="";
		if(sql.equals("")){
			System.out.println("----sql is null, do nothing");
		}
		sql=new String(sql.getBytes("iso8859-1"),"utf8");
		System.out.println("sql is:"+sql);
		String str=QueryManager.update(sql);
		FUtils.print(str,resp, out);
	}

}
