package naiveSummary;

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
import java.util.Vector;

import naiveSummary.Lib.*;

public class Summary {
	protected Vector<String> centralWords;
	protected Vector<Node> rootsforward;
	protected Vector<Node> rootsbackward;
	
	public Summary(Vector<String> r){
		centralWords=new Vector<String>();
		rootsforward=new Vector<Node>();
		rootsbackward=new Vector<Node>();
//		parser=new WordTopicParser(Config.topicIndexFile,Config.thresholdValue);
		for(int i=0;i<r.size();i++){
			centralWords.add(r.get(i));
			rootsforward.add(new Node(r.get(i)));
			rootsbackward.add(new Node(r.get(i)));
		}
	}
	
	//draw the pattern
	public boolean draw(String infile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(infile),"UTF-8"));
			String buffer;
			Node presentNode;
			//build the backward pattern tree and forward pattern tree
			while((buffer=reader.readLine())!=null){
				String[] words=buffer.split(" ");
				for(int i=0;i<words.length;i++){
					int pos=centralWords.indexOf(words[i]);
					if(pos>=0){
						String presentWord=words[i];
						presentNode=rootsbackward.get(pos);
						presentNode.number++;		//modified 
						for(int j=i-1;j>=0&&j>=i-Config.searchDeep;j--){
							presentWord=words[j];
							int quote=presentWord.indexOf("/w");
							if(quote>=0)
								break;
							Node exist=presentNode.search(presentWord);
							if(exist!=null){
								presentNode=exist;
								presentNode.number++;
							}else{
								presentNode=presentNode.addSon(presentWord);
							}
						}
						presentNode=rootsforward.get(pos);
						presentNode.number++;
						for(int j=i+1;j<words.length&&j<=i+Config.searchDeep;j++){
							presentWord=words[j];
							int quote=presentWord.indexOf("/w");
							if(quote>=0)
								break;
							Node exist=presentNode.search(presentWord);
							if(exist!=null){
								presentNode=exist;
								presentNode.number++;
							}else{
								presentNode=presentNode.addSon(presentWord);
							}
						}
					}
				}
			}
			//calculate the value of each node
			for(int i=0;i<rootsforward.size();i++){
				presentNode=rootsforward.get(i);
				//int number=0;
				while(presentNode!=null){
					//number++;
					presentNode.calc();
					//presentNode.print();
					presentNode=presentNode.nextNode();
				}
			}
			for(int i=0;i<rootsbackward.size();i++){
				presentNode=rootsbackward.get(i);
				//int number=0;
				while(presentNode!=null){
					//number++;
					presentNode.calc();
					//presentNode.print();
					presentNode=presentNode.nextNode();
				}
			}
			reader.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+infile);
			e.printStackTrace();
			return false;
		}
	}
	
	//print the words-tree
	public boolean printTree(String outfile){
		try{
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			for(int i=0;i<rootsbackward.size();i++){
				writer.write("The words before the word "+rootsbackward.get(i).content+" is:\n");
				Node presentNode;
				presentNode=rootsbackward.get(i);
				while(presentNode!=null){
					presentNode=presentNode.nextNode();
					if(presentNode!=null&&presentNode.depth<=Config.searchDeep&&presentNode.value>Config.valueThreshold){
						for(int j=1;j<presentNode.depth;j++)
							writer.write("\t");
						writer.write(presentNode.content+" "+presentNode.value+"\n");
					}
				}
				writer.write("==============================\n");
			}
			for(int i=0;i<rootsforward.size();i++){
				writer.write("The words after the word "+rootsforward.get(i).content+" is:\n");
				Node presentNode;
				presentNode=rootsforward.get(i);
				while(presentNode!=null){
					presentNode=presentNode.nextNode();
					if(presentNode!=null&&presentNode.depth<=Config.searchDeep&&presentNode.value>Config.valueThreshold){
						for(int j=1;j<presentNode.depth;j++)
							writer.write("\t");
						writer.write(presentNode.content+" "+presentNode.value+"\n");
					}
				}
				writer.write("==============================\n");
			}
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+outfile);
			e.printStackTrace();
			return false;
		}
	}
	
	//generate the summary
	public boolean printFrequentPattern(String outfile){
		try{
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			Vector<List<halfSummary>> leftall=new Vector<List<halfSummary>>();
			Vector<List<halfSummary>> rightall=new Vector<List<halfSummary>>();
			for(int i=0;i<rootsbackward.size();i++){
				leftall.add(new ArrayList<halfSummary>());
				Node presentNode=rootsbackward.get(i);
				while(presentNode!=null){
					presentNode=presentNode.nextNode();
					if(presentNode!=null&&presentNode.firstSon==null){
						String content="";
						double score=0;
						Node pt=presentNode;
						while(pt!=rootsbackward.get(i)){
							String extend=pt.content.contains("/")?pt.content.substring(0,pt.content.indexOf("/")):pt.content;
							content=content+extend;
							score=score+pt.value;
							pt=pt.father;
						}
						leftall.get(i).add(new halfSummary(content,score));
					}
				}
			}
			for(int i=0;i<rootsforward.size();i++){
				rightall.add(new ArrayList<halfSummary>());
				Node presentNode=rootsforward.get(i);
				while(presentNode!=null){
					presentNode=presentNode.nextNode();
					if(presentNode!=null&&presentNode.firstSon==null){
						String content="";
						double score=0;
						Node pt=presentNode;
						while(pt!=rootsforward.get(i)){
							String extend=pt.content.contains("/")?pt.content.substring(0,pt.content.indexOf("/")):pt.content;
							content=extend+content;
							score=score+pt.value;
							pt=pt.father;
						}
						rightall.get(i).add(new halfSummary(content,score));
					}
				}
			}
			halfSummaryComparator comparator=new halfSummaryComparator();
			for(int i=0;i<centralWords.size();i++){
				Collections.sort(leftall.get(i),comparator);
				Collections.sort(rightall.get(i),comparator);
				int leftsize=leftall.get(i).size();
				int rightsize=rightall.get(i).size();
				int leftpt=0,rightpt=0;
				String key=centralWords.get(i);
				while(leftpt<leftsize-1||rightpt<rightsize-1){
					String content=leftall.get(i).get(leftpt).sentence+key.substring(0,key.indexOf("/"))+rightall.get(i).get(rightpt).sentence;
					double lscore=leftall.get(i).get(leftpt).score;
					double rscore=rightall.get(i).get(rightpt).score;
					double score=lscore+rscore;
					writer.write(content+"\t"+score+"\n");
					if(leftpt<leftsize-1&&rightpt<rightsize-1){
						double ldelta=lscore-leftall.get(i).get(leftpt+1).score;
						double rdelta=rscore-rightall.get(i).get(rightpt+1).score;
						if(ldelta<rdelta)
							leftpt++;
						else
							rightpt++;
					}else{
						if(leftpt==leftsize-1)
							rightpt++;
						else
							leftpt++;
					}
				}
			}
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+outfile);
			e.printStackTrace();
			return false;
		}
	}
	
	//print the size of the tree
	public void printSize(){
		for(int i=0;i<rootsbackward.size();i++){
			System.out.println("the word before "+rootsbackward.get(i).content);
			for(int j=1;j<=Config.searchDeep&&j<=10;j++){
				int number=rootsbackward.get(i).size(j);
				System.out.println("level "+j+" "+number);
				if(number==0)
					break;
			}
			int total=rootsbackward.get(i).totalSize();
			System.out.println("total: "+total);
		}
		for(int i=0;i<rootsforward.size();i++){
			System.out.println("the word after "+rootsforward.get(i).content);
			for(int j=1;j<=Config.searchDeep&&j<=10;j++){
				int number=rootsforward.get(i).size(j);
				System.out.println("level "+j+" "+number);
				if(number==0)
					break;
			}
			int total=rootsbackward.get(i).totalSize();
			System.out.println("total: "+total);
		}
	}
	
	//for test
	public static void main(String[] args){
		Summary pattern=new Summary(Config.keywords);
		System.out.println("pattern finder established");
		pattern.draw(Config.root+Config.wordSplitedFile);
		System.out.println("draw the pattern tree");
		pattern.printTree(Config.path+Config.patternTreeFile);
		System.out.println("find frequency patterns");
		pattern.printFrequentPattern(Config.path+Config.singleSummaryFile);
		System.out.println("all finished");
	}
}
