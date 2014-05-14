package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class annotatedRead {

	static HashMap<String,HashSet<String>> res;
	
	static{
		res = new HashMap<String,HashSet<String>>();
		LoadText();
	}
	
	static void LoadText(){
		File src = new File("corpus/annotated.txt");
		try {
			BufferedReader bf = new BufferedReader(new FileReader(src));
			String str = null;
			while((str=bf.readLine())!=null){
				String[] temp = str.trim().split(" ");
				if(temp.length!=3){
					System.out.println(str);
				}
				else{
					if(temp[1].equals("无")||temp[2].equals("无")){
						continue;
					}
					
					String[] keyWords = temp[2].split("/");
//					if(keyWords.length>1){
//						System.out.println(temp[2]);
//					}
					if(res.containsKey(temp[1])){
						for(String s: keyWords){
							res.get(temp[1]).add(s);
						}
					}
					else{
						res.put(temp[1], new HashSet<String>());
						for(String s: keyWords){
							res.get(temp[1]).add(s);
						}
					}
					
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
//		System.out.println(res);
		Iterator it = res.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			HashSet<String> value = (HashSet<String>) entry.getValue();
			System.out.println(key);
//			System.out.println(key + "  :  " + value);
		}
	}
	
}
