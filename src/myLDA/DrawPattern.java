package myLDA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

public class DrawPattern {
	//central words
	protected Vector<String> centralWords;
	//trees' base for both directions
	protected Vector<Node> rootsforward;
	protected Vector<Node> rootsbackward;
	
	//category parser
	protected WordTopicParser parser;

	//construction function
	public DrawPattern(Vector<String> r){
		centralWords=new Vector<String>();
		rootsforward=new Vector<Node>();
		rootsbackward=new Vector<Node>();
		parser=new WordTopicParser(Config.topicIndexFile,Config.thresholdValue);
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
//				System.out.println(i);
				writer.write("The words before the word "+rootsbackward.get(i).content+" is:\n");
				Node presentNode;
				presentNode=rootsbackward.get(i);
				while(presentNode!=null){
//					System.out.println(presentNode.content);
					presentNode=presentNode.nextNode();
//					if(presentNode==null)
//						break;
					if(presentNode!=null&&presentNode.depth<=Config.searchDeep&&presentNode.value>Config.valueThreshold){
						for(int j=1;j<presentNode.depth;j++)
							writer.write("\t");
						writer.write(presentNode.content+" "+presentNode.value+"\n");
					}
//					if(presentNode==null)
//						System.out.println("------");
//					System.out.println(presentNode.content);
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

	//print frequent pattern
	public boolean printFrequentPattern(String outfile){
		try{
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			for(int i=0;i<rootsbackward.size();i++){
				Node presentNode=rootsbackward.get(i);
				while(presentNode!=null){
					presentNode=presentNode.nextNode();
					if(presentNode!=null&&presentNode.depth+1>=Config.searchDeep&&presentNode.value>=Config.valueThreshold){
						Node pt=presentNode;
						while(pt!=null){
							writer.write(pt.content.substring(0,pt.content.indexOf("/")));
							pt=pt.father;
						}
						writer.write("\n");
					}
				}
			}
			for(int i=0;i<rootsforward.size();i++){
				Node presentNode=rootsforward.get(i);
				while(presentNode!=null){
					presentNode=presentNode.nextNode();
					if(presentNode!=null&&presentNode.depth>=Config.searchDeep-1&&presentNode.value>=Config.valueThreshold){
						boolean extend=false;
						Node pt=presentNode.firstSon;
						if(pt==null)
							extend=false;
						else if(pt.value>Config.valueThreshold)
							extend=true;
						else{
							pt=pt.rightBrother;
							while(pt!=presentNode.firstSon){
								if(pt.value>Config.valueThreshold){
									extend=true;
									break;
								}
								pt=pt.rightBrother;
							}
						}
						if(extend==true)
							continue;
						pt=presentNode;
						Vector<String> stack=new Vector<String>();
						while(pt!=null){
							stack.add(pt.content);
							pt=pt.father;
						}
						for(int j=stack.size()-1;j>=0;j--)
							writer.write(stack.get(j).substring(0,stack.get(j).indexOf("/")));
						writer.write("\n");
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
		DrawPattern pattern=new DrawPattern(Config.keywords);
		System.out.println("pattern finder established");
		pattern.draw(Config.path+Config.wordSplitedFile);
		System.out.println("draw the pattern tree");
		pattern.printTree(Config.path+Config.patternTreeFile);
		System.out.println("find frequency patterns");
		pattern.printFrequentPattern(Config.path+Config.patternFrequencyFile);
		System.out.println("all finished");
	}
}
