package tf_idf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tf_idf.Lib.*;

public class TF_IDFSummary {
	public wordlist list;
	public List<sentence> contents;
	
	public TF_IDFSummary(){
		list=new wordlist();
		contents=new ArrayList<sentence>();
	}
	
	public boolean genSummary(String infile,String outfile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(infile),"UTF-8"));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			String buffer;
			while((buffer=reader.readLine())!=null){
				String[] parts=buffer.split(" ");
				list.addSentence(parts);
				contents.add(new sentence(parts));
			}
			list.calc();
			for(sentence sen:contents){
				sen.calc(list);
			}
			Collections.sort(contents,new sentenceComparator());
			for(sentence sen:contents){
				for(int i=0;i<sen.contents.length;i++){
					String word=sen.contents[i];
					String rawword=word.contains("/")?word.substring(0,word.indexOf("/")):word;
					writer.write(rawword);
				}
				writer.write(sen.value+"\n");
			}
			reader.close();
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the file "+infile+" and "+outfile);
			e.printStackTrace();
			return false;
		}
	}
}
