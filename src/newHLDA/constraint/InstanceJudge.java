package newHLDA.constraint;

import java.util.ArrayList;
import java.util.HashSet;


import ICTCLAS.I3S.AC.SegTagICTCLAS50;

import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;


//判断一个instance中，也就是一个doc中,是否包含某个,或者某些词
public class InstanceJudge {

	public static boolean indexOf = false;
	//是否包含每一个节点的全部关键词
	public static boolean containAll = false;
//	//判断instance中是否包含key
//	public static boolean isContains(Instance instance, String key){
//		boolean flag = false;
//		FeatureSequence fs = (FeatureSequence) instance.getData();
//		for(int i = 0 ; i < fs.getLength(); i++){
//			String curStr = (String)fs.get(i);
//			if(key.length() > 0 && curStr.indexOf(key)!=-1){
//				flag = true;
////				if(flag){
////					System.out.println(key);
////				}
//			}
//		}
//		
//		return flag;
//	}
//	
//	//判断是否包含keyList中的string
//	public static boolean isContains(Instance instance, ArrayList<String> keyList){
//		boolean flag = false;
//		for(String s : keyList){
//			flag = isContains(instance, s);
//			if(flag){
//				return flag;
//			}
//		}
//		return flag;
//	}
//	
//	//判断intances属于的位置
//	public static int getPos(Instance instance,ArrayList<ArrayList<String>> id2topicWord){
//		int pos = -1;
//		for(int i = 0 ; i < id2topicWord.size(); i++){
//			boolean flag = isContains(instance, id2topicWord.get(i));
//			if(flag){
//				if(pos == -1){
//					pos = i;
//				}
//				else{
//					//pos之前已经赋值
//					return -1;
//				}
//			}
//		}
//		return pos;
//	}
	
	//判断一个instance的约束限定结果
	public static InstanceResult getInstanceResults(Instance instance,ArrayList<ArrayList<String>> id2topicWord){
		ArrayList<HashSet<String>> resList = new ArrayList<HashSet<String>>();
		FeatureSequence fs = (FeatureSequence) instance.getData();
		for(int i = 0 ; i < id2topicWord.size(); i++){
			HashSet<String> subResList = new HashSet<String>();
			for(int j = 0 ; j < id2topicWord.get(i).size(); j++){
				String key = id2topicWord.get(i).get(j);
				//判断当前的instance是否包含key
				for(int k = 0 ; k < fs.getLength(); k++){
					String curStr = (String)fs.get(k);
					if(key.length() > 0 ){
						if(indexOf){
							if(curStr.indexOf(key)!=-1){
								subResList.add(key);
							}
						}
						else{
							if(curStr.equals(key)){
								subResList.add(key);
							}
						}
						
					}
				}
			}
			resList.add(subResList);
		}
		return new InstanceResult(resList);
	}
	
	
	//判断一个sentence的约束限定结果
	//每一个节点所包含的关键字
	public static InstanceResult getSentencesResults(String sentences,ArrayList<ArrayList<String>> id2topicWord){
		ArrayList<HashSet<String>> resList = new ArrayList<HashSet<String>>();
		String[] subStr = SegTagICTCLAS50.segTag(sentences).split(" ");
		for(int i = 0 ; i < id2topicWord.size(); i++){
			HashSet<String> subResList = new HashSet<String>();
			for(int j = 0 ; j < id2topicWord.get(i).size(); j++){
				String key = id2topicWord.get(i).get(j);
				//判断当前的instance是否包含key
				for(int k = 0 ; k < subStr.length; k++){
					String curStr;
					if(subStr[k].split("/").length > 1){
						 curStr = subStr[k].split("/")[0];
					}
					else{
						continue;
					}
					if(key.length() > 0 ){
						if(indexOf){
							if(curStr.indexOf(key)!=-1){
								subResList.add(key);
							}
						}
						else{
							if(curStr.equals(key)){
								subResList.add(key);
							}
						}
						
					}
				}
			}
			if(!containAll){
				resList.add(subResList);
			}
			else{
				if(subResList.size() == id2topicWord.get(i).size()){
					resList.add(subResList);
				}
				else{
					subResList.clear();
					resList.add(subResList);
				}
			}
		}
		return new InstanceResult(resList);
	}	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
