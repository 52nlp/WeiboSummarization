package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileWriter; 

public class File_util {
	
	public static String code = "UTF-8";
	
	//读文件
	public static List<String> readFile(String path){
		try{
			List<String> res = new ArrayList<String>();
			File src = new File(path);
			InputStreamReader isr = new InputStreamReader(new FileInputStream(src), code);
			BufferedReader bf = new BufferedReader(isr);
			String str = null;
			while((str = bf.readLine())!=null){
				if(str.toLowerCase().trim().length() >1){
					res.add(str.toLowerCase().trim());
				}
			}
			bf.close();
			isr.close();
			return res;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static List<String> readFile(File src){
		try{
			List<String> res = new ArrayList<String>();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(src), code);
			BufferedReader bf = new BufferedReader(isr);
			String str = null;
			while((str = bf.readLine())!=null){
				if(str.toLowerCase().trim().length() >1){
					res.add(str.toLowerCase().trim());
				}
				
			}
			bf.close();
			isr.close();
			return res;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;	
	}
	
	public static void write(List<String> sensList, String desPath){
		try{
			File des = new File(desPath);
			PrintWriter pw = new PrintWriter(des);
			for(String s: sensList){
				pw.println(s);
			}
			pw.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void write(String str, String desPath){
		try{
			File des = new File(desPath);
			FileWriter fileWriter = new FileWriter(des);
			PrintWriter pw = new PrintWriter(fileWriter);		
			pw.println(str);
			pw.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//讲HashMap排序之后写入文件
	public static void writeToFile(Map<String, Double> resMap, String path){
		try{
			PrintWriter pw = new PrintWriter(new File(path));
			List<Map.Entry<String, Double>> res = Map_util.sortMapByValue(resMap);
			for(int i = 0 ; i < res.size(); i++){
				pw.println(res.get(i).getKey() + " " + res.get(i).getValue());
			}
			pw.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	//读取存储在文件中的词频
	public static Map<String, Double> readMapFile(String path){
		try{
			Map<String,Double> resMap = new HashMap<String,Double>();
			File src = new File(path);
			BufferedReader bf = new BufferedReader(new FileReader(src));
			String str;
//			System.out.println("read file");
			while((str = bf.readLine())!=null){
				str = str.trim();
				if(str.split(" ").length > 1){
					String key = str.substring(0, str.indexOf(str.split(" ")[str.split(" ").length-1])).trim();
					Double value = Double.valueOf(str.split(" ")[str.split(" ").length-1]);
					if(resMap.containsKey(key)){
						resMap.put(key, resMap.get(key) + value);
					}
					else{
						resMap.put(key, value);
					}
				}
			}
			
			bf.close();
			return resMap;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	

	//读取存储在文件中的词频
	public static Map<String, Double> readMapFile(File src){
		try{
			Map<String,Double> resMap = new HashMap<String,Double>();
//			File src = new File(path);
			BufferedReader bf = new BufferedReader(new FileReader(src));
			String str;
//			System.out.println("read file");
			while((str = bf.readLine())!=null){
				if(str.split(" ").length > 1){
					String key = str.substring(0, str.indexOf(str.split(" ")[str.split(" ").length-1])).trim();
					Double value = Double.valueOf(str.split(" ")[str.split(" ").length-1]);
					if(resMap.containsKey(key)){
						resMap.put(key, resMap.get(key) + value);
					}
					else{
						resMap.put(key, value);
					}
				}
			}
			
			bf.close();
			return resMap;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}
