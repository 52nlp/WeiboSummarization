package myLDA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ClusterLoader {
	public int classNum;
	public Map<String,Integer> map;
	
	public ClusterLoader(int number){
		classNum=number;
		map=new HashMap<String,Integer>();
	}

	public boolean loadFromFile(String filename){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(filename),"UTF-8"));
			String buffer;
			while((buffer=reader.readLine())!=null){
				String[] info=buffer.split(" ");
				String word=info[0];
				int number=Integer.parseInt(info[1]);
				if(number>=0&&number<classNum){
					map.put(word, number);
//					System.out.println("add an entry");
				}
			}
			reader.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+filename);
			e.printStackTrace();
			return false;
		}
	}

	public int getCluterIndex(String inputWord){
//		System.out.println(inputWord);
//		if(map==null)
//			System.out.println("map==null");
//		if(inputWord==null)
//			System.out.println("inputword==null");
		if(!map.containsKey(inputWord))
			return -1;
		return map.get(inputWord);
	}
}
