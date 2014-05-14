package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import com.google.common.base.Joiner;


public class TFIDF {

	List<Map<String, Double>> textList;
	long wordCount;
	Map<String, Double> TF;
	Map<String, Double> DF;
	
	Map<String, Double> TFIDF;
	
	TFIDF(List<String> texts){
		textList = new ArrayList<Map<String, Double>>();
		TF = new HashMap<String, Double>();
		DF = new HashMap<String, Double>();
		TFIDF = new HashMap<String,Double>();
		
		for(String text: texts){
			String[] subStrs = text.split(" ");
			textList.add(new HashMap<String,Double>());
			for(String s: subStrs){
				if(s.length()!=0){
					wordCount++;
					if(textList.get(textList.size()-1).containsKey(s)){
						textList.get(textList.size()-1).put(s, textList.get(textList.size()-1).get(s)+1);
					}
					else{
						textList.get(textList.size()-1).put(s, 1.0);
					}
				}
			}
		}
		System.out.println("init map!");
		init();
	}
	
	
	void init(){
		for(Map<String, Double> map: textList){
			for(Entry<String, Double> entry: map.entrySet()){
				String key = entry.getKey();
				Double value = entry.getValue();
				if(TF.containsKey(key)){
					TF.put(key, TF.get(key)+value);
				}
				else{
					TF.put(key, value);
				}
				if(DF.containsKey(key)){
					DF.put(key, DF.get(key)+1);
				}
				else{
					DF.put(key, 1.0);
				}
			}
		}
		
		for(Entry<String, Double> entry: TF.entrySet()){
			String key = entry.getKey();
			Double value = entry.getValue();
			double tfValue = value/wordCount;
			Double dfValue = DF.get(key);
			double idfValue = Math.log(textList.size()/dfValue);
			TFIDF.put(key, tfValue*idfValue);
		}
	}
	
	public static List<Map.Entry<String, Double>> sortMapByValue(Map maps) {
        List<Map.Entry<String, Double>> info = new ArrayList<Map.Entry<String, Double>>(maps.entrySet());
        Collections.sort(info, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> obj1,Map.Entry<String, Double> obj2) {
            	Double temp = obj2.getValue() - obj1.getValue();
            	if(temp>0.00000001)
            		return 1;
            	else if(temp<-0.00000001)
            		return -1;
            	else
            		return 0;
            }
        });
        return info;
  }
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	
	}

}
