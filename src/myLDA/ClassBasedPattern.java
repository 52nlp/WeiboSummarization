package myLDA;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ClassBasedPattern extends PhraseBasedPattern{
	
	Map<String,WordGraph> map;
	
	public ClassBasedPattern(Vector<String> r){
		super(r);
		map=new HashMap<String,WordGraph>();
		map.put("n",new WordGraph());
		map.put("v",new WordGraph());
		map.put("a",new WordGraph());
		map.put("d",new WordGraph());
		map.put("q",new WordGraph());
		map.put("m",new WordGraph());
	}
	
	public void clear(){
		map.clear();
		map.put("n",new WordGraph());
		map.put("v",new WordGraph());
		map.put("a",new WordGraph());
		map.put("d",new WordGraph());
		map.put("q",new WordGraph());
		map.put("m",new WordGraph());
	}
	
	public void findSyn(Node n){
		if(n.firstSon==null)
			return;
		Vector<Node> noun=new Vector<Node>();
		Vector<Node> verb=new Vector<Node>();
		Vector<Node> adj=new Vector<Node>();
		Vector<Node> adv=new Vector<Node>();
		Vector<Node> qua=new Vector<Node>();
		Vector<Node> num=new Vector<Node>();
		//brother-brother
		Node presentNode=n.firstSon;
		do{
			String word=presentNode.content;
			if(word.contains("/n"))
				noun.add(presentNode);
			else if(word.contains("/v"))
				verb.add(presentNode);
			else if(word.contains("/a"))
				adj.add(presentNode);
			else if(word.contains("/d"))
				adv.add(presentNode);
			else if(word.contains("/q"))
				qua.add(presentNode);
			else if(word.contains("/m"))
				num.add(presentNode);
			presentNode=presentNode.rightBrother;
		}while(presentNode!=n.firstSon);
		map.get("n").addLinks(noun);
		map.get("v").addLinks(verb);
		map.get("a").addLinks(adj);
		map.get("d").addLinks(adv);
		map.get("q").addLinks(qua);
		map.get("m").addLinks(num);
	}
	
	public boolean print(String filename){
		try{
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename),"UTF-8"));
			Set<Map.Entry<String,WordGraph>> set=map.entrySet();
			for(Iterator<Map.Entry<String,WordGraph>> it=set.iterator();it.hasNext();){
				Map.Entry<String,WordGraph> entry=(Map.Entry<String,WordGraph>)it.next();
				writer.write("speech "+entry.getKey()+"\n");
				entry.getValue().print(writer);
				writer.write("===========================");
			}
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+filename);
			e.printStackTrace();
			return false;
		}
	}
	
	public void generateSyn(){
		clear();
		for(int i=0;i<rootsforward.size();i++){
			Node presentNode=rootsforward.get(i);
			while(presentNode!=null){
				findSyn(presentNode);
				presentNode=presentNode.nextNode();
			}
		}
		for(int i=0;i<rootsbackward.size();i++){
			Node presentNode=rootsbackward.get(i);
			while(presentNode!=null){
				findSyn(presentNode);
				presentNode=presentNode.nextNode();
			}
		}
	}
	
	public static void main(String[] args){
		ClassBasedPattern pattern=new ClassBasedPattern(Config.keywords);
		System.out.println("pattern finder established");
		pattern.draw(Config.path+Config.wordSplitedFile);
		System.out.println("find phrases");
		pattern.genPhrase(Config.path+Config.phraseFile);
		System.out.println("find similar word");
		pattern.generateSyn();
		System.out.println("print them to file");
		pattern.print(Config.path+Config.synIndexFile);
		System.out.println("all finished");
	}
	
}
