package myLDA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class VecLoader {
	public int wordNum;
	public int dimension;
	public String[] words;
	public double[][] vec;
	public String filename;
	
	public VecLoader(String fn){
		dimension=-1;
		wordNum=-1;
		filename=fn;
	}
	
	public boolean loadFromFile(){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(filename),"UTF-8"));
			String buffer;
			String[] parts;
			buffer=reader.readLine();
			parts=buffer.split(" ");
			wordNum=Integer.parseInt(parts[0]);
			dimension=Integer.parseInt(parts[1]);
			words=new String[wordNum];
			vec=new double[wordNum][dimension];
			for(int i=0;i<wordNum;i++){
				buffer=reader.readLine();
				if(buffer==null){
					System.out.println("Content Error at Line "+i+" :No Content");
					reader.close();
					return false;
				}
				parts=buffer.split(" ");
				if(parts.length<dimension+1){
					System.out.println("Content Error at Line "+i+" :Not Enough Length");
					reader.close();
					return false;
				}
				words[i]=parts[0];
				for(int j=0;j<dimension;j++){
					vec[i][j]=Double.parseDouble(parts[j+1]);
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
	
	public double[] lookUp(String word){
		for(int i=0;i<wordNum;i++){
			if(words[i].equals(word))
				return vec[i];
		}
		return null;
	}
}
