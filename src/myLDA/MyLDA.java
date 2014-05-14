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

import cc.mallet.types.InstanceList;

import util.File_util;
import util.mallet.MalletInput;
import LDAVariety.ASLDA;
//import LDAVariety.LDABase;

public class MyLDA extends ASLDA{	
	//comparator
	protected class word2indexCompare implements Comparator<Object>{
		@Override
		public int compare(Object o1, Object o2) {
			Word2Index word1=(Word2Index)o1;
			Word2Index word2=(Word2Index)o2;
			return word2.index.compareTo(word1.index);
		}
	}
		
	public MyLDA(int numberOfTopics){
		super(numberOfTopics);
	}
	
	public MyLDA(int numberOfTopics,double alpha,double beta){
		super(numberOfTopics,alpha,beta);
	}
	
	//generate word-topic-index files
	public boolean genWordTopicIndex(String outfile1,String outfile2){
		try{
			BufferedWriter writer1=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile1),"UTF-8"));
			BufferedWriter writer2=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile2),"UTF-8"));
			Vector<List<Word2Index>> topic2word=new Vector<List<Word2Index>>();
			for(int topic=0;topic<numTopics;topic++)
				topic2word.add(new ArrayList<Word2Index>());
			for(int v=0;v<alphabet.size();v++){
				int chosen=-1;
				double maxvalue=-1;
				for(int topic=0;topic<numTopics;topic++){
					if(phi[topic][v]>maxvalue){
						maxvalue=phi[topic][v];
						chosen=topic;
					}
				}
				String word=(String)alphabet.lookupObject(v);
				int occurtime=typeTopicCounts[v][chosen];
				writer1.write(word+" "+chosen+" "+occurtime+"\n");
				topic2word.get(chosen).add(new Word2Index(word,occurtime));
			}
			word2indexCompare comparator=new word2indexCompare();
			for(int i=0;i<numTopics;i++){
				Collections.sort(topic2word.get(i),comparator);
				writer2.write("topic "+i+" : ");
				for(int j=0;j<topic2word.get(i).size();j++){
					Word2Index item=(Word2Index)topic2word.get(i).get(j);
					writer2.write(item.word+":"+item.index+" ");
				}
				writer2.write("\n");
			}
			writer1.close();
			writer2.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+outfile1+" and "+outfile2);
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String args[]) throws IOException{
		//pre process to get the input file
		PreProcess process=new PreProcess();
		if(!process.execuate()){
			System.out.println("Load File Error!");
			return;
		}
		
		//ASLDA process
		List<String> sensList=File_util.readFile(Config.path+Config.destFile);
		InstanceList training=MalletInput.getInstanceList(sensList);
		MyLDA lda=new MyLDA(Config.topicNum,1,0.1);
		lda.addInstances(training);
		lda.sample(Config.iterationTimes);
		
		//find out topic of each word
		lda.genWordTopicIndex(Config.path+Config.wordIndexFile,Config.path+Config.topicIndexFile);
	}

	/*
	public static void main(String args[]) throws IOException{
		String text="E:/test.txt";
		List<String> sensList=File_util.readFile(text);
		InstanceList training=MalletInput.getInstanceList(sensList);
		int topicNum=2;
		LDABase lda=new MyLDA(topicNum,0.1,0.1);
		lda.addInstances(training);
		lda.sample(20);
		//System.out.println("finished\n");
	}
	*/

}
