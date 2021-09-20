package css.servlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

	private static String displayResultSet(ResultSet rs) {
		try {
			StringBuilder sb=new StringBuilder("<table id=fdata><tr>");
			int c=rs.getMetaData().getColumnCount()+1;
			
			//append header
			for(int i=1;i<c;i++){
				sb.append("<th>"+rs.getMetaData().getColumnName(i)+"</th>");
			}
			sb.append("</tr>");
			
			//append data
			while(rs.next()){
				sb.append("<tr>");
				for(int i=1;i<c;i++){
					sb.append("<td>"+rs.getString(i)+"</td>");
				}	
				sb.append("</tr>");
			}

			sb.append("</table>");
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "error:"+e.getMessage();
		}
	}
	
	private static String JsonResultSet(ResultSet rs){
//if(true) return "[{\"label\":\"aa\",\"value\":\"AA\"},{\"label\":\"ab\",\"value\":\"AB\"}]";
		try {
			StringBuilder sb=new StringBuilder("[");
			int c=rs.getMetaData().getColumnCount()+1;
			
			//get header
			String[] header=new String[c];
			for(int i=1;i<c;i++){
				header[i]="\""+rs.getMetaData().getColumnLabel(i)+"\"";
				System.out.println(header[i]);
			}
			
			//append data
			while(rs.next()){
				sb.append("{");
				for(int i=1;i<c-1;i++){
					sb.append(header[i]+":\""+rs.getString(i)+"\",");
				}
				sb.append(header[c-1]+":\""+rs.getString(c-1)+"\"},");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]");
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "error:"+e.getMessage();
		}
		
	}
	
  private static void closeConn(Connection conn, Statement stmt){
    try {
      if(conn!=null) conn.close();
      if(stmt!=null) stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
	public static String query(String query,String type) {
		System.out.println("DBUtils:query:"+query);
		Connection conn = null;
		Statement stmt = null;
		try {
			conn=getConnection();
			stmt = conn.createStatement();
			ResultSet rs=stmt.executeQuery(query);
			if(type.equals("html"))	return displayResultSet(rs);//html
			else return JsonResultSet(rs);//json
		} catch (Exception e) {
			e.printStackTrace();
			return "error:"+e.getMessage();
		} finally {
      closeConn(conn,stmt);
		}
	}

	public static List<String[]> queryData(String sql){
		System.out.println("DBUtils:queryDate:"+sql);
		Connection conn = null;
		Statement stmt = null;
		List<String[]> ret=new ArrayList<String[]>();
		try {
			conn=getConnection();
			stmt = conn.createStatement();
			ResultSet rs=stmt.executeQuery(sql);
			
			int n=rs.getMetaData().getColumnCount();
			String[] headers=new String[n];
			for(int i=0;i<n;i++){
				headers[i]=rs.getMetaData().getColumnLabel(i+1);
			}ret.add(headers);
			
			while(rs.next()){
				String[] record=new String[n];
				for(int i=0;i<n;i++){
					record[i]=rs.getString(i+1);
				}ret.add(record);
			}return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeConn(conn,stmt);
		}
	}
	
	private static Connection getConnection() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance?user=css&password=css&useUnicode=true&characterEncoding=gbk");
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String update(String sql) {
		String[] strs=sql.split(";");
		List<String> sqls=genSqls(strs);
		Connection conn = null;
		Statement stmt = null;
		try {
			conn=getConnection();
			stmt = conn.createStatement();
			for(String s:sqls){
				stmt.addBatch(s);
				System.out.println(s);
			}
			int[] ret=stmt.executeBatch();
			return "updated ok:"+ret.length;
		} catch (Exception e) {
			e.printStackTrace();
			return "error:"+e.getMessage();
		} finally {
			closeConn(conn,stmt);
		}
	
	}

  private static List<String> genSqls(String[] strs){
    List<String> sqls=new ArrayList<String>();
		int n=strs.length;
		for(int  i=0;i<n;i++){
			if(strs[i].startsWith("ftype")){
				FUtils.ftype(strs[i],sqls);
			}else sqls.add(strs[i]);
		}
    return sqls;
  }

	public static String insert(String sql) {
		System.out.println("insert sql:"+sql);
		Connection conn = null;
		Statement stmt = null;
		try {
			conn=getConnection();
			stmt = conn.createStatement();
			stmt.execute(sql);
			return "insert ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "error:"+e.getMessage();
		} finally {
			closeConn(conn,stmt);
		}
	
	}
	
	public static String toHTML(List<String[]> data, boolean sort){
		StringBuilder sb=new StringBuilder("<table id=ttable>"+(sort?"<thead>":"")+"<tr>");
		String[] d=data.get(0);
		int c=d.length;
		for(int i=0;i<c;i++) sb.append("<th>"+d[i]+"</th>");
		sb.append("</tr>"+(sort?"</thead><tbody>":""));
		for(int i=1;i<data.size();i++){
			d=data.get(i);
			sb.append("<tr>");
			for(int j=0;j<c;j++) sb.append("<td>"+d[j]+"</td>");
			sb.append("</tr>");
		}
		sb.append((sort?"</tbody>":"")+"</table>");
		return sb.toString();
	}
	
	public static void main(String[] args){
		String str=query("select * from transaction","json");
		System.out.println(str);
	}
}
