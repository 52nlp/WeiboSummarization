package myLDA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class WordGraph {
	class CrossPoint{
		private int word1;
		private int word2;
		private int factor1;
		private int factor2;
		public double weight;
		public CrossPoint next;
		
		public CrossPoint(int i1,int i2,int f1,int f2){
			word1=i1;
			word2=i2;
			factor1=f1;
			factor2=f2;
			weight=0;
			next=null;
		}
		
		public void addWeight(int f1,int f2){
			factor1+=f1;
			factor2+=f2;
			weight+=0;
		}

	}

	Vector<String> words;
	Vector<CrossPoint> links;
	Vector<Integer> wordfrequency;

	public WordGraph(){
		words=new Vector<String>();
		links=new Vector<CrossPoint>();
		wordfrequency=new Vector<Integer>();
	}
	
	public void clear(){
		words=new Vector<String>();
		links=new Vector<CrossPoint>();
		wordfrequency=new Vector<Integer>();
	}

	public void calc(CrossPoint r){
		r.weight=(double)(r.factor1*r.factor2)/(double)(wordfrequency.get(r.word1)*wordfrequency.get(r.word2));
	}

	public void addLinks(Vector<Node> n){
		if(n.size()<2)
			return;
		for(int i=0;i<n.size();i++){
			String word1=n.get(i).content;
			int factor1=n.get(i).number;
			int pos1=words.indexOf(word1);
			if(pos1<0){
				pos1=words.size();
				words.add(word1);
				wordfrequency.add(factor1);
				links.add(new CrossPoint(0,0,0,0));
			}else{
				wordfrequency.set(pos1, wordfrequency.get(pos1)+factor1);
			}
			for(int j=i+1;j<n.size();j++){
//				System.out.println(i+" "+j+" "+n.size());
				String word2=n.get(j).content;
				int factor2=n.get(j).number;
				int pos2=words.indexOf(word2);
				if(pos2<0){
					pos2=words.size();
					words.add(word2);
					wordfrequency.add(0);
					links.add(new CrossPoint(0,0,0,0));
				}
				if(pos1==pos2)
					continue;
//				System.out.println("insert");
				if(pos1<pos2)
					insertLink(pos1,pos2,factor1,factor2);
				else
					insertLink(pos2,pos1,factor2,factor1);
//				System.out.println("insert end");
			}
		}
	}

	private void insertLink(int pos1,int pos2,int factor1,int factor2){
		CrossPoint presentNode=links.get(pos1);
		boolean hit=false;
		while(presentNode.next!=null){
			if (presentNode.next.word2>pos2){
				CrossPoint saved=presentNode.next;
				presentNode.next=new CrossPoint(pos1,pos2,factor1,factor2);
				presentNode.next.next=saved;
				hit=true;
				break;
			}else if(presentNode.next.word2==pos2){
				presentNode.next.addWeight(factor1,factor2);
				hit=true;
				break;
			}
			presentNode=presentNode.next;
		}
		if(hit==false)
			presentNode.next=new CrossPoint(pos1,pos2,factor1,factor2);
	}

	public void print(BufferedWriter writer) throws IOException{
		//String ret="";
		for(int i=0;i<links.size();i++){
			//System.out.println(links.size());
			CrossPoint presentNode=links.get(i).next;
			while(presentNode!=null){
				calc(presentNode);
				String word1=words.get(presentNode.word1);
				String word2=words.get(presentNode.word2);
				//ret+=word1+" "+word2+" "+presentNode.weight+"\n";
				if(presentNode.weight>Config.synThresholdConsineValue&&presentNode.factor1>=Config.synThresholdValue
						&&presentNode.factor2>=Config.synThresholdValue)	
					writer.write(word1+" "+word2+" "+presentNode.factor1+" "+presentNode.factor2+" "+presentNode.weight+"\n");
				presentNode=presentNode.next;
			}
		}
		//return ret;
	}
	
	protected class WordPair{
		public String word1;
		public String word2;
		public int frequency1;
		public int frequency2;
		public double relevance;
		
		public WordPair(String w1,String w2,int f1,int f2,double r){
			word1=w1;
			word2=w2;
			frequency1=f1;
			frequency2=f2;
			relevance=r;
		}
	}
	
	protected class WordPairCompare implements Comparator<Object>{
		//wordpair comparator
		@Override
		public int compare(Object o1, Object o2) {
			WordPair wp1=(WordPair)o1;
			WordPair wp2=(WordPair)o2;
			if(wp1.relevance>wp2.relevance)
				return -1;
			else if(wp1.relevance<wp2.relevance)
				return 1;
			else
				return 0;
		}
		
	}
	
	public void sort(BufferedReader reader,BufferedWriter writer) throws IOException{
		String buffer;
		WordPairCompare comparator=new WordPairCompare();
		List<WordPair> elem=new ArrayList<WordPair>();
		while((buffer=reader.readLine())!=null){
			String[] parts=buffer.split(" ");
			WordPair wp=new WordPair(parts[0],parts[1],Integer.parseInt(parts[2]),Integer.parseInt(parts[3]),Double.parseDouble(parts[4]));
			elem.add(wp);
		}
		Collections.sort(elem,comparator);
		for(int i=0;i<elem.size();i++){
			WordPair wp=elem.get(i);
			writer.write(wp.word1+"\t"+wp.word2+"\t"+wp.frequency1+"\t"+wp.frequency2+"\t"+wp.relevance+"\n");
		}
	}
}
