package db;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class DBAccess {
	
	public static String drv="";
	public static String url="";
	public static String usr="";
	public static String pwd="";
	public static String dbno="";
	private Connection conn = null;
	private Statement stm = null;
	private ResultSet rs = null;
	private CallableStatement cs = null;
	
	static
	{
		usr = "zwh";
		pwd = "iar";
	    url = "jdbc:mysql://166.111.138.25:3308/db_weibo_effect?characterEncoding=utf-8";
	    drv = "com.mysql.jdbc.Driver";
	}
	
	/** getter and setter of each viariable; */
	
	public static String getDrv() {
		return drv;
	}
	public static void setDrv(String drv) {
		DBAccess.drv = drv;
	}
	public static String getUrl() {
		return url;
	}
	public static void setUrl(String url) {
		DBAccess.url = url;
	}
	public static String getUsr() {
		return usr;
	}
	public static void setUsr(String usr) {
		DBAccess.usr = usr;
	}
	public static String getPwd() {
		return pwd;
	}
	public static void setPwd(String pwd) {
		DBAccess.pwd = pwd;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public Statement getStm() {
		return stm;
	}
	public void setStm(Statement stm) {
		this.stm = stm;
	}
	public ResultSet getRs() {
		return rs;
	}
	public void setRs(ResultSet rs) {
		this.rs = rs;
	}
	
	/** DB Access functions */
	
	public boolean createConn(String tableName)
	{
		boolean b = false;
		try{
			url="jdbc:mysql://166.111.138.25:3308/"+tableName+"?characterEncoding=utf-8";
			
			Class.forName(drv).newInstance();
			conn = DriverManager.getConnection(url,usr,pwd);
			//conn = DriverManager.getConnection("jdbc:sqlserver://192.168.1.101:1433;DatabaseName=cippus_web_db","cippus_web","StyleName");
			b = true;
		}
		catch(SQLException e){
			System.out.println("Exception occurs: SQLException");
			e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			System.out.println("ClassNotFoundException occurs: SQLException");
			e.printStackTrace();
		}
		catch(InstantiationException e){
			System.out.println("InstantiationException occurs: SQLException");
			e.printStackTrace();
		}
		catch(IllegalAccessException e){
			System.out.println("IllegalAccessException occurs: SQLException");
			e.printStackTrace();
		}
		return b;
	}
	
	public boolean createConn()
	{
		boolean b = false;
		try{
			Class.forName(drv).newInstance();
			conn = DriverManager.getConnection(url,usr,pwd);
			//conn = DriverManager.getConnection("jdbc:sqlserver://192.168.1.101:1433;DatabaseName=cippus_web_db","cippus_web","StyleName");
			b = true;
		}
		catch(SQLException e){
			System.out.println("Exception occurs: SQLException");
			e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			System.out.println("ClassNotFoundException occurs: SQLException");
			e.printStackTrace();
		}
		catch(InstantiationException e){
			System.out.println("InstantiationException occurs: SQLException");
			e.printStackTrace();
		}
		catch(IllegalAccessException e){
			System.out.println("IllegalAccessException occurs: SQLException");
			e.printStackTrace();
		}
		return b;
	}

	public boolean createStoredProc(String sql)
	{
		try{
			cs = conn.prepareCall(sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean closeCS()
	{
		try{
			cs.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean setStringCS(int i,String str)
	{
		try{
			cs.setString(i, str);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean setDateCS(int i,Date str)
	{
		try{
			cs.setDate(i, str);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean setIntCS(int i,int temp)
	{
		try{
			cs.setInt(i, temp);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean registerOutParameter(int i, int type)
	{
		try{
			cs.registerOutParameter(i, type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean csexcute()
	{
		try{
			if(cs.execute())
			{
				rs = cs.getResultSet();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public int getInt(int i)
	{	
		int o = -1;
		try{
			o = cs.getInt(i);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return o;
	}
	
	public boolean update(String sql)
	{
		boolean b = false;
		try{
			stm = conn.createStatement();
			stm.execute(sql);
			b = true;
		}
		catch(Exception e)
		{
			System.out.println("Exception in update() function");
			e.printStackTrace();
		}
		return b;
	}
	
	public void query(String sql)
	{
		try{
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in query() function\n"+sql);
			e.printStackTrace();
		}
	}
	
	public boolean next()
	{
		boolean b = false;
		try
		{
			if(rs.next())
				b = true;
		}
		catch(Exception e)
		{
			System.out.println("Exception in next() function");
			e.printStackTrace();
		}
		return b;
	}
	
	public String getValue(String field)
	{
		String value = null;
		try
		{
			if(rs != null)
			value = rs.getString(field);
		}
		catch(Exception e)
		{
			System.out.println("Exception in getValue() function");
			e.printStackTrace();
		}
		return value;
	}
	
	public void closeRs()
	{
		try{
			if(rs != null)
				rs.close();
		}
		catch(SQLException e)
		{
			System.out.println("Exception in closeResultSet() function");
			e.printStackTrace();
		}
	}
	
	public void closeStm()
	{
		try{
			if(stm != null)
				stm.close();
		}
		catch(SQLException e)
		{
			System.out.println("Exception in closeStm() function");
			e.printStackTrace();
		}
	}
	
	public void closeConn()
	{
		try{
			if(conn != null)
				conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("Exception in closeConn() function");
			e.printStackTrace();
		}
	}
	public static String getDbno() {
		return dbno;
	}
	public static void setDbno(String dbno) {
		DBAccess.dbno = dbno;
	}
	public CallableStatement getCs() {
		return cs;
	}
	public void setCs(CallableStatement cs) {
		this.cs = cs;
	}
}
