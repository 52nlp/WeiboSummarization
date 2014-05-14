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
import java.util.Vector;

import naiveSummary.Lib.halfSummary;
import naiveSummary.Lib.halfSummaryComparator;

public class BothWaySummary_v2 {
	protected Vector<String> centralWords;
	protected Vector<Node> roots;
	
	//construction function
	public BothWaySummary_v2(Vector<String> r){
		centralWords=new Vector<String>();
		roots=new Vector<Node>();
		for(int i=0;i<r.size();i++){
			centralWords.add(r.get(i));
			roots.add(new Node(r.get(i)));
		}
	}
	
	//draw the pattern
	public boolean draw(String infile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(infile),"UTF-8"));
			String buffer;
			//build the pattern tree with the principle of left first and then right
			while((buffer=reader.readLine())!=null){
				String[] words=buffer.split(" ");
				for(int i=0;i<words.length;i++){
					int pos=centralWords.indexOf(words[i]);
					if(pos>=0){
						Node presentNode=roots.get(pos);
						String presentWord=words[i];
						for(int j=1,q=0,left=0,right=0;;){
							if(left==1&&right==1){
								Node exist=presentNode.search("/end");
								if(exist!=null){
									presentNode=exist;
									presentNode.number++;
								}else{
									presentNode.addSon("/end");
								}
								break;
							}else{
								if(q==0){	//go to left
									if(left==1||i-j<0||words[i-j].indexOf("/w")>=0){
										left=1;
										presentWord="/blank";
									}else{
										presentWord=words[i-j];
									}
									q=1;
								}else{		//go to right
									if(right==1||i+j>=words.length||words[i+j].indexOf("/w")>=0){
										right=1;
										presentWord="/blank";
									}else{
										presentWord=words[i+j];
									}
									q=0;
									j++;
								}
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
			}
			//calculate the value of each node
			for(int i=0;i<roots.size();i++){
				Node presentNode=roots.get(i);
				while(presentNode!=null){
					int depthSaved=presentNode.depth;
					presentNode.depth=(presentNode.depth+1)/2;					//adjust the depth of each node
					presentNode.calc();
					presentNode.depth=depthSaved;
					if(presentNode.content.contains("/blank"))
						presentNode.value=0;
//					if(presentNode.content.indexOf("/")<=0)
//						presentNode.value=0;
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
	
	//print the frequent pattern
	/*
	public boolean printFrequentPattern(String outfile){
		try{
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			for(int i=0;i<roots.size();i++){
				Node presentNode=roots.get(i);
				ArrayList<halfSummary> all=new ArrayList<halfSummary>();
				while(presentNode!=null){
					presentNode=presentNode.nextNode();
					if(presentNode!=null&&presentNode.firstSon==null){
						Node pt=presentNode;
						String lcontent="";
						String rcontent="";
						double value=0;
						while(pt!=null){
							if(pt.depth%2==1){
								String extend=pt.content.contains("/")?pt.content.substring(0,pt.content.indexOf("/")):pt.content;
								lcontent=lcontent+extend;
							}else{
								String extend=pt.content.contains("/")?pt.content.substring(0,pt.content.indexOf("/")):pt.content;
								rcontent=extend+rcontent;
							}
							value+=pt.value;
							pt=pt.father;
						}
						all.add(new halfSummary(lcontent+rcontent,value));
					}
				}
				halfSummaryComparator comparator=new halfSummaryComparator();
				Collections.sort(all,comparator);
				for(halfSummary summary:all){
					writer.write(summary.sentence+"\t"+summary.score+"\n");
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
	*/
	
	public boolean printFrequentPattern(String outfile){
		try{
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			for(int i=0;i<roots.size();i++){
				Node presentNode=roots.get(i);
				ArrayList<halfSummary> all=new ArrayList<halfSummary>();
				while(presentNode!=null){
					presentNode=presentNode.nextNode();
					if(presentNode!=null&&presentNode.content.equals("/end")&&presentNode.firstSon==null){
						
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
}
