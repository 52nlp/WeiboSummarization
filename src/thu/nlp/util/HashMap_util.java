package thu.nlp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class HashMap_util {

	public static boolean PRINT = true;

	
	public static void sortByValuePrint(HashMap<String,Integer> map){
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		//排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        return (o2.getValue() - o1.getValue()); 
//		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		});
		//排序后
		if(PRINT){
			for (int i = 0; i < infoIds.size(); i++) {
			    String id = infoIds.get(i).toString();
			    System.out.println(id);
			}
		}
	}
	
	//王玮测试用
	public static void sortByValuePrintConstrainst(HashMap<String,Integer> map,HashMap<String,Integer> constrain){
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		//排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        return (o2.getValue() - o1.getValue()); 
//		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		});
		//排序后
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    String key = infoIds.get(i).getKey();
		    int value = constrain.get(key);
		    if(value == 1 || infoIds.get(i).getValue() > 20)
		        System.out.println(id);
		}

	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 HashMap<String,Integer> t = new HashMap<String,Integer>();
		 t.put("a", 10);
		 t.put("b",5);
		 t.put("c", 15);
		 System.out.println(t);
		 sortByValuePrint(t);
		 System.out.println(t);
	}

}
