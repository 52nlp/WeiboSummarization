package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newHLDA.SegmentationTool;

import thu.nlp.util.POSCONFIG;

//计算语料库中的词的搭配情况

public class SentencesCompute {
	
	public static boolean ADD_POSTAG = false;
	public static double HOT_WORD_RATIO = 0.05;
	public static boolean SINGLE_WORD_DELETE = true;

	public ArrayList<String> sens;
	public ArrayList<String> segSens;
	public ArrayList<HashSet<String>> segSensWord;
	public HashMap<String,Double> wordFreq;
	public HashMap<String,Double> hotWordFreq;
	public HashMap<String,Double> abandonWordFreq;
	public HashMap<String,Double> hotAbandonWordFreq;
	public ArrayList<String> abandonWordSet;
	public ArrayList<String> wordSet;
	public HashMap<String,Double> wordCoOccur;
	
	public SentencesCompute(ArrayList<String> sens){
		this.sens = sens;
		segSens = new ArrayList<String>();
		for(int i = 0 ; i < sens.size(); i++){
			segSens.add(SegmentationTool.segTag(sens.get(i)));
		}
		//计算词频,wordFreq, abandonWordFreq
		wordFrequency();
		//词频较高的词
		hotWordFrequency();
	    getWord();
	    computeCoOccur(); 
	}
	
	//计算词频
	private void wordFrequency(){
		wordFreq = new HashMap<String,Double>();
		segSensWord = new ArrayList<HashSet<String>>();
		abandonWordFreq = new HashMap<String,Double>();
		for(int i = 0 ; i < this.segSens.size(); i++){
			String[] subStr = this.segSens.get(i).split(" ");
			HashSet<String> tempStr = new HashSet<String>();
			tempStr.clear();
			for(String s: subStr){
				if(s.split("/").length < 2){
					continue;
				}
				if(!POSCONFIG.POSFeatureWordList.contains(s.split("/")[1])){
					if(s.split("/")[0].length() >= 2){
						if(abandonWordFreq.containsKey(s.split("/")[0])){
							abandonWordFreq.put(s.split("/")[0], abandonWordFreq.get(s.split("/")[0])+1);
						}
						else{
							abandonWordFreq.put(s.split("/")[0], 1.0);
						}
					}		
					continue;
				}
				if(SINGLE_WORD_DELETE){
					if(s.split("/")[0].length() < 2){
						continue;
					}
				}
				String temp = s;
				//不考虑词性标记
				if(!ADD_POSTAG){
					temp = s.split("/")[0];
				}
				tempStr.add(temp);
			}
			segSensWord.add(tempStr);
//			System.out.println(tempStr);
			for(String temp : tempStr){
				//添加至hashmap中
				if(wordFreq.containsKey(temp)){
					wordFreq.put(temp, wordFreq.get(temp)+1.0);
				}
				else{
					wordFreq.put(temp, 1.0);
				}
			}
		}	
	}
	
	//热点词的词频，即需要大于一定数目
	private void hotWordFrequency(){
		hotWordFreq = new HashMap<String,Double>();
		hotAbandonWordFreq = new HashMap<String,Double>();
		abandonWordSet = new ArrayList<String>();
		double threshold = this.segSens.size() * HOT_WORD_RATIO;
		Iterator it = this.wordFreq.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,Double> entry = (Entry<String, Double>) it.next();
			String key = entry.getKey();
			Double value = entry.getValue();
			if( value > threshold){
				hotWordFreq.put(key, value/this.segSens.size());
			}
		}
		
		it = this.abandonWordFreq.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,Double> entry = (Entry<String, Double>) it.next();
			String key = entry.getKey();
			Double value = entry.getValue();
			if( value > threshold){
				hotAbandonWordFreq.put(key, value/this.segSens.size());
				abandonWordSet.add(key);
			}
		}
	}
	
	//词的集合
	private void getWord(){
		wordSet = new ArrayList<String>();
		Iterator it = this.hotWordFreq.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,Double> entry = (Entry<String, Double>) it.next();
			String key = entry.getKey();
			wordSet.add(key);
		}

	}
	
	//计算共现的情况
	public void computeCoOccur(){
		wordCoOccur = new HashMap<String,Double>();
		for(int i = 0; i < this.wordSet.size(); i++){
			for(int j =  i+1; j < this.wordSet.size(); j++){
				String str_i = this.wordSet.get(i);
				String str_j = this.wordSet.get(j);
				int count = 0;
				for(int k = 0 ; k < this.segSensWord.size(); k++){
					if(segSensWord.get(k).contains(str_i)&&segSensWord.get(k).contains(str_j)){
						count++;
					}
				}
				wordCoOccur.put(str_i + " " + str_j, (double) count);
			}
		}
	}
	
	public HashMap<String,Double> computePMI(){
		HashMap<String,Double> wordPMI = new HashMap<String,Double>();
		for(int i = 0; i < this.wordSet.size(); i++){
			for(int j =  i+1; j < this.wordSet.size(); j++){
				String str_i = this.wordSet.get(i);
				String str_j = this.wordSet.get(j);
				String str = str_i + " " + str_j;
				if(this.wordCoOccur.containsKey(str)){
					double count = this.wordCoOccur.get(str);		
					double value = Math.log(count/ (this.wordFreq.get(str_i) * this.wordFreq.get(str_j)));
					wordPMI.put(str, value);
				}
			}
		}
		return wordPMI;
	}
	
	public HashMap<String,Double> computeJaccard(){
		HashMap<String,Double> wordJaccard = new HashMap<String,Double>();
		for(int i = 0; i < this.wordSet.size(); i++){
			for(int j =  i+1; j < this.wordSet.size(); j++){
				String str_i = this.wordSet.get(i);
				String str_j = this.wordSet.get(j);
				String str = str_i + " " + str_j;
				if(this.wordCoOccur.containsKey(str)){
					double count = this.wordCoOccur.get(str);	
					double value = (double)count/(this.wordFreq.get(str_i) + this.wordFreq.get(str_j) - count);
					wordJaccard.put(str, value);
				}
			}
		}
		return wordJaccard;
	}
	
	
	public HashMap<String,Double> computeOverlap(){
		HashMap<String,Double> wordOverlap = new HashMap<String,Double>();
		for(int i = 0; i < this.wordSet.size(); i++){
			for(int j =  i+1; j < this.wordSet.size(); j++){
				String str_i = this.wordSet.get(i);
				String str_j = this.wordSet.get(j);
				String str = str_i + " " + str_j;
				if(this.wordCoOccur.containsKey(str)){
					double count = this.wordCoOccur.get(str);	
					double value = 0;
					if( this.wordFreq.get(str_i) < this.wordFreq.get(str_j) ){
						value = (double)count/this.wordFreq.get(str_i);
					}
					else{
						value = (double)count/this.wordFreq.get(str_j);
					}
					wordOverlap.put(str, value);
				}
			}
		}
		return wordOverlap;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
	}

}
