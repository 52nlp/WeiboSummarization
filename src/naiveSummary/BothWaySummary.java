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

public class BothWaySummary {
		//central words
		protected Vector<String> centralWords;
		//tree's base words
		protected Vector<Node> roots;
		//sentences
		protected Vector<String[]> sentences;
		
		//construction function
		public BothWaySummary(Vector<String> r){
			centralWords=new Vector<String>();
			roots=new Vector<Node>();
			sentences=new Vector<String[]>();
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
					sentences.add(words);
					for(int i=0;i<words.length;i++){
						int pos=centralWords.indexOf(words[i]);
						if(pos>=0){
							Node presentNode=roots.get(pos);
							String presentWord=words[i];
							for(int j=1,q=0,left=0,right=0;;){
								if(left==1&&right==1){
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
						if(presentNode.content.equals("/blank"))
							presentNode.value=0;
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
					while(presentNode!=null){
						presentNode=presentNode.nextNode();
						if(presentNode!=null&&presentNode.depth>=Config.searchDeep-1&&presentNode.value>Config.valueThreshold){
							boolean extend=false;
							Node pt=presentNode.firstSon;
							if(pt!=null){
								do{
									if(pt.value>Config.valueThreshold){
										extend=true;
										break;
									}
									pt=pt.rightBrother;
								}while(pt!=presentNode.firstSon);
							}
							if(extend==true)
								continue;
							pt=presentNode;
							Vector<String> pattern=new Vector<String>();
							double value=0;
							while(pt!=null){
								pattern.add(pt.content.substring(0,pt.content.indexOf("/")));
								if(pt.father!=null)
									value+=pt.value;
								pt=pt.father;
							}
							String word="";
							for(int j=1;j<=pattern.size();j++){
								if(j%2==0){
									word=pattern.get(pattern.size()-j)+word;
								}else{
									word=word+pattern.get(pattern.size()-j);
								}
							}
							writer.write(word+" "+value+"\n");
						}
					}
				}
				writer.close();
				return true;
			}catch(IOException e){
				System.out.println("Error While Writing the File "+outfile);
				e.printStackTrace();
				return false;
			}
		}
		*/
		
		public boolean printFrequentPattern(String outfile){
			try{
				BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outfile),"utf-8"));
				Vector<List<sortObject>> sort=new Vector<List<sortObject>>();
				double avgLen=0;
				for(int i=0;i<sentences.size();i++)
					avgLen+=sentences.get(i).length;
				avgLen/=sentences.size();
				avgLen=10;
				System.out.println(avgLen);
				for(int i=0;i<centralWords.size();i++)
					sort.add(new ArrayList<sortObject>());
				for(int i=0;i<sentences.size();i++){
					double[] maxScore=new double[centralWords.size()];
					for(int j=0;j<centralWords.size();j++)
						maxScore[j]=0;
					for(int j=0;j<sentences.get(i).length;j++){
//						System.out.println(j);
						String presentWord=sentences.get(i)[j];
						int index=centralWords.indexOf(presentWord);
						if(index>=0){
							Node presentNode=roots.get(index);
							double score=0;
							for(int l=1,r=1,s=0,lc=0,rc=0;;){
//								System.out.println(presentWord);
								//left first.
								if(s==0){
									//left
									s=1;
									if(j-l<0||lc==1){
										lc=1;
										presentNode=presentNode.search("/blank");
									}else{
										presentWord=sentences.get(i)[j-l];
										if(presentWord.indexOf("/w")>=0){
											lc=1;
											presentNode=presentNode.search("/blank");
										}else{
											presentNode=presentNode.search(presentWord);
											score+=presentNode.value;
										}
									}
									l++;
								}else{
									//right
									s=0;
									if(j+r>=sentences.get(i).length||rc==1){
										rc=1;
										presentNode=presentNode.search("/blank");
									}else{
										presentWord=sentences.get(i)[j+r];
										if(presentWord.indexOf("/w")>=0){
											rc=1;
											presentNode=presentNode.search("/blank");
										}else{
											presentNode=presentNode.search(presentWord);
											score+=presentNode.value;
										}
									}
									r++;
								}
								if(lc==1&&rc==1)
									break;
							}
							if(score>maxScore[index]){
								maxScore[index]=score;
							}
						}
					}
					for(int j=0;j<centralWords.size();j++){
						if(sentences.get(i).length>avgLen)
							maxScore[j]/=sentences.get(i).length;
						else
							maxScore[j]/=avgLen;
//						System.out.println(maxScore[j]);
						sort.get(j).add(new sortObject(sentences.get(i),maxScore[j]));
					}
				}
				sortObjectComparator comparator=new sortObjectComparator();
				for(int i=0;i<centralWords.size();i++){
					writer.write("============================"+centralWords.get(i)+"\n");
					Collections.sort(sort.get(i),comparator);
					for(int j=0;j<10&&j<sort.get(i).size();j++){
						String[] contents=(String[])sort.get(i).get(j).obj;
						for(int w=0;w<contents.length;w++)
							writer.write(contents[w]+' ');
						writer.write("\t"+sort.get(i).get(j).score+"\n");
					}
				}
				writer.close();
				return true;
			}catch(IOException e){
				System.out.println("Error While Loading "+outfile);
				e.printStackTrace();
				return false;
			}
		}
		
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
		}*/
		
		//for test
		public static void main(String[] args){
			BothWaySummary pattern=new BothWaySummary(Config.keywords);
			System.out.println("pattern finder established");
			pattern.draw(Config.root+Config.wordSplitedFile);
			System.out.println("find frequency patterns");
			pattern.printFrequentPattern(Config.path+Config.doubleSummaryFile);
			System.out.println("all finished");		
		}
}
