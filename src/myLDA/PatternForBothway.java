package myLDA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

public class PatternForBothway {
	//central words
	protected Vector<String> centralWords;
	//tree's base words
	protected Vector<Node> roots;
	
	//construction function
	public PatternForBothway(Vector<String> r){
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
						for(int j=1,q=0,left=0,right=0;j<=Config.searchDeep;){
							if(left==1&&right==1){
								break;
							}else{
								if(q==0){
									if(left==1||i-j<0||words[i-j].indexOf("/w")>=0){
										left=1;
										presentWord="/blank";
									}else{
										presentWord=words[i-j];
									}
									q=1;
								}else{
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
					presentNode.depth=(presentNode.depth+1)/2;					//adjust the depth of each node
					presentNode.calc();
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
	
	public static void main(String[] args){
		PatternForBothway pattern=new PatternForBothway(Config.keywords);
		System.out.println("pattern finder established");
		pattern.draw(Config.path+Config.wordSplitedFile);
		System.out.println("find frequency patterns");
		pattern.printFrequentPattern(Config.path+Config.patternFrequencyForBothwayFile);
		System.out.println("all finished");		
	}
	
}
