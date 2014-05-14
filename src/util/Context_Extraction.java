package util;
import java.io.UnsupportedEncodingException;
import java.util.*;

//import org.ictclas4j.segment.SegTag;

import Fpgrowth.ItemNode;
import ICTCLAS.I3S.AC.ICTCLAS50;
import ICTCLAS.I3S.AC.SegTagICTCLAS50;
import NodeClass.ConceptNode;

//抽取离散上下文Distributional Context，算法见论文Grouping Product Features Using Semi-Supervised Learning with Soft-Constraints

public class Context_Extraction{
	private ArrayList<ItemNode> items;
	private ArrayList<ConceptNode> concepts;
	public static ArrayList<String> sens, voc;
	public static Map map;
	private int winSize;
	final static int CHARSIZE = 30;
	final static int HASHSIZE = 1000019;
	
	public Context_Extraction(ArrayList<ItemNode> items,
			ArrayList<String> sens, int winSize) {
		this.items = items;
		this.sens = sens;
		this.winSize = winSize;
		concepts = new ArrayList<ConceptNode>();
		voc = new ArrayList<String>();
	}

	public ArrayList<ConceptNode> getConcepts(){
		GenContext();
		ArrayList<String> con_doc = new ArrayList<String>();
		for(int i = 0; i < concepts.size(); i++){
			con_doc.add(concepts.get(i).getDoc());
		}
		voc = GenVocabulary(con_doc);
		ComputeTF();
		return concepts;
	}
	class HashNode				//构建哈希表节点
	{
		String word;
		int number;
		HashNode next;
	}
	//计算离散上下文中每个单词的词频
	private void ComputeTF(){
		for(int i = 0; i < concepts.size(); i++){
			String[] words = concepts.get(i).getDoc().split(" ");
			concepts.get(i).setTxtWordSum(words.length);
			ArrayList<Integer> words_pos = new ArrayList<Integer>();
			ArrayList<Integer> words_num = new ArrayList<Integer>();
			map = new HashMap<Integer, Integer>();
			Iterator it = map.entrySet().iterator();
			for(int j = 0; j < words.length; j++){
				int pos = voc.indexOf(words[j]);
				if(!map.containsKey(pos)){
					map.put(pos, 1);
				}
				else{
					int num = (Integer) map.get(pos) + 1;
					map.put(pos, num);
				}
			}
//			for(int j = 0; j < words.length; j++){
//				System.out.println("@"+words[j]);
//				int pos = voc.indexOf(words[j]);
//				if(!words_pos.contains(pos)){
//					System.out.println("Not contain");
//					words_pos.add(pos);
//					words_num.add(1);
//				}
//				else{
//					System.out.println("Contains"+words_num.get(words_pos.indexOf(pos))+1);
//					words_num.set(words_pos.indexOf(pos), words_num.get(words_pos.indexOf(pos))+1);
//				}
//			}
			concepts.get(i).setVocSize(voc.size());
			concepts.get(i).setWordList(map);
		}
	}
	
	//所有概念的离散上下文中出现的词语，形成辞典
	private ArrayList<String> GenVocabulary(ArrayList<String> doc){
		ArrayList<String> dict = new ArrayList<String>();
		HashNode p;
		HashNode q;
		HashNode [] link = new HashNode[HASHSIZE];
		for(int it = 0; it < doc.size(); it++){
			String[] words = doc.get(it).split(" ");
			for(int i = 0; i< words.length; i++){
				words[i].trim();
				int e = elf_hash(words[i],HASHSIZE);
				q = link[e];
				while(q!=null)	{
					if(q.word.equals(words[i]))
						break;
					q = q.next;
				}
				if(q == null){
					p = new HashNode();
					p.word = words[i];
					p.next = link[e];
					link[e] = p;
				}
			}
		}
		int wordNum=0;
		for(int j = 0; j < HASHSIZE; j++){
			q = link[j];
			while(q != null){
				q.number = wordNum;
				//System.out.println(q.word);
				dict.add(q.word);
				wordNum++;
				q = q.next;
			}
		}
		return dict;
	}
	
	private int elf_hash(String str, int number){   
	    int hash = 0;   
	    long x =0l;   
	    char[] array = str.toCharArray();   
	    for(int i=0; i<array.length; i++){   
	        hash = (hash << 4) + array[i];   
	        if((x = (hash & 0xF0000000L)) != 0){   
	            hash ^= (x >> 24);   
	            hash &= ~x;   
	        }   
	    }   
	    int result = (hash & 0x7FFFFFFF) % number;   
	    return result;   
	}  
	//针对每个概念，产生相应的离散上下文，并存入概念节点的离散上下文域
	private void GenContext() {
	//	SegTag st = new SegTag(1);
		
	//	SegTagICTCLAS50 segtag = new SegTagICTCLAS50();
		
		/*
		try{
			
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
			String argu = ".";
			//初始化
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false)
			{
				System.out.println("Init Fail!");
				return;
			}
		*/	
			
		for (int i = 0; i < items.size(); i++) {	
			String item = items.get(i).content;
//			System.out.println("@item" + i + ": " + item);
			
			//String str = st.split(item+" ").getFinalResult();
			
			String tempitem = item + " ";
		//	byte nativeBytes[] = testICTCLAS50.ICTCLAS_ParagraphProcess(tempitem.getBytes("GB2312"), 0, 1);//分词处理
		//	String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
		//	String str = nativeStr;
			
			String str = SegTagICTCLAS50.segTag(tempitem);
			
			String[] item_words = str.split(" ");
			boolean isPhrase = false;
			if (item_words.length > 1){
			isPhrase = true;
			}
			String docs = "";
			for (int j = 0; j < sens.size(); j++) {
				String sen = sens.get(j);
				boolean flag = false;
				for(int ir = 0; ir <item_words.length; ir++){
					if(!sen.contains(item_words[ir])){
						flag = true;
						break;
					}
				}
				if (flag)
					continue;
				String[] sen_words = sen.split(" ");
				ArrayList<String> words_prune = new ArrayList<String>();
				ArrayList<Integer> pos = new ArrayList<Integer>();
				for (int tp = 0; tp < sen_words.length; tp++) {
					String check = sen_words[tp];
					if ((check.contains("/n") || check.contains("/a")
							|| check.contains("/m") || check.contains("/v") || check
							.contains("/s"))) {
						check = check.substring(0, check.indexOf("/"));
						words_prune.add(check);
						if (str.contains(check)) {
							pos.add(words_prune.size() - 1);
						}
					}
				}
				String doc = "";
				for (int k = 0; k < pos.size(); k++) {
					int ps = pos.get(k);
					int begin = ps - winSize, end = ps + winSize;
					if(begin < 0)
						begin = 0;
					if(end > words_prune.size()-1)
						end = words_prune.size()-1;
					for(int tp = begin; tp < ps; tp++){
						doc += words_prune.get(tp) + " ";
					}
					doc += words_prune.get(ps) + " ";
					for(int tp = ps+1; tp < end+1; tp++){
						doc += words_prune.get(tp) + " ";
					}
					
//					if (k == 0) {
//						if (ps - winSize < 0) {
//							for (int tp = 0; tp < ps; tp++)
//								doc += words_prune.get(tp) + " ";
//						}
//						else{
//							for(int tp = ps - winSize; tp < ps; tp++)
//								doc += words_prune.get(tp) + " ";
//						}
//						doc += words_prune.get(ps) + " ";
//						if(k == pos.size()-1){
//							if(ps + winSize > words_prune.size()-1){
//								for(int tp = ps+1; tp < words_prune.size(); tp++)
//									doc += words_prune.get(tp) + " ";
//							}
//							else{
//								for(int tp = ps+1; tp < ps+winSize+1; tp++)
//									doc += words_prune.get(tp) + " ";
//							}
//							break;
//						}
//						else{
//							if(ps + winSize > pos.get(k+1))
//						}
//						
//					}
//					else if(k != pos.size()-1)
				}
				docs += doc;
			}
			concepts.add(new ConceptNode(item, docs, isPhrase, items.get(i).support));
		}
	//	testICTCLAS50.ICTCLAS_Exit();
	//	}
	//	catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
	}
	
}
