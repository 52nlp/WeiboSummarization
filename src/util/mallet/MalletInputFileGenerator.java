package util.mallet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import newHLDA.Global;
import newHLDA.SegmentationTool;


import sentiment.dict.StopDict;
import thu.nlp.util.POSCONFIG;
public class MalletInputFileGenerator {
	
	public static int sensNum = 0;
	//是否处理停用词
	public static boolean dealStopword = false;
	//是否词性选择
	public static boolean selectPOS = false;
	//按词的数目过滤
	public static boolean numFilter = true;
	//比例
	public static double filterRatio = 0.005;
	//词频
	public static Map<String,Integer> wordNum = new HashMap<String,Integer>();
	public static Set<String> funtionWord = new HashSet<String> ();
	public static Set<String>  nonFuntionWord = new HashSet<String>();

	
	//统计词频
    public static void countWord(List<String> inputSentenceList){
    	Set<String> funtionWordCandidate = new HashSet<String>();
    	Set<String>  nonFuntionWordCandidate = new HashSet<String>();
    	sensNum = inputSentenceList.size();
//    	singleWord = new HashSet<String>();
    	for(String curSentence: inputSentenceList){
			String[] subStr = SegmentationTool.segTag(curSentence).split(" ");
			Set<String> tempSet = new HashSet<String>();
			//记录当前句子的词，对于一个词出现在句子中多次，按一次算
			for(String s: subStr){
				if(s.split("/").length <2 ){
					continue;
				}
				else{
					tempSet.add(s);
				}
			}
			for(String s: tempSet){
				String curSubStr = s.split("/")[0];
				String curSubStrPOS = s.split("/")[1];
				if(wordNum.containsKey(curSubStr)){
					wordNum.put(curSubStr, wordNum.get(curSubStr)+1);
				}
				else{
					wordNum.put(curSubStr, 1);
				}
				if(!POSCONFIG.POSFeatureWordList.contains(curSubStrPOS)){
					funtionWordCandidate.add(curSubStr);
				}
				else{
					nonFuntionWordCandidate.add(curSubStr);
				}
			}
    	}
//    	//计算funtionWord
//    	for(String s: funtionWordCandidate){
//    		if(wordNum.containsKey(s)){
//    			if(wordNum.get(s) > sensNum * Global.FUNTION_WORD_TOP_RATIO){
//    				funtionWord.add(s);
//    			}
//    		}
//    	}
//    	funtionWord.remove("救援");
//    	//计算nonFuntionWord
//    	for(String s: nonFuntionWordCandidate){
//    		if(wordNum.containsKey(s)){
//    			if(wordNum.get(s) < sensNum * Global.NONFUNTOPM_WORD_BOTTOM_RATIO){
//    				nonFuntionWord.add(s);
//    			}
//    		}
//    	}
    }
	
	public static void generateInputFile(List<String> inputSentenceList, String desFilePath){
		if(numFilter){
			countWord(inputSentenceList);	
		}
		File des = new File(desFilePath);
		try {
			PrintWriter pw = new PrintWriter(des);
			for(int i = 0; i < inputSentenceList.size(); i++){
				pw.print(i + " X ");
				String curSentence = inputSentenceList.get(i);
//				System.out.println(curSentence);
				String[] subStr = SegmentationTool.segTag(curSentence).split(" ");
				for(String s: subStr){
//					System.out.println(s);
					if(s.split("/").length <2 ){
						continue;
					}
					else{
						String curSubStr = s.split("/")[0];
						String curSubStrPOS = s.split("/")[1];
						if(curSubStr.equals(".")){
							continue;
						}
						if(curSubStr.equals("_")){
							continue;
						}
						if(dealStopword){
							if(selectPOS){
								if(!StopDict.stopword.contains(curSubStr) && POSCONFIG.POSFeatureWordList.contains(curSubStrPOS)){
									if(numFilter&&wordNum.containsKey(curSubStr)
											&&wordNum.get(curSubStr)> inputSentenceList.size() * filterRatio){
										pw.print(curSubStr + " ");
									}
									else{
										pw.print(curSubStr + " ");

									}
									
								}
							}
							else{
								if(!StopDict.stopword.contains(curSubStr)){
									if(numFilter&&wordNum.containsKey(curSubStr)
											&&wordNum.get(curSubStr)> inputSentenceList.size() * filterRatio){
										pw.print(curSubStr + " ");
										
									}	
									else{
										pw.print(curSubStr + " ");

									}
									}
							}
							
						}
						else if(selectPOS){
							if(dealStopword){
								if(!StopDict.stopword.contains(curSubStr) && POSCONFIG.POSFeatureWordList.contains(curSubStrPOS)){
									if(numFilter&&wordNum.containsKey(curSubStr)
											&&wordNum.get(curSubStr)> inputSentenceList.size() * filterRatio){
										pw.print(curSubStr + " ");
									}	
									else{
										pw.print(curSubStr + " ");

									}
									}
							}
							else{
								if(POSCONFIG.POSFeatureWordList.contains(curSubStrPOS)){
									if(numFilter&&wordNum.containsKey(curSubStr)
											&&wordNum.get(curSubStr)> inputSentenceList.size() * filterRatio){
										pw.print(curSubStr + " ");
									}								
									else{
										pw.print(curSubStr + " ");

									}
									}	
							}		
						}
						else{
							if(numFilter&&wordNum.containsKey(curSubStr)
									&&wordNum.get(curSubStr)> inputSentenceList.size() * filterRatio){
								pw.print(curSubStr + " ");
							}	
							else{
								pw.print(curSubStr + " ");

							}
						}
					}
				}
				pw.println();
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public static void generateTestFile(ArrayList<String> inputSentenceList, String desFilePath){
//		File des = new File(desFilePath);
//		try {
//			PrintWriter pw = new PrintWriter(des);
//			for(int i = 0; i < inputSentenceList.size(); i++){
//				pw.print((i+10000) + " X ");
//				String curSentence = inputSentenceList.get(i);
//				String[] subStr = segmentationTools.segTag(curSentence).split(" ");
//				for(String s: subStr){
//					if(s.split("/").length <2 ){
//						continue;
//					}
//					else{
//						String curSubStr = s.split("/")[0];
//						String curSubStrPOS = s.split("/")[1];
//						if(dealStopword){
//							if(!StopDict.stopword.contains(curSubStr)){
//								pw.print(curSubStr + " ");
//							}
//						}
//						else if(selectPOS){
//							if(POSCONFIG.POSFeatureWordList.contains(curSubStrPOS)){
//								pw.print(curSubStr + " ");
//							}
//						}
//						else{
//							pw.print(curSubStr + " ");
//						}
//					}
//				}
//				pw.println();
//			}
//			pw.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
