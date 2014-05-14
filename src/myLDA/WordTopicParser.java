package myLDA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class WordTopicParser {
	public String fileName;
	public int threshold;
	public Map<String,Integer> map;
	
	public WordTopicParser(String fileName,int threshold){
		this.fileName=fileName;
		this.threshold=threshold;
		map=new HashMap<String,Integer>();
	}
	
	public boolean execuate(){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName),"UTF-8"));
			String buffer;
			while((buffer=reader.readLine())!=null){
				String[] parts=buffer.split(" ");
				if(parts.length<3)
					continue;
				int topic=Integer.parseInt(parts[1]);
				int occurtime=Integer.parseInt(parts[2]);
				if(occurtime<threshold)
					continue;
				map.put(parts[0],topic);
			}
			reader.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+fileName);
			e.printStackTrace();
			return false;
		}
	}
	
	public int lookup(String word){
		if(map.containsKey(word))
			return map.get(word);
		else
			return -1;
	}
}
