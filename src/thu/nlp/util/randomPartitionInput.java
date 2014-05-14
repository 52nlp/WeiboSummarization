package thu.nlp.util;

import java.util.ArrayList;


public class randomPartitionInput {
	public ArrayList<String> inputList;
	public ArrayList<String> trainingList;
	public ArrayList<String> testingList;
	public int ratio; // 原始数据和测试例子的比例
	
	public randomPartitionInput(ArrayList<String> inputList, int ratio){
		this.inputList = inputList;
		this.ratio = ratio;
		this.testingList = new ArrayList<String>();
		this.trainingList = new ArrayList<String>();
		
		for(String s : this.inputList){
			if(Math.random() <= (1.0)/ratio){
				testingList.add(s);
			}
			else{
				trainingList.add(s);
			}
		}
	}
	
	public static void main(String[] args){
		
	}
	
	
}
