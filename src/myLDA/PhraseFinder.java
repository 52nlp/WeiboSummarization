package myLDA;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

public class PhraseFinder extends LinkBasedOnVec{
	
	public PhraseFinder(Vector<String> seeds,String vecFile){
		super(seeds,vecFile);
	}
	
	public boolean findPhrase(BufferedWriter writer,Node presentNode,boolean order) throws IOException{
		if(presentNode.father==null)
			return false;
		String word1=presentNode.content;
		String word2=presentNode.father.content;
		WordPair result=pairs.find(word1,word2);
		if(result!=null){
			result.setType(1);
			Node mergeNode=presentNode.father;
			writer.write(presentNode.content+" "+mergeNode.content+" -> ");
			presentNode.merge(mergeNode,order);
			writer.write(mergeNode.content+"\n");
			return true;
		}
		if(!Lib.canMerge(presentNode.father.content,presentNode.content, order))
			return false;
		double proportion=(double)presentNode.number/(double)presentNode.father.number;
		if(proportion>Config.phraseThresholdProValue&&presentNode.number>Config.phraseThresholdValue){
			Node mergeNode=presentNode.father;
			writer.write(presentNode.content+" "+mergeNode.content+" -> ");
			presentNode.merge(mergeNode,order);
			writer.write(mergeNode.content+"\n");
			return true;
		}
		return false;
	}
	
	public boolean genPhrase(String filename){
		try{
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename),"UTF-8"));
			for(int i=0;i<rootsforward.size();i++){
				Node presentNode=rootsforward.get(i);
				while(presentNode!=null){
					Node nextSaved=presentNode.nextNode();
					Node fatherSaved=presentNode.father;
					boolean result=findPhrase(writer,presentNode,true);
					if(result==false)
						presentNode=nextSaved;
					else
						presentNode=fatherSaved;
				}
			}
			for(int i=0;i<rootsbackward.size();i++){
				Node presentNode=rootsbackward.get(i);
				while(presentNode!=null){
					Node nextSaved=presentNode.nextNode();
					Node fatherSaved=presentNode.father;
					boolean result=findPhrase(writer,presentNode,false);
					if(result==false)
						presentNode=nextSaved;
					else
						presentNode=fatherSaved;
				}
			}
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading "+filename);
			e.printStackTrace();
			return false;
		}
	}
	
	public void mergeSyn(Node n){
		Node pt1=n.firstSon;
		Node pt2=n.firstSon;
		do{
			String word1=pt1.content;
			pt2=pt1.rightBrother;
			while(pt2!=n.firstSon){
				String word2=pt2.content;
				WordPair result=pairs.find(word1,word2);
				Node next=pt2.rightBrother;
				if(result!=null&&result.type==0){
					System.out.println(word1+" "+word2);
					pt1.merge(pt2,true);
				}
				pt2=next;
			}
			pt1=pt1.rightBrother;
		}while(pt1!=n.firstSon);
	}
	
	public void genSyn(){
		for(int i=0;i<rootsforward.size();i++){
			Node presentNode=rootsforward.get(i);
			while(presentNode!=null){
				if(presentNode.firstSon!=null)
					mergeSyn(presentNode);
				presentNode=presentNode.nextNode();
			}
		}
		for(int i=0;i<rootsbackward.size();i++){
			Node presentNode=rootsbackward.get(i);
			while(presentNode!=null){
				if(presentNode.firstSon!=null)
					mergeSyn(presentNode);
				presentNode=presentNode.nextNode();
			}
		}
	}
	
	public static void main(String args[]){
		PhraseFinder finder=new PhraseFinder(Config.keywords,Config.path+Config.wordVecFile);
		System.out.println("established");
		finder.draw(Config.path+Config.wordSplitedFile);
		System.out.println("tree set up");
		System.out.println("find link");
		finder.genLink(Config.path+Config.synBasedOnVecFile);
		System.out.println("generate phrases");
		finder.genPhrase(Config.path+Config.phraseFile);
		System.out.println("adjust..");
		finder.pairs.print(Config.path+Config.newSynBasedOnVecFile);
		System.out.println("merge syn");
		finder.genSyn();
		System.out.println("draw then pattern tree");
		finder.printTree(Config.path+Config.newPatternTree);
		System.out.println("frequency patterns");
		finder.printFrequentPattern(Config.path+Config.newSummary);
		System.out.println("all finished");
		return;
	}
}
