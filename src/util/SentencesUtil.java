package util;



import java.util.ArrayList;
import java.util.HashMap;

import db.StaticDB;

import sentiment.dict.StopDict;

import Fpgrowth.FrequentItems;
import Fpgrowth.ItemNode;
import Fpgrowth.PhraseExtraction;
import ICTCLAS.I3S.AC.SegTagICTCLAS50;
import NodeClass.ConceptNode;


//句子的处理
//目前包括频繁项的提取,二维频繁项算是一个很重要的特征
//词频的统计
//王玮 2012-10-25
//分为三个部分,一个是微博的文本的预处理,一个是考虑词频,一个是考虑二维频繁项,
public class SentencesUtil {
	//是否停用词处理
	public static boolean STOPWORDPROCESS = true;
	//是否考虑词性
	public static boolean POSPROCESS = false;
	//频繁项的置信度
	public static double CONFIDENCE = 0.05;
	
	
	
	//微博的预处理
	public  static String handleWeiboString(String text){
		  /*
		   * to split
		   * (//@[^:]+:)   转发
		   * (回复@[^:]+:)   回复
		   * (转发此微博：)
		   * to replace
		   * (转发微博)
		   * (@[^ ]+ )|(@[^ ]+$)   @人
		   * (http://t\\.cn/[0-9a-zA-Z]+)   http://t.cn/Sb7CLH
		   * (\\[.+\\])   表情:[嘻嘻] [哈哈] [淘气]
		   */
		  String result="";
		  //System.out.println(text);
		  String[] temp=text.split("(//@[^:]+:)|(回复@[^:]+:)|(转发此微博：)|(转发此微博:)");
		  for(int i=0; i<temp.length; i++)
		   if(temp[i].length()>0){
		    //System.out.println(temp[i]);
		    result+=" "+temp[i].replaceAll("(\\#.+\\#)|(\\【.+\\】)|(@[^ ]+ )|(@[^ ]+$)|(http://t\\.cn/[0-9a-zA-Z]+)|(\\[.+\\])|(转发微博)|(&#[0-9]+)", " ");
		   }
		  //System.out.println(result);
		  return result;
		 }

	
	
	//统计词频,同时排序
	//粒度问题需要在考虑下
	public static HashMap<String,Integer> getWordFrequency(ArrayList<String> sentencesList){
		HashMap<String,Integer> res = new HashMap<String,Integer>();
		for(int i = 0 ; i < sentencesList.size() ; i++){
			//分词,分句
			String[] temp = SegTagICTCLAS50.segTag(sentencesList.get(i)).split(" ");
			for(int j = 0 ; j < temp.length ;j++){
				if(temp[j].indexOf("/")!=-1){
					//处理停用词
					if(STOPWORDPROCESS){
						if(StopDict.stopword.contains(temp[j].split("/")[0])){
							continue;
						}
					}
					//处理词性
					if(POSPROCESS){
						if(!POSConfig.hotWordPOSList.contains(temp[j].split("/")[1])){
							continue;
						}
					}
					if(temp[j].split("/")[1].equals("w")){
						continue;
					}
					if(res.containsKey(temp[j].split("/")[0])){
						res.put(temp[j].split("/")[0], res.get(temp[j].split("/")[0]) + 1);
					}
					else{
						res.put(temp[j].split("/")[0], 1);
					}
				}
				
			}		
		}	
		//统计完毕,排序
//		HashMap_util.sortByValuePrint(res);
		return res;
	}
	
	
	//统计频繁项,返回一维和二维
	public static ArrayList<String> getFrequentItem(ArrayList<String> sentenceList){
		//分词后的句子
		ArrayList<String> sens_tag = new ArrayList<String>();
		//短语
		ArrayList<String> phrase_org = new ArrayList<String>();
		for(int i = 0; i < sentenceList.size() ; i++){
			//原始句子分词,保存至sens_tag
			String str = SegTagICTCLAS50.segTag(sentenceList.get(i));
			if(STOPWORDPROCESS){
				String[] temp = str.split(" ");
				StringBuilder sb = new StringBuilder("");
				for(String s: temp){
					if(!StopDict.stopword.contains(s.split("/")[0])){
						sb.append(s+" ");
					}
					else{
						System.out.println(s);
					}
				}
				sens_tag.add(str.trim());
			}
			else{
				sens_tag.add(str);
			}			
		}
		//提取短语
		phrase_org = new PhraseExtraction(sens_tag).GetPhrase();	
		System.out.println("Phrase extraction finished!");
		
		FrequentItems fi = new FrequentItems();
		ArrayList<ItemNode> fi_org = fi.run();
		//调用FPTree算法，抽取一维、二维频繁项集
		ArrayList<ItemNode> fi_sort = fi.sort(fi_org);
		System.out.println("Frequent Items sorted!");
		
		//Concept_Extraction(ArrayList<ItemNode> items,ArrayList<String> sens, int dist, int app, int sup)
		Concept_Extraction ce = new Concept_Extraction(fi_sort, sens_tag, 2, 3, 3);
		//利用三次剪枝，将频繁项集提取出概念集合
		ArrayList<ItemNode> conceptCandidates = ce.getConceptCandidates();		
		System.out.println("Concept Extraction finished!");
		
		//概念提取
		Context_Extraction ctxte = new Context_Extraction(conceptCandidates,sens_tag, 3);
		ArrayList<ConceptNode> conceptList = ctxte.getConcepts();
		System.out.println("Distributional Context Extraction finished!");
		
		StringBuilder ConceptResult = new StringBuilder();
		for (int i = 0; i < conceptList.size(); i++) {
			//System.out.println(conceptList.get(i).getConcept());
			ConceptResult.append(conceptList.get(i).getConcept()+"\n");
		}
		ArrayList<String> res = new ArrayList<String>();
		String[] tempStr = ConceptResult.toString().split("\n");
		for(String s: tempStr){
			res.add(s);
		}
		return res;
//		System.out.println(ConceptResult);
		
		
	}
	
	
	
	//统计频繁项,返回二维
	public static ArrayList<String> getTwoDimensionFrequentItem(ArrayList<String> sentenceList){
		//分词后的句子
		ArrayList<String> sens_tag = new ArrayList<String>();
		//短语
		ArrayList<String> phrase_org = new ArrayList<String>();
		for(int i = 0; i < sentenceList.size() ; i++){
			//原始句子分词,保存至sens_tag
			String str = SegTagICTCLAS50.segTag(sentenceList.get(i));
			if(STOPWORDPROCESS){
				String[] temp = str.split(" ");
				StringBuilder sb = new StringBuilder("");
				for(String s: temp){
					if(!StopDict.stopword.contains(s.split("/")[0])){
						sb.append(s+" ");
					}
//					else{
//						System.out.println(s);
//					}
				}
				sens_tag.add(sb.toString().trim());
			}
			else{
				sens_tag.add(str);
			}			
		}
		//提取短语
		phrase_org = new PhraseExtraction(sens_tag).GetPhrase();	
		System.out.println("Phrase extraction finished!");
			
		FrequentItems fi = new FrequentItems();
		ArrayList<ItemNode> fi_org = fi.run();
		//调用FPTree算法，抽取一维、二维频繁项集
		ArrayList<ItemNode> fi_sort = fi.sort(fi_org);
		System.out.println("Frequent Items sorted!");
			
		//Concept_Extraction(ArrayList<ItemNode> items,ArrayList<String> sens, int dist, int app, int sup)
		Concept_Extraction ce = new Concept_Extraction(fi_sort, sens_tag, 2, 3, 3);
		//利用三次剪枝，将频繁项集提取出概念集合
		ArrayList<ItemNode> conceptCandidates = ce.getConceptCandidates();		
		System.out.println("Concept Extraction finished!");
			
		//概念提取
		Context_Extraction ctxte = new Context_Extraction(conceptCandidates,sens_tag, 3);
		ArrayList<ConceptNode> conceptList = ctxte.getConcepts();
		System.out.println("Distributional Context Extraction finished!");
			
		StringBuilder ConceptResult = new StringBuilder();
		for (int i = 0; i < conceptList.size(); i++) {
			//System.out.println(conceptList.get(i).getConcept());
			ConceptResult.append(conceptList.get(i).getConcept()+"\n");
	    }
		ArrayList<String> res = new ArrayList<String>();
		String[] tempStr = ConceptResult.toString().split("\n");
		for(String s: tempStr){
			if(s.split(" ").length > 1){
				res.add(s);
			}		
		}
		return res;
//		System.out.println(ConceptResult);		
			
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> sentenceList = new ArrayList<String>();
//		String sql = "SELECT * FROM sentences WHERE is_explicit = 1 AND (concept_id1 BETWEEN 1 AND 6) AND concept_id2 IS NULL";
		String sql = "SELECT * FROM comments where id < 1000";
		if(StaticDB.getDB() == null){
			System.err.println("未连接到数据库！");
			System.exit(0);
		}
		else{					
			if(StaticDB.getDB().createConn()){	
				StaticDB.getDB().query(sql);
				while(StaticDB.getDB().next()){
					if (!(StaticDB.getDB().getValue("content") == null) && StaticDB.getDB().getValue("content").length() > 0){
						sentenceList.add(StaticDB.getDB().getValue("content").toUpperCase());
					}
				}
			}
		}
		for(String s : sentenceList){
			System.out.println(s);
		}
//		statisticsWordFrequency(sentenceList);	
//		statisticsFrequentItem(sentenceList);
	}
		
	

}
