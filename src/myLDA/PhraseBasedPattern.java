package myLDA;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

public class PhraseBasedPattern extends DrawPattern {
	
	public PhraseBasedPattern(Vector<String> r){
		super(r);
	}
	
	private int pickoutSpeech(String s){
		if(s.contains("/n"))
			return 1;
		else if(s.contains("/v"))
			return 2;
		else if(s.contains("/a"))
			return 3;
		else if(s.contains("/d"))
			return 4;
		else if(s.contains("/q"))
			return 5;
		else if(s.contains("/m"))
			return 6;
		else
			return -1;
	}
	
	protected boolean findPhrase(BufferedWriter writer,Node n,boolean order) throws IOException{
		if(n.father==null)
			return false;
		int speech1=pickoutSpeech(n.father.content);
		int speech2=pickoutSpeech(n.content);
		if(speech1!=speech2||speech1<0||speech2<0)
			return false;
		double proportion=(double)n.number/(double)n.father.number;
		if(proportion>Config.phraseThresholdProValue&&n.number>Config.phraseThresholdValue){
			Node mergeNode=n.father;
//			System.out.println(n.number+" "+n.father.number);
//			System.out.println(n.content+" "+n.father.content);
			writer.write(n.content+" "+n.father.content+" -> ");
			n.merge(n.father,order);
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
			System.out.println("Error While Loading File "+filename);
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args){
		PhraseBasedPattern pattern=new PhraseBasedPattern(Config.keywords);
		System.out.println("established");
		pattern.draw(Config.path+Config.wordSplitedFile);
		System.out.println("print tree");
		pattern.printSize();
		pattern.printTree(Config.path+"tree.old");
		System.out.println("find phrase");
		pattern.genPhrase(Config.path+Config.phraseFile);
		System.out.println("print tree");
		pattern.printSize();
		pattern.printTree(Config.path+"tree.new");
		System.out.println("all finished");
	}
}
