package css.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class FUtils {
	public static void print(String str,HttpServletResponse resp,OutputStream out){
		try {
			resp.setStatus(HttpServletResponse.SC_OK);
			out.write(str.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void ftype(String string, List<String> sqls) {
		try {
			String[] ss=string.split(":");//ftype:value:id:tradeDate:famount;
			sqls.add("delete from subtran where id="+ss[2]);
			if(!ss[1].startsWith("m")) return;
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date d=sdf.parse(ss[3]);
			Calendar c=Calendar.getInstance();
			
			int start=Integer.parseInt(ss[1].split(",")[0].substring(1));
			int n=Integer.parseInt(ss[1].split(",")[1]);
			System.out.println("start:"+start+";n:"+n);
			Double amount=Double.parseDouble(ss[4])/n;
			c.setTime(d);c.add(Calendar.MONTH, start);
			
			for(int i=0;i<n;i++){
				String strd=sdf.format(c.getTime());
				sqls.add("insert into subtran(id,tradeDate,famount) values(" + 
						ss[2]+",'"+strd+"',"+amount +
						")");
				c.add(Calendar.MONTH, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		List<String> l =new ArrayList<String>();
		ftype("ftype:m2,3:88:20120203:56",l);
		for(String s:l) System.out.println(s);
	}
}
