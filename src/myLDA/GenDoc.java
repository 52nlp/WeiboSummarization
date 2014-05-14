package myLDA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GenDoc {
	//Take off tags
	public boolean takeOffTags(String infile,String outfile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(infile),"UTF-8"));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
	 		//String regEx="\\'top\\'>(.+)</td";
			String buffer;
			while((buffer=reader.readLine())!=null){
				buffer=buffer.replaceAll(".{2}:http.+", "");
				buffer=buffer.replaceAll("http.+","");
				buffer=buffer.replaceAll("\\[(.+?)\\]","");
				buffer=buffer.replaceAll("@.+?\\s?","");
				buffer=buffer.replaceAll("[!@#%&\\*()\\?:;,\\'\\|\\+\\-~_]", "");
				if(buffer!=""&&buffer!=null)
					writer.write(buffer+"\n");
			}
			reader.close();
			writer.close();
			return true;
		}
		catch(Exception e){
			System.out.println("Error while loading file"+infile);
			e.getStackTrace();
			return false;
		}
	}
	
	public boolean Execuate(){
		if(!takeOffTags(Config.path+Config.srcFile,Config.path+Config.inputFile))
			return false;
		return true;
	}
}
