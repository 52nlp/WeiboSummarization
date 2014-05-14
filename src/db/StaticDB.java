package db;

public class StaticDB 
{
	public static DBAccess db;
	
	public static DBAccess getDB()
	{
		if(db == null)
		{
			db = new DBAccess();
		}
		return db; 
	}
	
	public static void closeDB()
	{
		db = null;
	}
}
