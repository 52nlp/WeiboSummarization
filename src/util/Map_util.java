package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Map_util {

	// map排序，同时打印结果
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
	 
	 //降序
	 public static List<Map.Entry<String, Double>> sortMapByValueDec(Map maps) {   
	        List<Map.Entry<String, Double>> info = new ArrayList<Map.Entry<String, Double>>(maps.entrySet());   
	        Collections.sort(info, new Comparator<Map.Entry<String, Double>>() {   
	            public int compare(Map.Entry<String, Double> obj1,Map.Entry<String, Double> obj2) { 
	            	Double temp = obj2.getValue() - obj1.getValue();
	            	if(temp>0.00000001)
	            		return -1;
	            	else if(temp<-0.00000001)
	            		return 1;
	            	else
	            		return 0; 
	            }   
	        });    
	        return info;
	  }  
	 
	 public static void main(String[] args){
		 HashMap<String,Double> t = new HashMap<String,Double>();
		 t.put("a", 1.0);
		 t.put("b",0.5);
		 t.put("c", 1.5);
		 System.out.println(t);
		 List<Map.Entry<String, Double>> res = sortMapByValue(t);
		 for(int i = 0 ; i < res.size() ; i++){
			 System.out.println(res.get(i));
		 }
		 
	 }

}
