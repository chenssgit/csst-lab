package css.manager;

import java.sql.ResultSet;

import css.servlet.DBUtils;
import css.servlet.FUtils;

public class QueryManager {

	public static String query(String type,String sql) {
		String ret="";
		if(type==null) return "";
		else if(type.equals("tran")) {
			ret=DBUtils.query(getQuery(type)+sql,"html");
		}else if(type.equals("funds")) {
			ret=DBUtils.query(getQuery(type),"json");
		}else if(type.equals("report")||type.equals("balance")||type.equals("monthlyReport")) {
			ret=DBUtils.query(getQuery(type),"html");
		}
		return ret;
	}

	public static String update(String sql) {
		return DBUtils.update(sql);
	}

	public static String insert(String sql) {
		return DBUtils.insert(sql);
	}

	public static String getQuery(String type){
		String query="";
		if(type==null) query="";
		else if(type.equals("tran")) {
			query="select id,ffrom,fto,famount,if(cur=1,'',cur) as cur,issueDate,tradeDate,fcomments,ftype from v_tran3 ";
		}else if(type.equals("funds")) {
			query="select concat(name,'-',category) as label,id as value from funds order by category,name";
		}else if(type.equals("report")) {
			query="select name,category,substring(tradeDate,1,6) as month,sum(famount) from vf_tran3 group by name,category,month";
		}else if(type.equals("balance")) {
			query="select * from vf_balance order by category, famount desc";
		}else if(type.equals("monthlyReport")) {
			query="SELECT * FROM vf_tran3_m v where category='expense-expense' and cmonth='201205'";
		}return query;
	}
}
