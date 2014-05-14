package myLDA;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class SynPair {
	class wordpairCompare implements Comparator<Object>{
		@Override
		public int compare(Object arg0, Object arg1) {
			WordPair wp1=(WordPair)arg0;
			WordPair wp2=(WordPair)arg1;
//			Double index1=wp1.similarity;
//			Double index2=wp2.similarity;
			Integer frequency1=wp1.frequency;
			Integer frequency2=wp2.frequency;
			return -frequency1.compareTo(frequency2);
		}
	}
	
	public Vector<String> words;
	public List<WordPair> pairs;
	
	public SynPair(){
		words=new Vector<String>();
		pairs=new ArrayList<WordPair>();
	}
	
	public void addPair(String word1,String word2,int frequency,double similarity){
		boolean newword=false;
		int index1=words.indexOf(word1);
		if(index1<0){
			newword=true;
			index1=words.size();
			words.add(word1);
		}
		int index2=words.indexOf(word2);
		if(index2<0){
			newword=true;
			index2=words.size();
			words.add(word2);
		}
		WordPair toadd=new WordPair(index1,index2,frequency,similarity);
		if(newword==false){
			for(int i=0;i<pairs.size();i++)
				if(pairs.get(i).equals(toadd)==true){
					pairs.get(i).add(frequency);
					return;
				}
		}
		pairs.add(toadd);
	}
	
	public WordPair find(String word1,String word2){
		int index1=words.indexOf(word1);
		if(index1<0)
			return null;
		int index2=words.indexOf(word2);
		if(index2<0)
			return null;
		for(int i=0;i<pairs.size();i++){
			int w1=pairs.get(i).word1;
			int w2=pairs.get(i).word2;
			if(w1==index1&&w2==index2)
				return pairs.get(i);
			if(w1==index2&&w2==index1)
				return pairs.get(i);
		}
		return null;
	}
	
	public void filter(int threshold){
		wordpairCompare comparator=new wordpairCompare();
		Collections.sort(pairs,comparator);
		for(int i=0;i<pairs.size();i++){
			int frequency=pairs.get(i).frequency;
			if(frequency<threshold){
				pairs.remove(i);
				i--;
			}
		}
	}
	
	public boolean print(String outfile){
		try{
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			for(int i=0;i<pairs.size();i++){
				int frequency=pairs.get(i).frequency;
				int index1=pairs.get(i).word1;
				int index2=pairs.get(i).word2;
				int type=pairs.get(i).type;
				double similarity=pairs.get(i).similarity;
				String word1=words.get(index1);
				String word2=words.get(index2);
				writer.write(word1+"\t"+word2+"\t"+similarity+"\t"+frequency+"\t"+type+"\n");
			}
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading "+outfile);
			e.printStackTrace();
			return false;
		}
	}
}
