package myLDA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class TopicWordParser {
	public String fileName;
	public int threshold;
	public Vector<Vector<Word2Index>> list;
	
	public TopicWordParser(String fileName,int threshold){
		this.fileName=fileName;
		this.threshold=threshold;
		list=new Vector<Vector<Word2Index>>();
	}
	
	public boolean execuate(){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName),"UTF-8"));
			String buffer;
			while((buffer=reader.readLine())!=null){
				String[] words=buffer.split(" ");
				if(words.length<=3)
					continue;
				int pos=list.size();
				list.add(new Vector<Word2Index>());
				for(int i=3;i<words.length;i++){
					String[] parts=words[i].split(":");
					if(parts.length<2)
						continue;
					int index=Integer.parseInt(parts[1]);
					if(index<threshold)
						break;
					list.get(pos).add(new Word2Index(parts[0],index));
				}
			}
			reader.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+fileName);
			e.printStackTrace();
			return false;
		}
	}
}
