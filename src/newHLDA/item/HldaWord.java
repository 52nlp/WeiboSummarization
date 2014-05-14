package newHLDA.item;

import java.util.ArrayList;

import newHLDA.Global;
import newHLDA.HLDAUtil;

import sentiment.dict.StopDict;
import util.mallet.MalletInputFileGenerator;

import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;

//每一个文档的每一个单词的封装
public class HldaWord{
	 //分配的层次
	 public int level;
	 //对应的文档的instance
	 Instance instance;
	 //对应的文档的位置
	 public int index;
	 //对应的空间向量的位置//所在维度
    //type = fs.getIndexAtPosition(index);
	 public int type;
	 //分配到的节点
	 public HldaTopic topic;
	 
	 public String content;
	 
	 public boolean newLevel;
	 //构造函数
	 public HldaWord(Instance instance,int index){
         this.instance = instance;
         this.index = index;
         this.level = -1;
         FeatureSequence fs = (FeatureSequence)instance.getData();
         this.type = fs.getIndexAtPosition(index);
         topic = null;
         newLevel = false;
         content = this.instance.getAlphabet().lookupObject(type).toString();
    }
	 
	 //给当前的词分配至每个结点,也就初始化的时候用
	 //适用于路径分配
	 public void AssignTopic(HldaTopic topic){
        this.topic = topic;
        //当前维度
        topic.typeCounts[this.type]++;
        topic.totalTokens++;
        level = topic.level;
    }
	 
	 //回收,OK
	 //适用于路径回收
	 public void UnassignTopic(){
        topic.typeCounts[this.type]--;
        topic.totalTokens--;
        topic = null;
    }
	  
	 //对当前的Hldaword采样
	 //基于10年的GEM部分
	 public HldaTopic SampleLevelByGEM(HldaDoc doc){
		 int K = doc.path.length;
		 if( K != Global.numLevels){
			 System.err.println("path error!");
			 System.err.println(String.format("path is : %s  numLevels is : %s", K,Global.numLevels));
		 }
		 //相似度
		 double[] wordLikelihood = new double[K];
         double[] stickLength = new double[K];
         double[] stickRemaining = new double[K]; 
        
         //unassigned
        doc.levelCount[this.level]--;      
        doc.f[level][type]--;
        this.topic.typeCounts[this.type]--;
        this.topic.totalTokens--;
        this.topic = null;
        double[] restlevelCountTotal = new double[K];
        //restlevelCountTotal[k] = sum(doc.levelCount[k...K])
        //因此 restlevelCountTotal[k] - restlevelCountTotal[k+1] = doc.levelCount[k]
        for(int i = 0 ; i < K ; i ++){
        	 restlevelCountTotal[i] = 0;
        	 for(int j = i; j < K ; j++){
        		 restlevelCountTotal[i] += doc.levelCount[j];
        	 }
        }
       
        //计算相似度
        for (int k = 0; k < K; k++){
               wordLikelihood[k] = Math.log((doc.path[k].typeCounts[type] + Global.etaList.get(k))
               		/ (doc.path[k].totalTokens + Global.etaList.get(k)*Global.vocalSize));
        }
        
        
        //保存之前的stickLength
        //doc._stickLength[k] = V_{k}
        //doc._stickRemaining = 1 - V_{k}
        double oldLevel = this.level;
        double tmp1 = doc._stickLength[this.level];
        double tmp2 = doc._stickRemaining[this.level];
      //更新
        double tmp = (Global.mpi + doc.levelCount[level]) / (Global.pi + restlevelCountTotal[level]);
        doc._stickLength[level] = Math.log(tmp); // Vk
        doc._stickRemaining[level] = Math.log(1 - tmp); // (1-Vk)
        
        stickLength[0] = doc._stickLength[0];
        stickRemaining[0] = doc._stickRemaining[0];
        
        //stickRemaining[k] = sum((1 - V_{0...k}))
        //stickRemaining[k] = V_{k} + sum((1 - V_{0...k-1}))
        for (int k = 1; k < K; k++){
               stickRemaining[k] = doc._stickRemaining[k] + stickRemaining[k - 1];
               stickLength[k] = doc._stickLength[k] + stickRemaining[k - 1];
        }
        if(Global.NEWLEVEL_DETECT){
            this.newLevel = HLDAUtil.newTopicGenerate(stickLength,this);
        }
        
        for (int k = 0; k < K; k++){
               stickLength[k] = stickLength[k] + wordLikelihood[k];
        }
         
        //不放在根节点是采样前处理
        //中文，非单字不放在根节点
        if(Global.MULTI_WORD_BOTTOM){
        	if(content.length()>1){
        		stickLength[0] = Double.NEGATIVE_INFINITY;
        	}
        }
        
        //中英文,预定义的非功能词
        if(Global.NONFUNTION_WORD_BOTTOM){
        	if(MalletInputFileGenerator.nonFuntionWord.contains(content)){
        		stickLength[0] = Double.NEGATIVE_INFINITY;
        	}
        }
        
        //gibbs采样
        this.level  = HLDAUtil.sampleLog(stickLength);  
        //超过一定比例的单字扔到根节点
        //放置根节点是采样后处理
        //中文
        if(Global.SINGLE_WORD_TOP_RATIO_CONSTRAINT){
        	if(content.length()==1){
        		if(MalletInputFileGenerator.wordNum.containsKey(content)){
        			if( MalletInputFileGenerator.wordNum.get(content) > MalletInputFileGenerator.sensNum * Global.SINGLE_WORD_TOP_RATIO){
        				this.level = 0;
        			}
        		}
        	}
        }
    
        //中英文
        if(Global.FUNTION_WORD_TOP){
        	if(MalletInputFileGenerator.funtionWord.contains(content)){
        		this.level = 0;
        	}
        }
        //assign
        this.topic = doc.path[this.level];
        topic.typeCounts[this.type]++;
        topic.totalTokens++;
        this.level = topic.level;
        doc.levelCount[this.level]++;      
        doc.f[level][type]++;	     
 
        if (level == oldLevel){
            doc._stickLength[this.level] = tmp1;
            doc._stickRemaining[this.level] = tmp2;
       }
       else{
           tmp = (Global.mpi + doc.levelCount[this.level]) / (Global.pi + restlevelCountTotal[level]);
           doc._stickLength[level] = Math.log(tmp);
           doc._stickRemaining[level] = Math.log(1 - tmp);
       }
//        tmp = (Global.mpi + doc.levelCount[level]) / (Global.pi + restlevelCountTotal[level]);
//        doc._stickLength[level] = Math.log(tmp);
//        doc._stickRemaining[level] = Math.log(1 - tmp);
   
        return doc.path[this.level];
	 }
	 
	  
	//对当前的Hldaword采样
    //基于03年的论文
    public HldaTopic SampleLevel(HldaDoc doc){
    	 int K = doc.path.length;
		 if( K != Global.numLevels){
			 System.err.println("path error!");
			 System.err.println(String.format("path is : %s  numLevels is : %s", K,Global.numLevels));
		 }
		 //unassigned
	     doc.levelCount[this.level]--;      
	     doc.f[level][type]--;
	     this.topic.typeCounts[this.type]--;
	     this.topic.totalTokens--;
	     this.topic = null;
		 double[] wordLikelihood = new double[K];
		 double[] topicWeights = new double[K];
		 for (int k = 0; k < K; k++){
              wordLikelihood[k] = (doc.path[k].typeCounts[type] + Global.etaList.get(k))
              		/ (doc.path[k].totalTokens + Global.etaList.get(k)*Global.vocalSize);
         }
	     double sum = 0;
		 for(int k = 0 ; k < K ; k++){
			 topicWeights[k] =  wordLikelihood[k] *(Global.alpha + doc.levelCount[k]);
			 sum +=  topicWeights[k];
		 }
	
		 //采样
		 this.level = Global.random.nextDiscrete(topicWeights,sum);	 
	     //assign
	     this.topic = doc.path[this.level];
	     topic.typeCounts[this.type]++;
	     topic.totalTokens++;
	     this.level = topic.level;
	     doc.levelCount[this.level]++;      
	     doc.f[level][type]++;	 
	     return doc.path[this.level];
    }
		 
}

