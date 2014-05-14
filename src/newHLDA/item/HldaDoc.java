package newHLDA.item;

import java.util.ArrayList;

import newHLDA.Global;
import newHLDA.HLDAUtil;
import newHLDA.NodeDict;
import newHLDA.constraint.InstanceJudge;
import newHLDA.constraint.InstanceResult;

import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;

//输入的每一个文档
public class HldaDoc{
	//输入的mallet的instance
	public Instance instance;
	//当前的文档的路径
	public HldaTopic[] path;
	//词的链表
    public ArrayList<HldaWord> words;
	//每一层的词的数目
    public int[] levelCount;
    //计算用，每一层的每一个词
    //当前文档每一维度的词的数量
    public int[][] f;
    //每一层的主题概率
    public double[] topicProb;
    //限定结果
    public InstanceResult insRes;
    public int dimensions;

    //precompute the stick breaking stuff for faster computation
    public double[] _stickLength;
    public double[] _stickRemaining;
    
    public double score;
    
    public HldaDoc(Instance instance){
   	this.instance = instance;
   	levelCount = new int[Global.numLevels];
    for (int i = 0; i < Global.numLevels; i++){
       	 //每一层为0
       	 levelCount[i] = 0;
    }
    _stickLength = new double[Global.numLevels];
    _stickRemaining = new double[Global.numLevels];
        
    path = new HldaTopic[Global.numLevels];
   //f 和 topicProb 还没有分配
    this.f = null;
    //初始化words
    words = new ArrayList<HldaWord>();
    FeatureSequence fs = (FeatureSequence)instance.getData();
    for(int i = 0 ; i < fs.getLength(); i++){
       words.add(new HldaWord(instance, i));
    }
    score = 0;
    
    if(Global.PATH_INIT_CONSTRAINT||Global.PATH_SAMPLING_CONSTRAINT){
//    	System.out.println(NodeDict.id2topicWord);
    	insRes = InstanceJudge.getInstanceResults(instance, NodeDict.id2topicWord);
    }
    this.dimensions = Global.vocalSize;
   }
    
    public void dimensionsExtend(int newDimensions){
    	if(f!=null && f.length == Global.numLevels){
    		this.dimensions = newDimensions;
    		for(int i = 0 ; i < f.length; i++){
    			f[i] = HLDAUtil.arrayExtend(f[i], newDimensions);
    		}
    	}
    }
    
    //计算属于每一层的概率，没有smooth
    public void CalculateTopicProbability(){
       topicProb = new double[Global.numLevels];
       FeatureSequence fs = (FeatureSequence)instance.getData();
       //对应每一层的概率
       for (int k = 0; k < Global.numLevels; k++){
           topicProb[k] = (double)levelCount[k]/ fs.getLength() ;
       }
     }
     
     //给当前文档分配一条路径
    //如果当前节点是内部节点，则需要构造一条新的路径
    //否则，可以直接分配
     public void AssignPath(HldaTopic curTopic){
   	    //获得当前路径
   	  HldaTopic leafTopic ;
   	  if(!curTopic.isLeaf()){
   		  leafTopic = curTopic.getNewLeaf();
   		  System.out.println("add path");
   	  }
   	  else{
   	     leafTopic = curTopic;
   	  }
   	  //获得当前分配的完整路径
      HldaTopic[] ppath = leafTopic.getPath();
      if(ppath.length!=Global.numLevels){
    	  System.err.println("assigned path error!");
      }
      //对于给定路径，需要分配路径，顾客，以及每一个单词至对应的节点
      //分配至顾客  
      for(HldaTopic t : ppath){
            t.customers++;
      }     
    
      //分配路径
      this.path = ppath;
      
      //分配每个词
      for(HldaWord w : words){
      //update每个词的topic
       w.topic = this.path[w.level];
       w.topic.typeCounts[w.type]++;
       w.topic.totalTokens++;
       }
   }
     
     //对当前文档进行回收
     public void UnassignPath(){
   	  //叶子节点回收路径
          this.path[this.path.length-1].dropPath();
          //每个单词回收
          for(HldaWord w : words){
        	  w.topic.typeCounts[w.type]--;
        	  w.topic.totalTokens--;
        	  w.topic = null;
          }
     }
     
     //路径检查
     public void pathVertify(){
    	 for(int i = 0 ; i < path.length; i++){
    		 if(path[i].level!=i){
    			 System.err.println("doc path error!");
    		 }
    	 }
     }

}