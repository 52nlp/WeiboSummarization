package thu.nlp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import sentiment.dict.StopDict;

import ICTCLAS.I3S.AC.SegTagICTCLAS50;

public class standfordTMInput {
	
	//是否处理停用词
	public static boolean dealStopword = true;
	//是否词性选择
	public static boolean selectPOS = true;
	
	public static void generateInputFile(ArrayList<String> inputSentenceList, String desFilePath){
		File des = new File(desFilePath);
		try {
			PrintWriter pw = new PrintWriter(des);
			for(int i = 0; i < inputSentenceList.size(); i++){
				pw.print(i + ",X,");
				String curSentence = inputSentenceList.get(i);
				String[] subStr = SegTagICTCLAS50.segTag(curSentence).split(" ");
				for(String s: subStr){
					if(s.split("/").length <2 ){
						continue;
					}
					else{
						String curSubStr = s.split("/")[0];
						String curSubStrPOS = s.split("/")[1];
						if(dealStopword){
							if(!StopDict.stopword.contains(curSubStr)){
								pw.print(curSubStr + " ");
							}
						}
						else if(selectPOS){
							if(POSCONFIG.POSFeatureWordList.contains(curSubStrPOS)){
								pw.print(curSubStr + " ");
							}
						}
						else{
							pw.print(curSubStr + " ");
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
}
