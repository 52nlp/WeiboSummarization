package newHLDA;

import java.util.ArrayList;

import newHLDA.item.HldaTopic;
import newHLDA.item.HldaWord;

public class HLDAUtil {
	 //log情况下采样，需要做归一化处理
	 public static int sampleLog(double[] nodeWeights){
		 double[] weights = new double[nodeWeights.length];
		 double sum = 0.0;
		 double max = Double.NEGATIVE_INFINITY;	
		  
		 for (int i=0; i<nodeWeights.length; i++) {
			if (nodeWeights[i] > max) {
				max = nodeWeights[i];
			}
		 }
		 for (int i=0; i<nodeWeights.length; i++) {
			weights[i] = Math.exp(nodeWeights[i] - max);
			sum += weights[i];
		 }
		 return  Global.random.nextDiscrete(weights, sum);
	 }	
	 
	 public static double logSum(double logA, double logB){
		 if (logA < logB)
		      return(logB+Math.log(1 + Math.exp(logA-logB)));
		  else
		      return(logA+Math.log(1 + Math.exp(logB-logA)));
	 }
	 
	 //测试当前的词，是否属于maxLevel+1层
	 public static boolean newTopicGenerate(double[] stickLength, HldaWord word){
		 double remainLength = 1.0;
		 double[] realStickLength = new double[stickLength.length];

		 
	     ArrayList<HldaTopic> All = new ArrayList<HldaTopic>();
	     Global.rootNode.GetAllTopics(All);
		 double[] levelLikelihood = new double[Global.numLevels];
		 //初始化
		 for(int i = 0 ; i < levelLikelihood.length; i++){
			 levelLikelihood[i] = 0;
		 }
		 //边缘化c， p(w|z,c,w,eta)p(c|c_{-d})
		 for(HldaTopic t: All){
			 int curLevel = t.level;
			 double weight =( Math.exp(t.ncrp))* (t.typeCounts[word.type] + Global.etaList.get(curLevel))
					 / (t.totalTokens + Global.etaList.get(curLevel)*Global.vocalSize)
					 ;
			 levelLikelihood[curLevel] += weight;
		 }
//		 for(double d: levelLikelihood){
//			 System.out.print(d+" ");
//		 }
		 System.out.println();
		 for(int i = 0 ; i < realStickLength.length; i++){
			 realStickLength[i] = Math.exp(stickLength[i]) * levelLikelihood[i];
			 remainLength -= realStickLength[i];
//			 remainLength -= Math.exp(stickLength[i]);
		 }
		 
		 if(remainLength<0){
			 System.err.println("remain stick length error!");
		 }
		 for(int i = 0 ; i < realStickLength.length; i++){
			 if(remainLength<realStickLength[i]){
				 return false;
			 }
		 }
//		 System.out.println("remainLength is:" + remainLength);
		 return true;
		 
		 
	 }
	 
	 public static int[] arrayExtend(int[] oriRes, int length){
			if(length < oriRes.length){
				return oriRes;
			}
			else{
				int[] newRes = new int[length];
				for(int value: newRes){
					value = 0;
				}
				for(int i = 0 ; i < oriRes.length; i++){
					newRes[i] = oriRes[i];
				}
				return newRes;
			}
	}
}
