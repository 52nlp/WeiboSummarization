package newHLDA.constraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/*
 * 对于约束的情况下，判断一个instance的情况
 */
public class InstanceResult {
	
	//单主题约束
	public static boolean singleTopicConstraint = false;
	
	//每一个结果包含的主题的数目
	public ArrayList<HashSet<String>> resList;
	public HashMap<String,Double> resTopicNum;
	//对应的主题表
	public ArrayList<Integer> topicSet;
	
	public InstanceResult(ArrayList<HashSet<String>> resList){
		this.resList = resList;
		resTopicNum = new HashMap<String,Double>();
		topicSet = new ArrayList<Integer>();
		int max = -1;
		for(int i = 0 ; i < resList.size(); i++){
			resTopicNum.put(String.valueOf(i),Double.valueOf(resList.get(i).size()));
			if(resList.get(i).size() > max){
				max = resList.get(i).size();
			}
		}
		if( max!=0 ){
			for(int i = 0 ; i < resList.size(); i++){
				if(resList.get(i).size()!=0){
					if(singleTopicConstraint){
						if(max == resList.get(i).size()){
							topicSet.add(i);
						}
					}
					else{
						topicSet.add(i);
					}
				}
			}
		}
		
	}

}
