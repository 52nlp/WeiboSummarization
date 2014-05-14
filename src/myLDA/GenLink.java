package myLDA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

public class GenLink {
	public DrawPattern pattern;
	public ClusterLoader loader;
	public WordGraph wg;
	
	public GenLink(DrawPattern p,int classNum){
		pattern=p;
		loader=new ClusterLoader(classNum);
		wg=new WordGraph();
	}
	
	public boolean genLink(String infile,String outfile){
		try{
			if(!loader.loadFromFile(infile))
				return false;
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			wg.clear();
			for(int i=0;i<pattern.rootsforward.size();i++){
//				System.out.println("\t"+i);
				Node presentNode=pattern.rootsforward.get(i);
				while(presentNode!=null){
					if(presentNode.firstSon!=null&&presentNode.sonNum>1){
						Vector<Vector<Node>> elem;
						elem=new Vector<Vector<Node>>();
						for(int j=0;j<loader.classNum;j++)
							elem.add(new Vector<Node>());
						Node pt=presentNode.firstSon;
//						System.out.println("loop begin");
						do{
							int index=loader.getCluterIndex(pt.content);
//							System.out.println(pt.content);
							if(index>=0&&index<loader.classNum){
								elem.get(index).add(pt);
							}
							pt=pt.rightBrother;
						}while(pt!=presentNode.firstSon);
//						System.out.println("begin");
						//generate similar words
						for(int j=0;j<loader.classNum;j++){
//							System.out.println(j+" "+elem.get(j).size());
							wg.addLinks(elem.get(j));
						}
//						System.out.println("end");
					}
//					System.out.println("begin outer loop");
					presentNode=presentNode.nextNode();
//					System.out.println("outer loop");
				}
			}
			for(int i=0;i<pattern.rootsbackward.size();i++){
				Node presentNode=pattern.rootsbackward.get(i);
				while(presentNode!=null){
					if(presentNode.firstSon!=null&&presentNode.sonNum>1){
						Vector<Vector<Node>> elem;
						elem=new Vector<Vector<Node>>();
						for(int j=0;j<loader.classNum;j++)
							elem.add(new Vector<Node>());
						Node pt=presentNode.firstSon;
						do{
							int index=loader.getCluterIndex(pt.content);
							if(index>=0&&index<loader.classNum){
								elem.get(index).add(pt);
							}
							pt=pt.rightBrother;
						}while(pt!=presentNode.firstSon);
						//generate similar words
						for(int j=0;j<loader.classNum;j++){
							wg.addLinks(elem.get(j));
						}
					}
					presentNode=presentNode.nextNode();
				}
			}
			wg.print(writer);
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading File "+outfile);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean sort(String unsorted,String sorted){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(unsorted),"UTF-8"));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(sorted),"UTF-8"));
			wg.sort(reader,writer);
			reader.close();
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+unsorted+"or File "+sorted);
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args){
		PhraseBasedPattern pattern=new PhraseBasedPattern(Config.keywords);
		System.out.println("established");
//		pattern.draw(Config.path+Config.wordSplitedFile);
//		System.out.println("tree set up");
//		pattern.genPhrase(Config.path+Config.phraseFile);
//		System.out.println("phrase builded");
		GenLink link=new GenLink(pattern,20);
//		link.genLink(Config.path+Config.cluterFile,Config.path+Config.synIndexFile);
//		System.out.println("start sorting");
		link.sort(Config.path+Config.synIndexFile,Config.path+Config.sortedSynIndexFile);
		System.out.println("all finished");
	}
}
