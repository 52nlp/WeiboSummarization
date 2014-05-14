package demo;

import db.StaticDB;

public class SqlDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(StaticDB.getDB().createConn()){
			String sql = "select * from comments where id < 100";
			StaticDB.getDB().query(sql);
			while(StaticDB.getDB().next()){
				String id = StaticDB.getDB().getValue("id");
				String content = StaticDB.getDB().getValue("content");
				System.out.println(content);
			}
		}
	}

}
